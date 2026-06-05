package app.dtos;

import app.model.Mentee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenteeResponseDto {

    private Long id;
    private Long userId;
    private String username;
    private String role;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String educationLevel;
    private String fieldOfStudy;
    private String learningGoals;
    private String phoneNumber;
    private String mentorId;

    public static MenteeResponseDto fromEntity(Mentee mentee) {
        if (mentee == null) {
            return null;
        }

        return new MenteeResponseDto(
                mentee.getId(),
                mentee.getUserId(),
                mentee.getUsername(),
                mentee.getRole(),
                mentee.getEmail(),
                mentee.getStatus(),
                mentee.getCreatedAt(),
                mentee.getUpdatedAt(),
                mentee.getEducationLevel(),
                mentee.getFieldOfStudy(),
                mentee.getLearningGoals(),
                mentee.getPhoneNumber(),
                mentee.getMentorId()
        );
    }
}