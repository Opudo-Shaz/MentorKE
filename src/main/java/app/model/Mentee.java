package app.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentees")
public class Mentee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @Column(name = "education_level", length = 100)
    private String educationLevel;

    @Column(name = "field_of_study", length = 100)
    private String fieldOfStudy;

    @Column(name = "learning_goals", columnDefinition = "TEXT")
    private String learningGoals;

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

    public Mentee(Long id, Long userId, String educationLevel, String fieldOfStudy,
                  String learningGoals, String phoneNumber, String mentorId, String status) {
        this.id = id;
        setUserId(userId);
        this.educationLevel = educationLevel;
        this.fieldOfStudy = fieldOfStudy;
        this.learningGoals = learningGoals;
        this.phoneNumber = phoneNumber;
        setMentorId(mentorId);
        this.status = status;
    }

    public Mentee() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Transient
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public void setUserId(Long userId) {
        if (userId == null) {
            this.user = null;
            return;
        }

        User reference = new User();
        reference.setId(userId);
        this.user = reference;
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

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    @Transient
    public String getMentorId() {
        return mentor != null && mentor.getId() != null ? String.valueOf(mentor.getId()) : null;
    }

    public void setMentorId(String mentorId) {
        if (mentorId == null || mentorId.trim().isEmpty()) {
            this.mentor = null;
            return;
        }

        Mentor reference = new Mentor();
        reference.setId(Long.parseLong(mentorId));
        this.mentor = reference;
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
        return "Mentee{" +
                "id='" + id + '\'' +
                ", userId='" + getUserId() + '\'' +
                ", educationLevel='" + educationLevel + '\'' +
                ", fieldOfStudy='" + fieldOfStudy + '\'' +
                ", learningGoals='" + learningGoals + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", mentorId='" + getMentorId() + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
