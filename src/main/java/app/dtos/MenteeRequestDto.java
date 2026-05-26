package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import app.model.Mentee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenteeRequestDto {

    private Long userId;
    private String username;
    private String password;
    private String role;
    private String email;
    private String status;
    private String educationLevel;
    private String fieldOfStudy;
    private String learningGoals;
    private String phoneNumber;
    private String mentorId;

    public Mentee toEntity() {
        Mentee mentee = new Mentee();
        mentee.setUserId(userId);
        mentee.setUsername(username);
        mentee.setPassword(password);
        mentee.setRole(role);
        mentee.setEmail(email);
        mentee.setStatus(status);
        mentee.setEducationLevel(educationLevel);
        mentee.setFieldOfStudy(fieldOfStudy);
        mentee.setLearningGoals(learningGoals);
        mentee.setPhoneNumber(phoneNumber);
        mentee.setMentorId(mentorId);
        return mentee;
    }
}