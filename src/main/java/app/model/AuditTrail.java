package app.model;

import app.framework.DbTable;
import app.framework.DbColumn;
import java.io.Serial;
import java.io.Serializable;

@DbTable(name = "audit_trail")
public class AuditTrail implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @DbColumn(name = "id", type = "SERIAL", primaryKey = true, autoIncrement = true)
    private String id;

    @DbColumn(name = "entity_type", type = "VARCHAR(50)", notNull = true)
    private String entityType;

    @DbColumn(name = "entity_id", type = "VARCHAR(100)", notNull = true)
    private String entityId;

    @DbColumn(name = "operation", type = "VARCHAR(20)", notNull = true)
    private String operation;

    @DbColumn(name = "user_id", type = "VARCHAR(100)")
    private String userId;

    @DbColumn(name = "details", type = "LONGTEXT")
    private String details;

    @DbColumn(name = "timestamp", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long timestamp;

    public AuditTrail() {
        this.timestamp = System.currentTimeMillis();
    }

    public AuditTrail(String entityType, String entityId, String operation, String userId, String details) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.operation = operation;
        this.userId = userId;
        this.details = details;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AuditTrail{" +
                "id='" + id + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", operation='" + operation + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

