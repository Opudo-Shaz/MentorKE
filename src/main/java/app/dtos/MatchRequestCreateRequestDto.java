package app.dtos;

import app.model.MatchRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequestCreateRequestDto {

    private String menteeId;
    private String mentorId;
    private String specialization;

    public MatchRequest toEntity() {
        return MatchRequest.builder()
                .menteeId(menteeId)
                .mentorId(mentorId)
                .requestedSpecialization(specialization)
                .build();
    }
}