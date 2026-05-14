package app.bean;

import app.bean.event.UserRegisteredEvent;
import app.dao.MenteeDAO;
import app.dao.UserDAO;
import app.model.AuditTrail;
import app.model.Mentee;
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

/**
 * MenteeBean - EJB Stateless bean that handles mentee CRUD operations
 * Manages mentees with full validation and audit trail integration
 */
@Stateless
@Named("menteeBean")
public class MenteeBean {

    private static final Logger logger = AppLogger.getLogger(MenteeBean.class);

    private MenteeDAO menteeDAO;
    private UserDAO userDAO;

    @Inject
    private Event<AuditTrail> auditTrailEvent;

    @Inject
    private Event<UserRegisteredEvent> userRegisteredEvent;

    @Inject
    @ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTEE)
    private Validator<Mentee> menteeValidator;

    // PUBLIC NO-ARG CONSTRUCTOR (required by CDI/EJB)
    public MenteeBean() {
        logger.debug("CDI Bean initialized with default constructor");
    }

    // CONSTRUCTOR INJECTION (alternative)
    @Inject
    public MenteeBean(MenteeDAO menteeDAO, UserDAO userDAO) {
        this.menteeDAO = menteeDAO;
        this.userDAO = userDAO;
        logger.debug("CDI Bean initialized with constructor injection");
    }

    /**
     * CREATE - Register a new mentee
     */
    public void registerMentee(Mentee mentee, User user) throws SQLException {
        logger.info("=== Starting Mentee Registration ===");
        logger.info("Username: {}, Email: {}, Field of Study: {}", user.getUsername(), user.getEmail(),
                mentee.getFieldOfStudy());

        // Step 1: Add user to database first
        logger.debug("Adding user to database...");
        userDAO.addUser(user);
        logger.info("User added successfully, ID: {}", user.getId());

        // Step 2: Set the user ID and status for mentee
        mentee.setUserId(user.getId());
        mentee.setStatus("Active");

        // Step 3: NOW validate mentee data (after required fields are set)
        logger.debug("Validating mentee data...");
        ValidationResult validationResult = menteeValidator.validate(mentee);
        if (!validationResult.isValid()) {
            logger.error("Validation failed!");
            throw new IllegalArgumentException("Mentee validation failed: " + validationResult.getErrorMessages());
        }
        logger.debug("Validation passed ✓");

        // Step 4: Add mentee to database
        logger.debug("Adding mentee to database...");
        menteeDAO.addMentee(mentee);
        logger.info("Mentee added successfully, ID: {}", mentee.getId());

        // Step 5: Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
            "Mentee",
            String.valueOf(mentee.getId()),
            "CREATE",
            String.valueOf(user.getId()),
            "Mentee registered: " + user.getUsername() + ", Field: " + mentee.getFieldOfStudy()));

        // Step 6: Fire email event for mentees
        logger.debug("Firing email registration event for mentee...");
        userRegisteredEvent.fire(
                new UserRegisteredEvent(
                        user.getEmail(),
                        user.getUsername(),
                        "MENTEE"));

        logger.info("=== Mentee Registration Completed Successfully ===");
    }

    /**
     * READ - Get mentee by ID
     */
    public Mentee getMenteeById(String menteeId) throws SQLException {
        logger.debug("Fetching mentee by ID: {}", menteeId);
        return menteeDAO.getMentee(menteeId);
    }

    /**
     * READ - Get mentee by user ID
     */
    public Mentee getMenteeByUserId(String userId) throws SQLException {
        logger.debug("Fetching mentee for user ID: {}", userId);
        return menteeDAO.getMenteeByUserId(userId);
    }

    /**
     * READ - Get all mentees
     */
    public List<Mentee> getAllMentees() throws SQLException {
        logger.debug("Fetching all mentees");
        return menteeDAO.getAllMentees();
    }

    /**
     * READ - Get mentees assigned to a mentor
     */
    public List<Mentee> getMenteesByMentorId(String mentorId)
            throws SQLException {

        logger.debug("Fetching mentees for mentor ID: {}", mentorId);

        return menteeDAO.getMenteesByMentorId(mentorId);
    }

    /**
     * UPDATE - Update existing mentee
     */
    public void updateMentee(String menteeId, Mentee mentee) throws SQLException {
        logger.info("=== Updating mentee ===");
        logger.info("Mentee ID: {}", menteeId);

        // Step 1: Check if mentee exists
        logger.debug("Checking if mentee exists...");
        Mentee existingMentee = menteeDAO.getMentee(menteeId);
        if (existingMentee == null) {
            logger.error("Mentee not found!");
            throw new IllegalArgumentException("Mentee with ID '" + menteeId + "' not found");
        }
        logger.debug("Mentee found ✓");

        // Step 2: Normalize mentorId (convert empty/whitespace to null BEFORE
        // preservation)
        if (mentee.getMentorId() != null && mentee.getMentorId().trim().isEmpty()) {
            logger.debug("Empty mentorId provided, treating as null");
            mentee.setMentorId(null);
        }

        // Step 3: Preserve existing userId BEFORE validation (cannot be changed)
        if (mentee.getUserId() == null) {
            logger.debug("No new userId provided, keeping existing userId");
            mentee.setUserId(existingMentee.getUserId());
        }

        // Step 4: Preserve existing educationLevel if not provided
        if (mentee.getEducationLevel() == null || mentee.getEducationLevel().isEmpty()) {
            logger.debug("No new educationLevel provided, keeping existing educationLevel");
            mentee.setEducationLevel(existingMentee.getEducationLevel());
        }

        // Step 5: Preserve existing mentorId if not provided
        if (mentee.getMentorId() == null) {
            logger.debug("No new mentorId provided, keeping existing mentorId");
            mentee.setMentorId(existingMentee.getMentorId());
        }

        // Step 6: Set ID (important for validator context)
        mentee.setId(Long.parseLong(menteeId));

        // Step 7: Set default status if needed
        if (mentee.getStatus() == null || mentee.getStatus().isEmpty()) {
            mentee.setStatus("Active");
        }

        // Step 8: Validate AFTER fixing missing fields
        logger.debug("Validating mentee data...");
        ValidationResult validationResult = menteeValidator.validate(mentee);
        if (!validationResult.isValid()) {
            logger.error("Validation failed!");
            throw new IllegalArgumentException("Mentee validation failed: " + validationResult.getErrorMessages());
        }
        logger.debug("Validation passed ✓");

        // Step 9: Update mentee in database
        logger.debug("Updating mentee in database...");
        menteeDAO.updateMentee(menteeId, mentee);
        logger.info("Mentee updated successfully");

        // Step 10: Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
                "Mentee",
                menteeId,
                "UPDATE",
                String.valueOf(existingMentee.getUserId()),
                "Mentee updated: Field=" + mentee.getFieldOfStudy()));

        logger.info("=== Mentee Update Completed Successfully ===");
    }

    /**
     * CREATE - Add mentee (admin function, no user registration)
     */
    public void addMenteeAdmin(Mentee mentee) throws SQLException {
        logger.info("=== Admin Adding Mentee ===");
        logger.info("User ID: {}, Field of Study: {}", mentee.getUserId(), mentee.getFieldOfStudy());

        // Step 1: Check if user exists
        logger.debug("Checking if user exists...");
        if (mentee.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        User user = userDAO.getUser(String.valueOf(mentee.getUserId()));
        if (user == null) {
            logger.error("User not found!");
            throw new IllegalArgumentException("User with ID '" + mentee.getUserId() + "' not found");
        }
        logger.debug("User found ✓");

        // Step 2: Validate mentee data
        logger.debug("Validating mentee data...");
        ValidationResult validationResult = menteeValidator.validate(mentee);
        if (!validationResult.isValid()) {
            logger.error("Validation failed!");
            throw new IllegalArgumentException("Mentee validation failed: " + validationResult.getErrorMessages());
        }
        logger.debug("Validation passed ✓");

        // Step 3: Add mentee to database
        logger.debug("Adding mentee to database...");
        menteeDAO.addMentee(mentee);
        logger.info("Mentee added successfully, ID: {}", mentee.getId());

        // Step 4: Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
                "Mentee",
                String.valueOf(mentee.getId()),
                "CREATE",
                "ADMIN",
                "Mentee added by admin: " + user.getUsername() + ", Field: " + mentee.getFieldOfStudy()));

        // Step 5: Fire email event to notify user they're now a mentee
        logger.debug("Firing email for newly assigned mentee...");
        userRegisteredEvent.fire(
                new UserRegisteredEvent(
                        user.getEmail(),
                        user.getUsername(),
                        "MENTEE"));

        logger.info("=== Mentee Addition Completed Successfully ===");
    }

    /**
     * DELETE - Delete mentee
     */
    public void deleteMentee(String menteeId) throws SQLException {
        logger.info("=== Deleting mentee ===");
        logger.info("Mentee ID: {}", menteeId);

        // Step 1: Check if mentee exists
        logger.debug("Checking if mentee exists...");
        Mentee mentee = menteeDAO.getMentee(menteeId);
        if (mentee == null) {
            logger.error("Mentee not found!");
            throw new IllegalArgumentException("Mentee with ID '" + menteeId + "' not found");
        }
        logger.debug("Mentee found ✓");

        // Step 2: Delete mentee from database
        logger.debug("Deleting mentee from database...");
        menteeDAO.deleteMentee(menteeId);
        logger.info("Mentee deleted successfully");

        // Step 3: Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
                "Mentee",
                menteeId,
                "DELETE",
                String.valueOf(mentee.getUserId()),
                "Mentee deleted"));

        logger.info("=== Mentee Deletion Completed Successfully ===");
    }
}
