package app.dtos;

import app.model.Mentor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorRequestDto {

    private String userId;
    private String username;
    private String password;
    private String role;
    private String email;
    private String status;
    private String specialization;
    private String expertise;
    private Integer yearsOfExperience;
    private String bio;
    private String qualifications;
    private String phoneNumber;

    public Mentor toEntity() {
        Mentor mentor = new Mentor();
        mentor.setUserId(userId);
        mentor.setUsername(username);
        mentor.setPassword(password);
        mentor.setRole(role);
        mentor.setEmail(email);
        mentor.setStatus(status);
        mentor.setSpecialization(specialization);
        mentor.setExpertise(expertise);
        mentor.setYearsOfExperience(yearsOfExperience);
        mentor.setBio(bio);
        mentor.setQualifications(qualifications);
        mentor.setPhoneNumber(phoneNumber);
        return mentor;
    }
}