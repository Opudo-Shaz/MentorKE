package app.dao;

import app.model.Message;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Dependent
public class MessageDAO extends GenericDAO<Message, Long> {

    public MessageDAO() {
        super(Message.class);
    }

    // Add message
    public void addMessage(Message message) {
        save(message);
    }

    // Find message by id
    public Message getMessage(String id) {
        return findById(Long.parseLong(id));
    }

    // Find all messages
    public List<Message> getAllMessages() {
        return findAll();
    }

    // Update message
    public void updateMessage(String id, Message message) {
        message.setId(Long.parseLong(id));
        update(message);
    }

    // Delete message
    public void deleteMessage(String id) {
        delete(Long.parseLong(id));
    }

    // Get total number of messages
    public int getTotalMessages() {
        return count();
    }

    // Get conversation between two users
    public List<Message> getConversation(String userId1, String userId2) {
        String jpql = "SELECT m FROM Message m WHERE " +
                     "(m.senderId = :userId1 AND m.recipientId = :userId2) OR " +
                     "(m.senderId = :userId2 AND m.recipientId = :userId1) " +
                     "ORDER BY m.createdAt ASC";
        TypedQuery<Message> query = entityManager.createQuery(jpql, Message.class);
        query.setParameter("userId1", userId1);
        query.setParameter("userId2", userId2);
        return query.getResultList();
    }

    // Get unread messages for a user
    public List<Message> getUnreadMessages(String userId) {
        String jpql = "SELECT m FROM Message m WHERE m.recipientId = :userId AND m.isRead = false " +
                     "ORDER BY m.createdAt DESC";
        TypedQuery<Message> query = entityManager.createQuery(jpql, Message.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    // Get unread message count for a user
    public int getUnreadMessageCount(String userId) {
        String jpql = "SELECT COUNT(m) FROM Message m WHERE m.recipientId = :userId AND m.isRead = false";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("userId", userId);
        return ((Long) query.getSingleResult()).intValue();
    }

    // Mark message as read
    public void markAsRead(String messageId) {
        Message message = findById(Long.parseLong(messageId));
        if (message != null) {
            message.setIsRead(true);
            update(message);
        }
    }

    // Mark all messages in conversation as read
    public void markConversationAsRead(String userId1, String userId2) {
        String jpql = "UPDATE Message m SET m.isRead = true WHERE m.recipientId = :userId1 AND m.senderId = :userId2";
        entityManager.createQuery(jpql).setParameter("userId1", userId1).setParameter("userId2", userId2).executeUpdate();
    }

    // Get recent conversations (distinct pairs)
    public List<Message> getRecentConversations(String userId) {
        String jpql = "SELECT m FROM Message m WHERE m.senderId = :userId OR m.recipientId = :userId " +
                     "ORDER BY m.createdAt DESC";
        TypedQuery<Message> query = entityManager.createQuery(jpql, Message.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
