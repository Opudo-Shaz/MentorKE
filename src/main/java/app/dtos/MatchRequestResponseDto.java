package app.dtos;

import app.model.MatchRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequestResponseDto {

    private Long id;
    private String menteeId;
    private String mentorId;
    private String requestedSpecialization;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MatchRequestResponseDto fromEntity(MatchRequest request) {
        if (request == null) {
            return null;
        }

        return new MatchRequestResponseDto(
                request.getId(),
                request.getMenteeId(),
                request.getMentorId(),
                request.getRequestedSpecialization(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}