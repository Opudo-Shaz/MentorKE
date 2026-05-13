package app.dao;

import app.dbconnection.DataSourceHelper;
import app.model.Message;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class MessageDAO extends GenericDAO<Message, String> {

    @Inject
    public MessageDAO(DataSourceHelper dataSourceHelper) {
        super(Message.class, dataSourceHelper);
    }

    // Add message
    public void addMessage(Message message) throws SQLException {
        save(message);
    }

    // Find message by id
    public Message getMessage(String id) throws SQLException {
        return findById(id);
    }

    // Find all messages
    public List<Message> getAllMessages() throws SQLException {
        return findAll();
    }

    // Update message
    public void updateMessage(String id, Message message) throws SQLException {
        message.setId(id);
        update(message);
    }

    // Delete message
    public void deleteMessage(String id) throws SQLException {
        delete(id);
    }

    // Get total number of messages
    public int getTotalMessages() throws SQLException {
        return count();
    }

    // Get conversation between two users
    public List<Message> getConversation(String userId1, String userId2) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE " +
                    "(sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?) " +
                    "ORDER BY created_at ASC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId1);
            stmt.setString(2, userId2);
            stmt.setString(3, userId2);
            stmt.setString(4, userId1);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting conversation", e);
        }
        return messages;
    }

    // Get unread messages for a user
    public List<Message> getUnreadMessages(String userId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE recipient_id = ? AND is_read = false " +
                    "ORDER BY created_at DESC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting unread messages", e);
        }
        return messages;
    }

    // Get unread message count for a user
    public int getUnreadMessageCount(String userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM messages WHERE recipient_id = ? AND is_read = false";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting unread message count", e);
        }
        return 0;
    }

    // Mark message as read
    public void markAsRead(String messageId) throws SQLException {
        String sql = "UPDATE messages SET is_read = true WHERE id = ?";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageId);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("Error marking message as read", e);
        }
    }

    // Mark all messages in conversation as read
    public void markConversationAsRead(String userId1, String userId2) throws SQLException {
        String sql = "UPDATE messages SET is_read = true WHERE recipient_id = ? AND sender_id = ?";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId1);
            stmt.setString(2, userId2);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("Error marking conversation as read", e);
        }
    }

    // Get recent conversations (distinct pairs)
    public List<Message> getRecentConversations(String userId) throws SQLException {
        List<Message> conversations = new ArrayList<>();
        String sql = "SELECT DISTINCT ON (CASE WHEN sender_id = ? THEN recipient_id ELSE sender_id END) * " +
                    "FROM messages WHERE sender_id = ? OR recipient_id = ? " +
                    "ORDER BY CASE WHEN sender_id = ? THEN recipient_id ELSE sender_id END, created_at DESC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, userId);
            stmt.setString(3, userId);
            stmt.setString(4, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    conversations.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting recent conversations", e);
        }
        return conversations;
    }
}
