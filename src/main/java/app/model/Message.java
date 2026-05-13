package app.model;

import java.io.Serial;
import java.io.Serializable;
import app.framework.DbTable;
import app.framework.DbColumn;

@DbTable(name = "messages")
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @DbColumn(name = "id", type = "SERIAL", primaryKey = true, autoIncrement = true)
    private String id;

    @DbColumn(name = "sender_id", type = "VARCHAR(50)", notNull = true)
    private String senderId;

    @DbColumn(name = "recipient_id", type = "VARCHAR(50)", notNull = true)
    private String recipientId;

    @DbColumn(name = "message", type = "TEXT", notNull = true)
    private String message;

    @DbColumn(name = "is_read", type = "BOOLEAN", defaultValue = "false")
    private Boolean isRead;

    @DbColumn(name = "created_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long createdAt;

    // Constructors
    public Message() {
    }

    public Message(String senderId, String recipientId, String message) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.message = message;
        this.isRead = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}
