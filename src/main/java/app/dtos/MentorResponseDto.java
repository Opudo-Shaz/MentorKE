package app.dtos;

import app.model.Mentor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorResponseDto {

    private Long id;
    private String userId;
    private String username;
    private String role;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String specialization;
    private String expertise;
    private Integer yearsOfExperience;
    private String bio;
    private String qualifications;
    private String phoneNumber;

    public static MentorResponseDto fromEntity(Mentor mentor) {
        if (mentor == null) {
            return null;
        }

        return new MentorResponseDto(
                mentor.getId(),
                mentor.getUserId(),
                mentor.getUsername(),
                mentor.getRole(),
                mentor.getEmail(),
                mentor.getStatus(),
                mentor.getCreatedAt(),
                mentor.getUpdatedAt(),
                mentor.getSpecialization(),
                mentor.getExpertise(),
                mentor.getYearsOfExperience(),
                mentor.getBio(),
                mentor.getQualifications(),
                mentor.getPhoneNumber()
        );
    }
}