package app.action;

import app.dao.UserDAO;
import app.model.Mentee;
import app.model.User;
import app.utility.validation.Validator;
import app.utility.validation.ValidatorQualifier;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.SQLException;

@Dependent
public class MenteeRegistration extends BaseRegistration<User> {

    // FIELD INJECTION
    @Inject
    @ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTEE)
    private Validator<Mentee> menteeValidator;

    @Inject
    private UserDAO userDAO;

    private String fieldOfStudy;
    private String learningGoals;
    private String phoneNumber;

    @Inject
    public MenteeRegistration() {
        super(null, null, null, "MENTEE", null);
    }

    @Override
    protected void validateRoleSpecific() {

        System.out.println("[MenteeRegistration] Running mentee validation...");

        if (password != null && password.length() < 6) {
            errors.add("Mentee password must be at least 6 characters");
        }

        System.out.println("[MenteeRegistration] Validation complete");
    }

    @Override
    protected User createUser() {
        return new User(
                null,
                username,
                password,
                role,
                email,
                "Pending"
        );
    }

    public void process() throws SQLException {

        Mentee mentee = new Mentee();
        mentee.setFieldOfStudy(fieldOfStudy);
        mentee.setLearningGoals(learningGoals);
        mentee.setPhoneNumber(phoneNumber);

        var result = menteeValidator.validate(mentee);

        if (!result.isValid()) {
            System.out.println(result.getErrorMessages());
            return;
        }

        userDAO.addUser(createUser());
    }

    // setters (form data)
    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public void setLearningGoals(String learningGoals) {
        this.learningGoals = learningGoals;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}