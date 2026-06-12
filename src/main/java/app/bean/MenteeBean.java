package app.bean;

import app.bean.event.UserRegisteredEvent;
import app.dao.MenteeDAO;
import app.model.AuditTrail;
import app.model.Mentee;
import app.utility.helper.PasswordUtil;
import app.utility.validation.ValidationResult;
import app.utility.validation.Validator;
import app.utility.validation.ValidatorQualifier;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ejb.Stateless;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.util.List;


@Stateless
@Named("menteeBean")
public class MenteeBean {

    private static final Logger logger = AppLogger.getLogger(MenteeBean.class);

    @Inject
    private MenteeDAO menteeDAO;

    @Inject
    private Event<AuditTrail> auditTrailEvent;

    @Inject
    private Event<UserRegisteredEvent> userRegisteredEvent;

    @Inject
    @ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTEE)
    private Validator<Mentee> menteeValidator;

    public MenteeBean() {
        logger.debug("CDI Bean initialized with default constructor");
    }

    @Inject
    public MenteeBean(MenteeDAO menteeDAO) {
        this.menteeDAO = menteeDAO;
        logger.debug("CDI Bean initialized with constructor injection");
    }

    /**
     * CREATE - Register a mentee.
     * source = "SELF"  -> self-registration, mentee.getPassword() is the plain password the user chose
     * source = "ADMIN" -> admin-created, system generates a temp password and emails it
     */
    public void add(Mentee mentee, String source) {
        logger.info("=== Starting Mentee Registration ({}) ===", source);
        logger.info("Username: {}, Email: {}, Field of Study: {}", mentee.getUsername(), mentee.getEmail(),
                mentee.getFieldOfStudy());

        String plainTempPassword = null;

        if ("ADMIN".equals(source)) {
            // Admin does not set a password — generate a temporary one
            plainTempPassword = PasswordUtil.generateTempPassword();
            mentee.setPassword(PasswordUtil.hashPassword(plainTempPassword));
            mentee.setMustChangePassword(true);
        } else {
            // Self-registration — user provided their own password
            if (PasswordUtil.needsHashing(mentee.getPassword())) {
                mentee.setPassword(PasswordUtil.hashPassword(mentee.getPassword()));
            }
            mentee.setMustChangePassword(false);
        }

        mentee.setRole("mentee");
        if (mentee.getStatus() == null || mentee.getStatus().isEmpty()) {
            mentee.setStatus("Active");
        }

        // Validate mentee data
        logger.debug("Validating mentee data...");
        ValidationResult validationResult = menteeValidator.validate(mentee);
        if (!validationResult.isValid()) {
            logger.error("Validation failed!");
            throw new IllegalArgumentException("Mentee validation failed: " + validationResult.getErrorMessages());
        }
        logger.debug("Validation passed ✓");

        // Add mentee to database
        logger.debug("Adding mentee to database...");
        menteeDAO.save(mentee);
        logger.info("Mentee added successfully, ID: {}", mentee.getId());

        // Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
            "Mentee",
            String.valueOf(mentee.getId()),
            "CREATE",
            "ADMIN".equals(source) ? "ADMIN" : String.valueOf(mentee.getId()),
            "Mentee registered: " + mentee.getUsername() + ", Field: " + mentee.getFieldOfStudy()));

        // Fire email event — includes temp password only for admin-created accounts
        logger.debug("Firing email registration event for mentee...");
        userRegisteredEvent.fire(
            new UserRegisteredEvent(
                mentee.getEmail(),
                mentee.getUsername(),
                "MENTEE",
                null,
                plainTempPassword
            ));

        logger.info("=== Mentee Registration Completed Successfully ===");
    }

    // Get mentee by ID
    public Mentee getById(String menteeId) {
        logger.debug("Fetching mentee by ID: {}", menteeId);
        return menteeDAO.findById(Long.parseLong(menteeId));
    }

    // Get all mentees
    public List<Mentee> findAll() {
        logger.debug("Fetching all mentees");
        return menteeDAO.findAll();
    }

    // Get mentees assigned to a mentor
    public List<Mentee> findByMentorId(String mentorId) {
        logger.debug("Fetching mentees for mentor ID: {}", mentorId);
        return menteeDAO.getMenteesByMentorId(mentorId);
    }

    // Get mentee by user ID
    public Mentee getByUserId(String userId) {
        logger.debug("Fetching mentee by user ID: {}", userId);
        return menteeDAO.findById(Long.parseLong(userId));
    }

    // Update existing mentee
    public void update(String menteeId, Mentee mentee) {
        logger.info("=== Updating mentee ===");
        logger.info("Mentee ID: {}", menteeId);

        logger.debug("Checking if mentee exists...");
        Mentee existingMentee = menteeDAO.findById(Long.parseLong(menteeId));
        if (existingMentee == null) {
            logger.error("Mentee not found!");
            throw new IllegalArgumentException("Mentee with ID '" + menteeId + "' not found");
        }
        logger.debug("Mentee found ✓");

        // Normalize mentorId (convert empty/whitespace to null BEFORE preservation)
        if (mentee.getMentorId() != null && mentee.getMentorId().trim().isEmpty()) {
            logger.debug("Empty mentorId provided, treating as null");
            mentee.setMentorId(null);
        }

        // Preserve immutable account identity
        mentee.setUsername(existingMentee.getUsername());
        mentee.setPassword(existingMentee.getPassword());
        mentee.setEmail(existingMentee.getEmail());
        mentee.setRole(existingMentee.getRole());
        mentee.setCreatedAt(existingMentee.getCreatedAt());
        mentee.setUpdatedAt(existingMentee.getUpdatedAt());
        mentee.setMustChangePassword(existingMentee.isMustChangePassword());

        // Preserve existing educationLevel if not provided
        if (mentee.getEducationLevel() == null || mentee.getEducationLevel().isEmpty()) {
            logger.debug("No new educationLevel provided, keeping existing educationLevel");
            mentee.setEducationLevel(existingMentee.getEducationLevel());
        }

        // Preserve existing mentorId if not provided
        if (mentee.getMentorId() == null) {
            logger.debug("No new mentorId provided, keeping existing mentorId");
            mentee.setMentorId(existingMentee.getMentorId());
        }

        // Set ID (important for validator context)
        mentee.setId(Long.parseLong(menteeId));

        // Set default status if needed
        if (mentee.getStatus() == null || mentee.getStatus().isEmpty()) {
            mentee.setStatus("Active");
        }

        // Validate AFTER fixing missing fields
        logger.debug("Validating mentee data...");
        ValidationResult validationResult = menteeValidator.validate(mentee);
        if (!validationResult.isValid()) {
            logger.error("Validation failed!");
            throw new IllegalArgumentException("Mentee validation failed: " + validationResult.getErrorMessages());
        }
        logger.debug("Validation passed ");

        // Update mentee in database
        logger.debug("Updating mentee in database...");
        menteeDAO.update(mentee);
        logger.info("Mentee updated successfully");

        // Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
                "Mentee",
                menteeId,
                "UPDATE",
                menteeId,
                "Mentee updated: Field=" + mentee.getFieldOfStudy()));

        logger.info("=== Mentee Update Completed Successfully ===");
    }

    // Delete mentee
    public void delete(String menteeId) {
        logger.info("=== Deleting mentee ===");
        logger.info("Mentee ID: {}", menteeId);

        // Check if mentee exists
        logger.debug("Checking if mentee exists...");
        Mentee mentee = menteeDAO.findById(Long.parseLong(menteeId));
        if (mentee == null) {
            logger.error("Mentee not found!");
            throw new IllegalArgumentException("Mentee with ID '" + menteeId + "' not found");
        }
        logger.debug("Mentee found ✓");

        // Delete mentee from database
        logger.debug("Deleting mentee from database...");
        menteeDAO.delete(Long.parseLong(menteeId));
        logger.info("Mentee deleted successfully");

        // Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
                "Mentee",
                menteeId,
                "DELETE",
                String.valueOf(mentee.getUserId()),
                "Mentee deleted"));

        logger.info("=== Mentee Deletion Completed Successfully ===");
    }
}