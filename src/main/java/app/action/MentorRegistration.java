package app.action;

import app.dao.UserDAO;
import app.model.User;
import app.model.Mentor;
import app.utility.validation.ValidatorQualifier;
import app.utility.validation.Validator;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;


@RequestScoped
public class MentorRegistration extends BaseRegistration<User> {

    @Inject
    @ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTOR)
    private Validator<Mentor> mentorValidator;

    private String expertise;
    private Integer yearsOfExperience;
    private String bio;
    private String qualifications;
    private String phoneNumber;

    public MentorRegistration(String username, String password, String email, UserDAO userDAO) {
        super(username, password, email, "MENTOR", userDAO);
    }

    @Override
    protected void validateRoleSpecific() {
        System.out.println("[MentorRegistration] Running mentor-specific validation...");
        // Mentors need strong passwords for security
        if (password.length() < 8) {
            errors.add("Mentor password must be at least 8 characters for security");
        }
        System.out.println("[MentorRegistration] Mentor validation complete");
    }

    @Override
    protected User createUser() {
        return new User(null, username, password, role, email, "Pending");
    }


    // Getters and Setters for mentor-specific fields
    public void setSpecialization(String specialization) {
        // Additional fields for mentor-specific data
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
