package app.action;

import app.bean.MessageBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.Message;
import jakarta.inject.Inject;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import jakarta.enterprise.context.ApplicationScoped;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.framework.ActionPostMethod;

import java.util.List;

/**
 * Messaging - framework action handling messaging features.
 */
@ApplicationScoped
@Action(value = "messaging", label = "Messaging", showLink = false)
public class Messaging extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(Messaging.class);

    @Inject
    private MessageBean messageBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @ActionGetMethod("conversation")
    public void conversation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleConversation(request, response, userId);
    }

    @ActionGetMethod("unread-count")
    public void unreadCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleUnreadCount(request, response, userId);
    }

    @ActionGetMethod("list-conversations")
    public void listConversations(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleListConversations(request, response, userId);
    }

    @ActionGetMethod("")
    public void defaultGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        listConversations(request, response);
    }

    @ActionPostMethod("send-message")
    public void sendMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleSendMessage(request, response, userId);
    }

    @ActionPostMethod("mark-read")
    public void markRead(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleMarkAsRead(request, response, userId);
    }

    /**
     * View conversation between two users
     */
    private void handleConversation(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        String otherUserId = request.getParameter("userId");
        logger.info("User {} viewing conversation with {}", userId, otherUserId);

        try {
            if (otherUserId == null || otherUserId.isEmpty()) {
                setAttribute(request, "errorMessage", "User ID not provided");
                handleListConversations(request, response, userId);
                return;
            }

            List<Message> messages = messageBean.getConversation(userId, otherUserId);
            messageBean.markConversationAsRead(userId, otherUserId);

            Object otherUser = null;
            try {
                otherUser = mentorBean.getMentorById(otherUserId);
                if (otherUser == null) {
                    otherUser = menteeBean.getMenteeById(otherUserId);
                }
            } catch (Exception e) {
                logger.warn("Could not load other user details");
            }

            setAttribute(request, "messages", messages);
            setAttribute(request, "otherUserId", otherUserId);
            setAttribute(request, "otherUser", otherUser);
            forward(request, response, "/conversation.jsp");

        } catch (Exception e) {
            logger.error("Error retrieving conversation", e);
            throw new ServletException(e);
        }
    }

    /**
     * Send a message
     */
    private void handleSendMessage(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        String recipientId = request.getParameter("recipientId");
        String messageText = request.getParameter("message");

        logger.info("User {} sending message to {}", userId, recipientId);

        try {
            if (messageText == null || messageText.trim().isEmpty()) {
                setAttribute(request, "errorMessage", "Message cannot be empty");
                handleConversation(request, response, userId);
                return;
            }

            messageBean.sendMessage(userId, recipientId, messageText.trim());
            logger.info("Message sent successfully");
            handleConversation(request, response, userId);

        } catch (Exception e) {
            logger.error("Error sending message", e);
            setAttribute(request, "errorMessage", "Error sending message: " + e.getMessage());
            handleConversation(request, response, userId);
        }
    }

    /**
     * Mark a message as read
     */
    private void handleMarkAsRead(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        String messageId = request.getParameter("messageId");

        try {
            Message message = messageBean.getMessage(messageId);
            
            if (message != null && message.getRecipientId().equals(userId)) {
                messageBean.markMessageAsRead(messageId);
                logger.info("Message {} marked as read", messageId);
            }

            String returnTo = request.getParameter("returnTo");
            if ("conversation".equals(returnTo)) {
                String otherUserId = request.getParameter("userId");
                handleConversation(request, response, userId);
            } else {
                response.getWriter().write("{\"success\": true}");
            }

        } catch (Exception e) {
            logger.error("Error marking message as read", e);
            response.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Get unread message count for the user
     */
    private void handleUnreadCount(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws IOException {
        
        try {
            int count = messageBean.getUnreadMessageCount(userId);
            response.setContentType("application/json");
            response.getWriter().write("{\"unreadCount\": " + count + "}");
            logger.debug("Unread message count for user {}: {}", userId, count);

        } catch (Exception e) {
            logger.error("Error getting unread message count", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * List all conversations for the user
     */
    private void handleListConversations(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        logger.info("User {} viewing message inbox", userId);

        try {
            List<Message> recentConversations = messageBean.getRecentConversations(userId);
            int unreadCount = messageBean.getUnreadMessageCount(userId);

            setAttribute(request, "conversations", recentConversations);
            setAttribute(request, "unreadCount", unreadCount);
            forward(request, response, "/message-inbox.jsp");

        } catch (Exception e) {
            logger.error("Error retrieving conversations", e);
            throw new ServletException(e);
        }
    }
}
