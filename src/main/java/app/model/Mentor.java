package app.model;

import java.io.Serial;
import java.io.Serializable;

import app.framework.DbTable;
import app.framework.DbColumn;




@DbTable(name = "mentors")
public class Mentor implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @DbColumn(name = "id", type = "SERIAL", primaryKey = true, autoIncrement = true)
    private String id;

    @DbColumn(name = "user_id", type = "VARCHAR(50)", notNull = true)
    private String userId;

    @DbColumn(name = "specialization", type = "VARCHAR(100)")
    private String specialization;

    @DbColumn(name = "expertise", type = "TEXT")
    private String expertise;

    @DbColumn(name = "years_of_experience", type = "INTEGER")
    private Integer yearsOfExperience;

    @DbColumn(name = "bio", type = "TEXT")
    private String bio;

    @DbColumn(name = "qualifications", type = "TEXT")
    private String qualifications;

    @DbColumn(name = "phone_number", type = "VARCHAR(20)")
    private String phoneNumber;

    @DbColumn(name = "status", type = "VARCHAR(50)", defaultValue = "'Active'")
    private String status;

    @DbColumn(name = "created_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long createdAt;

    @DbColumn(name = "updated_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long updatedAt;

    public Mentor(String id, String userId, String specialization, String expertise,
                  Integer yearsOfExperience, String bio, String qualifications,
                  String phoneNumber, String status) {
        this.id = id;
        this.userId = userId;
        this.specialization = specialization;
        this.expertise = expertise;
        this.yearsOfExperience = yearsOfExperience;
        this.bio = bio;
        this.qualifications = qualifications;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Mentor() {

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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
        return "Mentor{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", specialization='" + specialization + '\'' +
                ", expertise='" + expertise + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", bio='" + bio + '\'' +
                ", qualifications='" + qualifications + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

