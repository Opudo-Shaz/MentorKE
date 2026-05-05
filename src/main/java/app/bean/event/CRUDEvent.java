package app.bean.event;


public class CRUDEvent {
    private final String entityType;
    private final String entityId;
    private final String operation;
    private final String userId;
    private final String details;

    public CRUDEvent(String entityType, String entityId, String operation, String userId, String details) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.operation = operation;
        this.userId = userId;
        this.details = details;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getOperation() {
        return operation;
    }

    public String getUserId() {
        return userId;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "CRUDEvent{" +
                "entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", operation='" + operation + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}

