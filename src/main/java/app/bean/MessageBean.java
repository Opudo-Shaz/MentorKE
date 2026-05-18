package app.bean;

import app.dao.MessageDAO;
import app.model.Message;
import app.utility.logging.AppLogger;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.List;


@Stateless
@Named("messageBean")
public class MessageBean {

    private static final Logger logger = AppLogger.getLogger(MessageBean.class);

    @Inject
    private MessageDAO messageDAO;

    public MessageBean() {
        logger.debug("CDI Bean initialized with default constructor");
    }

    /**
     * Send a message from one user to another
     */
    public void sendMessage(String senderId, String recipientId, String messageText) throws SQLException {
        logger.info("Sending message from {} to {}", senderId, recipientId);

        Message message = new Message(senderId, recipientId, messageText);
        messageDAO.addMessage(message);

        logger.info("Message sent successfully");
    }

    /**
     * Get conversation between two users
     */
    public List<Message> getConversation(String userId1, String userId2) throws SQLException {
        logger.info("Retrieving conversation between {} and {}", userId1, userId2);
        return messageDAO.getConversation(userId1, userId2);
    }

    /**
     * Get unread messages for a user
     */
    public List<Message> getUnreadMessages(String userId) throws SQLException {
        logger.info("Getting unread messages for user: {}", userId);
        return messageDAO.getUnreadMessages(userId);
    }

    /**
     * Get unread message count for a user
     */
    public int getUnreadMessageCount(String userId) throws SQLException {
        logger.info("Getting unread message count for user: {}", userId);
        return messageDAO.getUnreadMessageCount(userId);
    }

    /**
     * Mark a specific message as read
     */
    public void markMessageAsRead(String messageId) throws SQLException {
        logger.info("Marking message as read: {}", messageId);
        messageDAO.markAsRead(messageId);
    }

    /**
     * Mark all messages in a conversation as read
     */
    public void markConversationAsRead(String userId1, String userId2) throws SQLException {
        logger.info("Marking conversation as read between {} and {}", userId1, userId2);
        messageDAO.markConversationAsRead(userId1, userId2);
    }

    /**
     * Get a single message by ID
     */
    public Message getMessage(String messageId) throws SQLException {
        return messageDAO.getMessage(messageId);
    }

    /**
     * Delete a message
     */
    public void deleteMessage(String messageId) throws SQLException {
        logger.info("Deleting message: {}", messageId);
        messageDAO.deleteMessage(messageId);
    }

    /**
     * Get recent conversations (distinct message threads)
     */
    public List<Message> getRecentConversations(String userId) throws SQLException {
        logger.info("Getting recent conversations for user: {}", userId);
        return messageDAO.getRecentConversations(userId);
    }

    /**
     * Get total unread messages for a user
     */
    public int getTotalUnreadMessages(String userId) throws SQLException {
        return messageDAO.getUnreadMessageCount(userId);
    }
}
