package app.soapapi;

import app.model.Mentor;
import app.model.Mentee;
import app.model.User;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserSoapDto {

    // ── Base user fields ──────────────────────────────
    @XmlElement private Long id;
    @XmlElement private String username;
    @XmlElement private String email;
    @XmlElement private String role;
    @XmlElement private String status;
    @XmlElement private LocalDateTime createdAt;
    @XmlElement private LocalDateTime updatedAt;

    // ── Mentor-specific fields ────────────────────────
    @XmlElement private String specialization;
    @XmlElement private String expertise;
    @XmlElement private Integer yearsOfExperience;
    @XmlElement private String bio;
    @XmlElement private String qualifications;
    @XmlElement private String phoneNumber;

    // ── Mentee-specific fields ────────────────────────
    @XmlElement private String educationLevel;
    @XmlElement private String fieldOfStudy;
    @XmlElement private String learningGoals;
    @XmlElement private String mentorId;

    public UserSoapDto() {}

    // ── Smart factory method — handles User, Mentor, Mentee ──
    public static UserSoapDto fromEntity(User user) {
        UserSoapDto dto = new UserSoapDto();

        // Base fields — always present
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.role = user.getRole();
        dto.status = user.getStatus();
        dto.createdAt = user.getCreatedAt();
        dto.updatedAt = user.getUpdatedAt();

        // Mentor-specific fields
        if (user instanceof Mentor mentor) {
            dto.specialization = mentor.getSpecialization();
            dto.expertise = mentor.getExpertise();
            dto.yearsOfExperience = mentor.getYearsOfExperience();
            dto.bio = mentor.getBio();
            dto.qualifications = mentor.getQualifications();
            dto.phoneNumber = mentor.getPhoneNumber();
        }

        // Mentee-specific fields
        if (user instanceof Mentee mentee) {
            dto.educationLevel = mentee.getEducationLevel();
            dto.fieldOfStudy = mentee.getFieldOfStudy();
            dto.learningGoals = mentee.getLearningGoals();
            dto.phoneNumber = mentee.getPhoneNumber();
            dto.mentorId = mentee.getMentorId();
        }

        return dto;
    }
}