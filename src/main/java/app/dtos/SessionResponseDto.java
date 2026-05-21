package app.dtos;

import app.model.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponseDto {

    private Long id;
    private String mentorId;
    private String menteeId;
    private String sessionLink;
    private LocalDateTime scheduledDate;
    private Integer durationMinutes;
    private String status;
    private String topic;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SessionResponseDto fromEntity(Session session) {
        if (session == null) {
            return null;
        }

        return new SessionResponseDto(
                session.getId(),
                session.getMentorId(),
                session.getMenteeId(),
                session.getSessionLink(),
                session.getScheduledDate(),
                session.getDurationMinutes(),
                session.getStatus(),
                session.getTopic(),
                session.getNotes(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}