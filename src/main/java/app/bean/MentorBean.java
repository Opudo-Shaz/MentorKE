package app.bean;

import app.bean.event.CRUDEvent;
import app.bean.event.UserRegisteredEvent;
import app.dao.MentorDAO;
import app.dao.UserDAO;
import app.model.Mentor;
import app.model.User;
import app.utility.validation.ValidationResult;
import app.utility.validation.Validator;
import app.utility.validation.ValidatorQualifier;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ejb.Stateless;

import java.sql.SQLException;
import java.util.List;



@Stateless
@Named("mentorBean")
public class MentorBean {

    private MentorDAO mentorDAO;
    private UserDAO userDAO;

    @Inject
    private Event<CRUDEvent> crudEventFirer;

    @Inject
    private Event<UserRegisteredEvent> userRegisteredEvent;

    @Inject
    @ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTOR)
    private Validator<Mentor> mentorValidator;

    // PUBLIC NO-ARG CONSTRUCTOR (required by CDI/EJB)
    public MentorBean() {
        System.out.println("[MentorBean] CDI Bean initialized with default constructor");
    }

    // CONSTRUCTOR INJECTION (alternative)
    @Inject
    public MentorBean(MentorDAO mentorDAO, UserDAO userDAO) {
        this.mentorDAO = mentorDAO;
        this.userDAO = userDAO;
        System.out.println("[MentorBean] CDI Bean initialized with constructor injection");
    }

    public void registerMentor(Mentor mentor, User user) throws SQLException {
        System.out.println("[MentorBean] === Starting Mentor Registration ===");
        System.out.println("[MentorBean] Username: " + user.getUsername() +
                         ", Email: " + user.getEmail() +
                         ", Specialization: " + mentor.getSpecialization());

        // Step 1: Validate mentor data
        ValidationResult validationResult = mentorValidator.validate(mentor);
        if (!validationResult.isValid()) {
            System.err.println("[MentorBean] Validation failed!");
            throw new IllegalArgumentException("Mentor validation failed: " + validationResult.getErrorMessages());
        }
        System.out.println("[MentorBean] Validation passed ✓");

        // Step 2: Add user to database first
        System.out.println("[MentorBean] Adding user to database...");
        userDAO.addUser(user);
        System.out.println("[MentorBean] User added successfully, ID: " + user.getId());

        // Step 3: Set the user ID for mentor
        mentor.setUserId(user.getId());
        mentor.setStatus("Active");

        // Step 4: Add mentor to database
        System.out.println("[MentorBean] Adding mentor to database...");
        mentorDAO.addMentor(mentor);
        System.out.println("[MentorBean] Mentor added successfully, ID: " + mentor.getId());

        // Step 5: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "Mentor",
            mentor.getId(),
            "CREATE",
            user.getId(),
            "Mentor registered: " + user.getUsername() + ", Specialization: " + mentor.getSpecialization()
        ));

        // Step 6: Fire email event with specialization for mentors
        System.out.println("[MentorBean] Firing email registration event for mentor...");
        userRegisteredEvent.fire(
            new UserRegisteredEvent(
                user.getEmail(),
                user.getUsername(),
                "MENTOR",
                mentor.getSpecialization()
            )
        );

        System.out.println("[MentorBean] === Mentor Registration Completed Successfully ===");
    }

    /**
     * READ - Get mentor by ID
     */
    public Mentor getMentorById(String mentorId) throws SQLException {
        System.out.println("[MentorBean] Fetching mentor by ID: " + mentorId);
        return mentorDAO.getMentor(mentorId);
    }

    /**
     * READ - Get mentor by user ID
     */
    public Mentor getMentorByUserId(String userId) throws SQLException {
        System.out.println("[MentorBean] Fetching mentor for user ID: " + userId);
        return mentorDAO.getMentorByUserId(userId);
    }

    /**
     * READ - Get all mentors
     */
    public List<Mentor> getAllMentors() throws SQLException {
        System.out.println("[MentorBean] Fetching all mentors");
        return mentorDAO.getAllMentors();
    }

    /**
     * UPDATE - Update existing mentor
     */
     public void updateMentor(String mentorId, Mentor mentor) throws SQLException {
         System.out.println("[MentorBean] === Updating mentor ===");
         System.out.println("[MentorBean] Mentor ID: " + mentorId);

         // Step 1: Check if mentor exists
         System.out.println("[MentorBean] Checking if mentor exists...");
         Mentor existingMentor = mentorDAO.getMentor(mentorId);
         if (existingMentor == null) {
             System.err.println("[MentorBean] Mentor not found!");
             throw new IllegalArgumentException("Mentor with ID '" + mentorId + "' not found");
         }
         System.out.println("[MentorBean] Mentor found ✓");

         // Step 1b: Validate that user still exists
         if (!userDAO.exists(existingMentor.getUserId())) {
             System.err.println("[MentorBean] Associated user does not exist!");
             throw new IllegalArgumentException("Associated user no longer exists");
         }

         // Step 2: Validate mentor data
         System.out.println("[MentorBean] Validating mentor data...");
         mentor.setId(mentorId);
         ValidationResult validationResult = mentorValidator.validate(mentor);
         if (!validationResult.isValid()) {
             System.err.println("[MentorBean] Validation failed!");
             throw new IllegalArgumentException("Mentor validation failed: " + validationResult.getErrorMessages());
         }
         System.out.println("[MentorBean] Validation passed ✓");

         // Step 3: Update mentor in database
         System.out.println("[MentorBean] Updating mentor in database...");
         mentorDAO.updateMentor(mentorId, mentor);
         System.out.println("[MentorBean] Mentor updated successfully");

         // Step 4: Fire CRUD event for audit trail
         crudEventFirer.fire(new CRUDEvent(
             "Mentor",
             mentorId,
             "UPDATE",
             existingMentor.getUserId(),
             "Mentor updated: Specialization=" + mentor.getSpecialization()
         ));

         System.out.println("[MentorBean] === Mentor Update Completed Successfully ===");
     }

    /**
     * DELETE - Delete mentor
     */
    public void deleteMentor(String mentorId) throws SQLException {
        System.out.println("[MentorBean] === Deleting mentor ===");
        System.out.println("[MentorBean] Mentor ID: " + mentorId);

        // Step 1: Check if mentor exists
        System.out.println("[MentorBean] Checking if mentor exists...");
        Mentor mentor = mentorDAO.getMentor(mentorId);
        if (mentor == null) {
            System.err.println("[MentorBean] Mentor not found!");
            throw new IllegalArgumentException("Mentor with ID '" + mentorId + "' not found");
        }
        System.out.println("[MentorBean] Mentor found ✓");

        // Step 2: Delete mentor from database
        System.out.println("[MentorBean] Deleting mentor from database...");
        mentorDAO.deleteMentor(mentorId);
        System.out.println("[MentorBean] Mentor deleted successfully");

        // Step 3: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "Mentor",
            mentorId,
            "DELETE",
            mentor.getUserId(),
            "Mentor deleted"
        ));

        System.out.println("[MentorBean] === Mentor Deletion Completed Successfully ===");
    }
}
