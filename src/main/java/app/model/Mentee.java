package app.model;

import java.io.Serial;
import java.io.Serializable;
import app.framework.DbTable;
import app.framework.DbColumn;


@DbTable(name = "mentees")
public class Mentee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @DbColumn(name = "id", type = "SERIAL", primaryKey = true, autoIncrement = true)
    private String id;

    @DbColumn(name = "user_id", type = "VARCHAR(50)", notNull = true)
    private String userId;

    @DbColumn(name = "education_level", type = "VARCHAR(100)")
    private String educationLevel;

    @DbColumn(name = "field_of_study", type = "VARCHAR(100)")
    private String fieldOfStudy;

    @DbColumn(name = "learning_goals", type = "TEXT")
    private String learningGoals;

    @DbColumn(name = "phone_number", type = "VARCHAR(20)")
    private String phoneNumber;

    @DbColumn(name = "mentor_id", type = "VARCHAR(50)")
    private String mentorId;

    @DbColumn(name = "status", type = "VARCHAR(50)", defaultValue = "'Active'")
    private String status;

    @DbColumn(name = "created_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long createdAt;

    @DbColumn(name = "updated_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long updatedAt;

    public Mentee(String id, String userId, String educationLevel, String fieldOfStudy,
                  String learningGoals, String phoneNumber, String mentorId, String status) {
        this.id = id;
        this.userId = userId;
        this.educationLevel = educationLevel;
        this.fieldOfStudy = fieldOfStudy;
        this.learningGoals = learningGoals;
        this.phoneNumber = phoneNumber;
        this.mentorId = mentorId;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Mentee() {

    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getLearningGoals() {
        return learningGoals;
    }

    public void setLearningGoals(String learningGoals) {
        this.learningGoals = learningGoals;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMentorId() {
        return mentorId;
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
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
        return "Mentee{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", educationLevel='" + educationLevel + '\'' +
                ", fieldOfStudy='" + fieldOfStudy + '\'' +
                ", learningGoals='" + learningGoals + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", mentorId='" + mentorId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

