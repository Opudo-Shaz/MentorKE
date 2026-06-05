package app.dtos;

import app.model.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
        
        // Handle datetime parsing: if format is HH:mm (missing seconds), pad with :00
        String dateTimeString = scheduledDate;
    if (scheduledDate != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");
        session.setScheduledDate(LocalDateTime.parse(scheduledDate, formatter));
    }
        
        session.setScheduledDate(LocalDateTime.parse(dateTimeString));
        session.setDurationMinutes(durationMinutes);
        session.setTopic(topic);
        return session;
    }
}