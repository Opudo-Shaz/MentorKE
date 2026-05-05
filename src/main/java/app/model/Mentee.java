package app.model;

import java.io.Serial;
import java.io.Serializable;

public class Mentee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String educationLevel;
    private String fieldOfStudy;
    private String learningGoals;
    private String phoneNumber;
    private String mentorId;
    private String status;
    private long createdAt;
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

