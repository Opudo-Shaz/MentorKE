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
public class SessionCreateRequestDto {

    private String mentorId;
    private String menteeId;
    private String scheduledDate;
    private Integer durationMinutes;
    private String topic;

    public Session toEntity() {
        Session session = new Session();
        session.setMentorId(mentorId);
        session.setMenteeId(menteeId);
        session.setScheduledDate(LocalDateTime.parse(scheduledDate));
        session.setDurationMinutes(durationMinutes);
        session.setTopic(topic);
        return session;
    }
}