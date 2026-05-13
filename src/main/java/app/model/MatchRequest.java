package app.model;

import java.io.Serial;
import java.io.Serializable;
import app.framework.DbTable;
import app.framework.DbColumn;

@DbTable(name = "match_requests")
public class MatchRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @DbColumn(name = "id", type = "SERIAL", primaryKey = true, autoIncrement = true)
    private String id;

    @DbColumn(name = "mentee_id", type = "VARCHAR(50)", notNull = true)
    private String menteeId;

    @DbColumn(name = "mentor_id", type = "VARCHAR(50)")
    private String mentorId;

    @DbColumn(name = "requested_specialization", type = "VARCHAR(100)")
    private String requestedSpecialization;

    @DbColumn(name = "status", type = "VARCHAR(50)", defaultValue = "'PENDING'")
    private String status;

    @DbColumn(name = "created_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long createdAt;

    @DbColumn(name = "updated_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long updatedAt;

    // Constructors
    public MatchRequest() {
    }

    public MatchRequest(String menteeId, String mentorId, String requestedSpecialization) {
        this.menteeId = menteeId;
        this.mentorId = mentorId;
        this.requestedSpecialization = requestedSpecialization;
        this.status = "PENDING";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenteeId() {
        return menteeId;
    }

    public void setMenteeId(String menteeId) {
        this.menteeId = menteeId;
    }

    public String getMentorId() {
        return mentorId;
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
    }

    public String getRequestedSpecialization() {
        return requestedSpecialization;
    }

    public void setRequestedSpecialization(String requestedSpecialization) {
        this.requestedSpecialization = requestedSpecialization;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "MatchRequest{" +
                "id='" + id + '\'' +
                ", menteeId='" + menteeId + '\'' +
                ", mentorId='" + mentorId + '\'' +
                ", requestedSpecialization='" + requestedSpecialization + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
