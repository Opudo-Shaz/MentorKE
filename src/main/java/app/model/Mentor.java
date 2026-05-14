package app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentors")
public class Mentor implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "expertise", columnDefinition = "TEXT")
    private String expertise;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "qualifications", columnDefinition = "TEXT")
    private String qualifications;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "status", length = 50)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Mentor(Long id, String userId, String specialization, String expertise,
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
    }

    public Mentor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
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
