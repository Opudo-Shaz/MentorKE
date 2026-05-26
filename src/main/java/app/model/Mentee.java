package app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "mentees")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true, callSuper = false)
public class Mentee extends User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    @JsonIgnore
    private Mentor mentor;

    @NotBlank
    @Size(max = 100)
    @Column(name = "education_level", length = 100)
    @ToString.Include
    private String educationLevel;

    @NotBlank
    @Size(max = 100)
    @Column(name = "field_of_study", length = 100)
    @ToString.Include
    private String fieldOfStudy;

    @Size(max = 5000)
    @Column(name = "learning_goals", columnDefinition = "TEXT")
    @ToString.Include
    private String learningGoals;

    @Pattern(regexp = "^$|^[0-9+()\\-\\s]{7,20}$")
    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    @ToString.Include
    private String phoneNumber;

    @Builder
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

    // --- Identity bridge (delegates to inherited id) ---

    @Transient
    public Long getUserId() {
        return getId();
    }

    public void setUserId(Long userId) {
        setId(userId);
    }

    // --- Mentor FK convenience accessors ---

    @Transient
    @ToString.Include
    public String getMentorId() {
        return mentor != null && mentor.getId() != null
                ? String.valueOf(mentor.getId())
                : null;
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
}