package app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentees")
public class Mentee extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @NotBlank
    @Size(max = 100)
    @Column(name = "education_level", length = 100)
    private String educationLevel;

    @NotBlank
    @Size(max = 100)
    @Column(name = "field_of_study", length = 100)
    private String fieldOfStudy;

    @Size(max = 5000)
    @Column(name = "learning_goals", columnDefinition = "TEXT")
    private String learningGoals;

    @Pattern(regexp = "^$|^[0-9+()\\-\\s]{7,20}$")
    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    public Mentee(Long id, Long userId, String educationLevel, String fieldOfStudy,
                  String learningGoals, String phoneNumber, String mentorId, String status) {
        setId(id);
        setUserId(userId);
        this.educationLevel = educationLevel;
        this.fieldOfStudy = fieldOfStudy;
        this.learningGoals = learningGoals;
        this.phoneNumber = phoneNumber;
        setMentorId(mentorId);
        setStatus(status);
    }

    public Mentee() {
    }

    @Transient
    public Long getUserId() {
        return getId();
    }

    public void setUserId(Long userId) {
        setId(userId);
    }

    public User getUser() {
        return this;
    }

    public void setUser(User user) {
        if (user == null) {
            setId(null);
            setUsername(null);
            setPassword(null);
            setRole(null);
            setEmail(null);
            setStatus(null);
            setCreatedAt(null);
            setUpdatedAt(null);
            return;
        }

        setId(user.getId());
        setUsername(user.getUsername());
        setPassword(user.getPassword());
        setRole(user.getRole());
        setEmail(user.getEmail());
        setStatus(user.getStatus());
        setCreatedAt(user.getCreatedAt());
        setUpdatedAt(user.getUpdatedAt());
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
        return super.getStatus();
    }

    public void setStatus(String status) {
        super.setStatus(status);
    }

    @Override
    public String toString() {
        return "Mentee{" +
            "id='" + getId() + '\'' +
                ", userId='" + getUserId() + '\'' +
                ", educationLevel='" + educationLevel + '\'' +
                ", fieldOfStudy='" + fieldOfStudy + '\'' +
                ", learningGoals='" + learningGoals + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", mentorId='" + getMentorId() + '\'' +
            ", status='" + getStatus() + '\'' +
                '}';
    }
}
