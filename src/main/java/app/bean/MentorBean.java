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
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.List;



@Stateless
@Named("mentorBean")
public class MentorBean {

    private static final Logger logger = AppLogger.getLogger(MentorBean.class);

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
        logger.debug("CDI Bean initialized with default constructor");
    }

    // CONSTRUCTOR INJECTION (alternative)
    @Inject
    public MentorBean(MentorDAO mentorDAO, UserDAO userDAO) {
        this.mentorDAO = mentorDAO;
        this.userDAO = userDAO;
        logger.debug("CDI Bean initialized with constructor injection");
    }

    public void registerMentor(Mentor mentor, User user) throws SQLException {
        logger.info("=== Starting Mentor Registration ===");
        logger.info("Username: {}, Email: {}, Specialization: {}", user.getUsername(), user.getEmail(), mentor.getSpecialization());

        // Step 1: Validate mentor data
        ValidationResult validationResult = mentorValidator.validate(mentor);
        if (!validationResult.isValid()) {
            logger.error("Validation failed!");
            throw new IllegalArgumentException("Mentor validation failed: " + validationResult.getErrorMessages());
        }
        logger.debug("Validation passed ✓");

        // Step 2: Add user to database first
        logger.debug("Adding user to database...");
        userDAO.addUser(user);
        logger.info("User added successfully, ID: {}", user.getId());

        // Step 3: Set the user ID for mentor
        mentor.setUserId(user.getId());
        mentor.setStatus("Active");

        // Step 4: Add mentor to database
        logger.debug("Adding mentor to database...");
        mentorDAO.addMentor(mentor);
        logger.info("Mentor added successfully, ID: {}", mentor.getId());

        // Step 5: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "Mentor",
            mentor.getId(),
            "CREATE",
            user.getId(),
            "Mentor registered: " + user.getUsername() + ", Specialization: " + mentor.getSpecialization()
        ));

        // Step 6: Fire email event with specialization for mentors
        logger.debug("Firing email registration event for mentor...");
        userRegisteredEvent.fire(
            new UserRegisteredEvent(
                user.getEmail(),
                user.getUsername(),
                "MENTOR",
                mentor.getSpecialization()
            )
        );

        logger.info("=== Mentor Registration Completed Successfully ===");
    }

    /**
     * READ - Get mentor by ID
     */
    public Mentor getMentorById(String mentorId) throws SQLException {
        logger.debug("Fetching mentor by ID: {}", mentorId);
        return mentorDAO.getMentor(mentorId);
    }

    /**
     * READ - Get mentor by user ID
     */
    public Mentor getMentorByUserId(String userId) throws SQLException {
        logger.debug("Fetching mentor for user ID: {}", userId);
        return mentorDAO.getMentorByUserId(userId);
    }

    /**
     * READ - Get all mentors
     */
    public List<Mentor> getAllMentors() throws SQLException {
        logger.debug("Fetching all mentors");
        return mentorDAO.getAllMentors();
    }

    /**
     * UPDATE - Update existing mentor
     */
     public void updateMentor(String mentorId, Mentor mentor) throws SQLException {
         logger.info("=== Updating mentor ===");
         logger.info("Mentor ID: {}", mentorId);

         // Step 1: Check if mentor exists
         logger.debug("Checking if mentor exists...");
         Mentor existingMentor = mentorDAO.getMentor(mentorId);
         if (existingMentor == null) {
             logger.error("Mentor not found!");
             throw new IllegalArgumentException("Mentor with ID '" + mentorId + "' not found");
         }
         logger.debug("Mentor found ✓");

         // Step 1b: Validate that user still exists
         if (!userDAO.exists(existingMentor.getUserId())) {
             logger.error("Associated user does not exist!");
             throw new IllegalArgumentException("Associated user no longer exists");
         }

         // Step 2: Validate mentor data
         logger.debug("Validating mentor data...");
         mentor.setId(mentorId);
         ValidationResult validationResult = mentorValidator.validate(mentor);
         if (!validationResult.isValid()) {
             logger.error("Validation failed!");
             throw new IllegalArgumentException("Mentor validation failed: " + validationResult.getErrorMessages());
         }
         logger.debug("Validation passed ✓");

         // Step 3: Update mentor in database
         logger.debug("Updating mentor in database...");
         mentorDAO.updateMentor(mentorId, mentor);
         logger.info("Mentor updated successfully");

         // Step 4: Fire CRUD event for audit trail
         crudEventFirer.fire(new CRUDEvent(
             "Mentor",
             mentorId,
             "UPDATE",
             existingMentor.getUserId(),
             "Mentor updated: Specialization=" + mentor.getSpecialization()
         ));

         logger.info("=== Mentor Update Completed Successfully ===");
     }

    /**
     * CREATE - Add mentor (admin function, no user registration)
     */
    public void addMentorAdmin(Mentor mentor) throws SQLException {
        logger.info("=== Admin Adding Mentor ===");
        logger.info("User ID: {}, Specialization: {}", mentor.getUserId(), mentor.getSpecialization());

        // Step 1: Check if user exists
        logger.debug("Checking if user exists...");
        if (mentor.getUserId() == null || mentor.getUserId().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        User user = userDAO.getUser(mentor.getUserId());
        if (user == null) {
            logger.error("User not found!");
            throw new IllegalArgumentException("User with ID '" + mentor.getUserId() + "' not found");
        }
        logger.debug("User found ✓");

        // Step 2: Validate mentor data
        logger.debug("Validating mentor data...");
        ValidationResult validationResult = mentorValidator.validate(mentor);
        if (!validationResult.isValid()) {
            logger.error("Validation failed!");
            throw new IllegalArgumentException("Mentor validation failed: " + validationResult.getErrorMessages());
        }
        logger.debug("Validation passed ✓");

        // Step 3: Add mentor to database
        logger.debug("Adding mentor to database...");
        mentorDAO.addMentor(mentor);
        logger.info("Mentor added successfully, ID: {}", mentor.getId());

        // Step 4: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "Mentor",
            mentor.getId(),
            "CREATE",
            "ADMIN",
            "Mentor added by admin: " + user.getUsername() + ", Specialization: " + mentor.getSpecialization()
        ));

        // Step 5: Fire email event to notify user they're now a mentor
        logger.debug("Firing email for newly assigned mentor...");
        userRegisteredEvent.fire(
            new UserRegisteredEvent(
                user.getEmail(),
                user.getUsername(),
                "MENTOR",
                mentor.getSpecialization()
            )
        );

        logger.info("=== Mentor Addition Completed Successfully ===");
    }

    /**
     * DELETE - Delete mentor
     */
    public void deleteMentor(String mentorId) throws SQLException {
        logger.info("=== Deleting mentor ===");
        logger.info("Mentor ID: {}", mentorId);

        // Step 1: Check if mentor exists
        logger.debug("Checking if mentor exists...");
        Mentor mentor = mentorDAO.getMentor(mentorId);
        if (mentor == null) {
            logger.error("Mentor not found!");
            throw new IllegalArgumentException("Mentor with ID '" + mentorId + "' not found");
        }
        logger.debug("Mentor found ✓");

        // Step 2: Delete mentor from database
        logger.debug("Deleting mentor from database...");
        mentorDAO.deleteMentor(mentorId);
        logger.info("Mentor deleted successfully");

        // Step 3: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "Mentor",
            mentorId,
            "DELETE",
            mentor.getUserId(),
            "Mentor deleted"
        ));

        logger.info("=== Mentor Deletion Completed Successfully ===");
    }
}
