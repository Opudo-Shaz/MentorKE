package app.action;

import app.bean.MessageBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.Message;
import app.model.Mentor;
import app.model.Mentee;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Messaging - Handles messaging between mentors and mentees
 * URL: /messaging
 * Actions: conversation, send, mark-read, unread-count
 */
@WebServlet(name = "Messaging", urlPatterns = {"/messaging"})
public class Messaging extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(Messaging.class);

    @Inject
    private MessageBean messageBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        if (!isLoggedIn(request)) {
            redirect(response, "login");
            return;
        }

        String userId = getUserId(request);

        try {
            if ("conversation".equalsIgnoreCase(action)) {
                handleConversation(request, response, userId);
            } else if ("unread-count".equalsIgnoreCase(action)) {
                handleUnreadCount(request, response, userId);
            } else if ("list-conversations".equalsIgnoreCase(action)) {
                handleListConversations(request, response, userId);
            } else {
                handleListConversations(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error in MessagingAction", e);
            setAttribute(request, "errorMessage", "An error occurred: " + e.getMessage());
            try {
                forward(request, response, "/mentee-dashboard.jsp");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        if (!isLoggedIn(request)) {
            redirect(response, "login");
            return;
        }

        String userId = getUserId(request);

        try {
            if ("send-message".equalsIgnoreCase(action)) {
                handleSendMessage(request, response, userId);
            } else if ("mark-read".equalsIgnoreCase(action)) {
                handleMarkAsRead(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error processing messaging action", e);
            setAttribute(request, "errorMessage", "An error occurred: " + e.getMessage());
            try {
                forward(request, response, "/mentee-dashboard.jsp");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
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
