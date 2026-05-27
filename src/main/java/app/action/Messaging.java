package app.action;

import app.bean.MessageBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.Message;
import app.model.Mentor;
import app.model.Mentee;
import app.security.MentorKeSecurity;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
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
import app.websocket.ChatRoomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Action(value = "messaging", label = "Messaging")
@RolesAllowed({"mentor","mentee","admin"})
public class Messaging extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(Messaging.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Inject
    private MessageBean messageBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @Inject
    private MentorKeSecurity security;

    @ActionGetMethod("conversation")
    @RolesAllowed({"mentor", "mentee"})
    public void conversation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        listConversations(request, response);
    }

    @ActionGetMethod("unread-count")
    @RolesAllowed({"mentor", "mentee"})
    public void unreadCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireAuthentication();
        String userId = getUserId(request);
        handleUnreadCount(request, response, userId);
    }

    @ActionGetMethod("list-conversations")
    @RolesAllowed({"mentor", "mentee"})
    public void listConversations(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireAuthentication();
        String userId = getUserId(request);
        handleListConversations(request, response, userId);
    }

    @ActionGetMethod("")
    @RolesAllowed({"mentor", "mentee"})
    public void defaultGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        listConversations(request, response);
    }

    @ActionPostMethod("send-message")
    public void sendMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = getUserId(request);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(response, Map.of("success", false, "error", "Not authenticated"));
            return;
        }

        handleSendMessage(request, response, userId);
    }

    @ActionPostMethod("mark-read")
    public void markRead(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = getUserId(request);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(response, Map.of("success", false, "error", "Not authenticated"));
            return;
        }

        handleMarkAsRead(request, response, userId);
    }

    /**
     * View conversation between two users
     */
    private void handleConversation(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        String otherUserId = request.getParameter("userId");
        String currentRole = getUserRole(request);
        logger.info("User {} viewing conversation with {}", userId, otherUserId);

        try {
            if (otherUserId == null || otherUserId.isEmpty()) {
                setAttribute(request, "errorMessage", "User ID not provided");
                handleListConversations(request, response, userId);
                return;
            }

            List<Message> messages = messageBean.getConversation(userId, otherUserId);
            messageBean.markConversationAsRead(userId, otherUserId);

            Object otherUser = resolvePartnerEntity(otherUserId, currentRole);

            setAttribute(request, "messages", messages);
            setAttribute(request, "otherUserId", otherUserId);
            setAttribute(request, "otherUser", otherUser);
            setAttribute(request, "selectedPartnerId", otherUserId);
            setAttribute(request, "selectedPartner", otherUser);
            setAttribute(request, "roomId", resolveRoomId(userId, otherUserId));
            forward(request, response, "/message-inbox.jsp");

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
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJson(response, Map.of("success", false, "error", "Message cannot be empty"));
                return;
            }

            Message savedMessage = messageBean.sendMessage(userId, recipientId, messageText.trim());
            String roomId = resolveRoomId(userId, recipientId);

            Map<String, Object> payload = new HashMap<>();
            payload.put("success", true);
            payload.put("roomId", roomId);
            payload.put("message", messageToMap(savedMessage));

            writeJson(response, payload);
            logger.info("Message sent successfully");

        } catch (Exception e) {
            logger.error("Error sending message", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, Map.of("success", false, "error", "Error sending message: " + e.getMessage()));
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
                handleConversation(request, response, userId);
            } else {
                writeJson(response, Map.of("success", true));
            }

        } catch (Exception e) {
            logger.error("Error marking message as read", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, Map.of("success", false, "error", e.getMessage()));
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
            String currentRole = getUserRole(request);

            String partnerId = request.getParameter("partnerId");
            if ((partnerId == null || partnerId.trim().isEmpty()) && recentConversations != null && !recentConversations.isEmpty()) {
                Message firstConversation = recentConversations.get(0);
                partnerId = userId.equals(String.valueOf(firstConversation.getSenderId()))
                        ? String.valueOf(firstConversation.getRecipientId())
                        : String.valueOf(firstConversation.getSenderId());
            }

            if (partnerId == null || partnerId.trim().isEmpty()) {
                partnerId = resolveDefaultChatPartner(userId);
            }

            Object selectedPartner = null;
            String selectedPartnerName = null;
            String selectedPartnerRole = null;
            List<Message> conversationMessages = null;
            String roomId = null;

            List<Map<String, Object>> conversationSummaries = new java.util.ArrayList<>();
            java.util.Set<String> seenPartnerIds = new java.util.LinkedHashSet<>();

            if (recentConversations != null) {
                for (Message message : recentConversations) {
                    String candidatePartnerId = userId.equals(String.valueOf(message.getSenderId()))
                            ? String.valueOf(message.getRecipientId())
                            : String.valueOf(message.getSenderId());

                    if (!seenPartnerIds.add(candidatePartnerId)) {
                        continue;
                    }

                    Object partnerEntity = resolvePartnerEntity(candidatePartnerId, currentRole);
                    String partnerName = resolvePartnerName(partnerEntity, candidatePartnerId);
                    String partnerRole = resolvePartnerRole(partnerEntity, currentRole);

                    Map<String, Object> summary = new HashMap<>();
                    summary.put("partnerId", candidatePartnerId);
                    summary.put("partnerName", partnerName);
                    summary.put("partnerRole", partnerRole);
                    summary.put("lastMessage", message.getMessage());
                    summary.put("createdAt", message.getCreatedAt());
                    summary.put("selected", candidatePartnerId.equals(partnerId));
                    conversationSummaries.add(summary);
                }
            }

            if (partnerId != null && !partnerId.trim().isEmpty()) {
                conversationMessages = messageBean.getConversation(userId, partnerId);
                roomId = resolveRoomId(userId, partnerId);
                selectedPartner = resolvePartnerEntity(partnerId, currentRole);
                selectedPartnerName = resolvePartnerName(selectedPartner, partnerId);
                selectedPartnerRole = resolvePartnerRole(selectedPartner, currentRole);
            }

            setAttribute(request, "conversations", recentConversations);
            setAttribute(request, "conversationSummaries", conversationSummaries);
            setAttribute(request, "unreadCount", unreadCount);
            setAttribute(request, "selectedPartnerId", partnerId);
            setAttribute(request, "selectedPartner", selectedPartner);
            setAttribute(request, "selectedPartnerName", selectedPartnerName);
            setAttribute(request, "selectedPartnerRole", selectedPartnerRole);
            setAttribute(request, "messages", conversationMessages);
            setAttribute(request, "roomId", roomId);
            forward(request, response, "/message-inbox.jsp");

        } catch (Exception e) {
            logger.error("Error retrieving conversations", e);
            throw new ServletException(e);
        }
    }

    private void writeJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(data));
    }

    private Map<String, Object> messageToMap(Message message) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", message.getId());
        map.put("senderId", message.getSenderId());
        map.put("recipientId", message.getRecipientId());
        map.put("message", message.getMessage());
        map.put("isRead", message.getIsRead());
        map.put("createdAt", message.getCreatedAt() != null ? message.getCreatedAt().toString() : null);
        return map;
    }


    // helper methods
    private String resolveRoomId(String userId1, String userId2) {
        return ChatRoomUtil.getRoomId(userId1, userId2);
    }

    private String resolveDefaultChatPartner(String userId) {
        try {
            if (userId == null) {
                return null;
            }

            Mentee mentee = menteeBean.getByUserId(userId);
            if (mentee != null && mentee.getMentorId() != null && !mentee.getMentorId().isBlank()) {
                return mentee.getMentorId();
            }

            Mentor mentor = mentorBean.getByUserId(userId);
            if (mentor != null) {
                List<Mentee> mentees = menteeBean.findByMentorId(String.valueOf(mentor.getId()));
                if (mentees != null && !mentees.isEmpty() && mentees.get(0) != null && mentees.get(0).getId() != null) {
                    return String.valueOf(mentees.get(0).getId());
                }
            }
        } catch (Exception e) {
            logger.warn("Could not resolve default chat partner for user {}", userId, e);
        }

        return null;
    }

    private Object resolvePartnerEntity(String partnerId, String currentRole) throws Exception {
        if (partnerId == null || partnerId.isBlank()) {
            return null;
        }

        if ("mentor".equalsIgnoreCase(currentRole)) {
            return menteeBean.getById(partnerId);
        }

        if ("mentee".equalsIgnoreCase(currentRole)) {
            return mentorBean.getById(partnerId);
        }

        Mentor mentor = mentorBean.getById(partnerId);
        if (mentor != null) {
            return mentor;
        }
        return menteeBean.getById(partnerId);
    }

    private String resolvePartnerName(Object partnerEntity, String fallbackId) {
        if (partnerEntity instanceof Mentor mentor && mentor.getUsername() != null) {
            return mentor.getUsername();
        }
        if (partnerEntity instanceof Mentee mentee && mentee.getUsername() != null) {
            return mentee.getUsername();
        }
        return "User #" + fallbackId;
    }

    private String resolvePartnerRole(Object partnerEntity, String currentRole) {
        if (partnerEntity instanceof Mentor) {
            return "mentor";
        }
        if (partnerEntity instanceof Mentee) {
            return "mentee";
        }
        return "mentor".equalsIgnoreCase(currentRole) ? "mentee" : "mentor";
    }
}
