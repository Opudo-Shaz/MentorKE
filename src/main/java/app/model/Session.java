package app.model;

import java.io.Serial;
import java.io.Serializable;
import app.framework.DbTable;
import app.framework.DbColumn;

@DbTable(name = "sessions")
public class Session implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @DbColumn(name = "id", type = "SERIAL", primaryKey = true, autoIncrement = true)
    private String id;

    @DbColumn(name = "mentor_id", type = "VARCHAR(50)", notNull = true)
    private String mentorId;

    @DbColumn(name = "mentee_id", type = "VARCHAR(50)", notNull = true)
    private String menteeId;

    @DbColumn(name = "session_link", type = "VARCHAR(255)")
    private String sessionLink;

    @DbColumn(name = "scheduled_date", type = "BIGINT")
    private long scheduledDate;

    @DbColumn(name = "duration_minutes", type = "INTEGER")
    private Integer durationMinutes;

    @DbColumn(name = "status", type = "VARCHAR(50)", defaultValue = "'PENDING'")
    private String status;

    @DbColumn(name = "topic", type = "TEXT")
    private String topic;

    @DbColumn(name = "notes", type = "TEXT")
    private String notes;

    @DbColumn(name = "created_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long createdAt;

    @DbColumn(name = "updated_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long updatedAt;

    // Constructors
    public Session() {
    }

    public Session(String mentorId, String menteeId, long scheduledDate, 
                   Integer durationMinutes, String topic) {
        this.mentorId = mentorId;
        this.menteeId = menteeId;
        this.scheduledDate = scheduledDate;
        this.durationMinutes = durationMinutes;
        this.topic = topic;
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

    public String getMentorId() {
        return mentorId;
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
    }

    public String getMenteeId() {
        return menteeId;
    }

    public void setMenteeId(String menteeId) {
        this.menteeId = menteeId;
    }

    public String getSessionLink() {
        return sessionLink;
    }

    public void setSessionLink(String sessionLink) {
        this.sessionLink = sessionLink;
    }

    public long getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(long scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        return "Session{" +
                "id='" + id + '\'' +
                ", mentorId='" + mentorId + '\'' +
                ", menteeId='" + menteeId + '\'' +
                ", scheduledDate=" + scheduledDate +
                ", status='" + status + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
