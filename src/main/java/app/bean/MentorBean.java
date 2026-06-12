package app.bean;

import app.bean.event.UserRegisteredEvent;
import app.dao.MentorDAO;
import app.model.AuditTrail;
import app.model.Mentor;
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

import java.sql.SQLException;
import java.util.List;



@Stateless
@Named("mentorBean")
public class MentorBean {

    private static final Logger logger = AppLogger.getLogger(MentorBean.class);

    @Inject
    private MentorDAO mentorDAO;

    @Inject
    private Event<AuditTrail> auditTrailEvent;

    @Inject
    private Event<UserRegisteredEvent> userRegisteredEvent;

    @Inject
    @ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTOR)
    private Validator<Mentor> mentorValidator;

    public MentorBean() {}


/**
 * CREATE - Register a mentor.
 * source = "SELF" (self-registration, user provides their own password)
 * source = "ADMIN" (admin-created, system generates temp password and emails it)
 */
public void add(Mentor mentor, String source) throws SQLException {
    logger.info("=== Starting Mentor Registration ({}) ===", source);
    logger.info("Username: {}, Email: {}, Specialization: {}",
            mentor.getUsername(), mentor.getEmail(), mentor.getSpecialization());

    String plainTempPassword = null;

    if ("ADMIN".equals(source)) {
        // Admin does not set a password — generate a temporary one
        plainTempPassword = PasswordUtil.generateTempPassword();
        mentor.setPassword(PasswordUtil.hashPassword(plainTempPassword));
        mentor.setMustChangePassword(true);
    } else {
        // Self-registration — user provided their own password
        if (PasswordUtil.needsHashing(mentor.getPassword())) {
            mentor.setPassword(PasswordUtil.hashPassword(mentor.getPassword()));
        }
        mentor.setMustChangePassword(false);
    }

    // Common setup
    mentor.setRole("mentor");
    if (mentor.getStatus() == null || mentor.getStatus().isEmpty()) {
        mentor.setStatus("Active");
    }

    // Validate
    logger.debug("Validating mentor data...");
    ValidationResult validationResult = mentorValidator.validate(mentor);
    if (!validationResult.isValid()) {
        logger.error("Validation failed!");
        throw new IllegalArgumentException("Mentor validation failed: " + validationResult.getErrorMessages());
    }
    logger.debug("Validation passed ✓");

    // Persist
    logger.debug("Adding mentor to database...");
    mentorDAO.save(mentor);
    logger.info("Mentor added successfully, ID: {}", mentor.getId());

    // Audit trail
    auditTrailEvent.fire(new AuditTrail(
        "Mentor",
        String.valueOf(mentor.getId()),
        "CREATE",
        "ADMIN".equals(source) ? "ADMIN" : String.valueOf(mentor.getId()),
        "Mentor registered: " + mentor.getUsername() + ", Specialization: " + mentor.getSpecialization()
    ));

    // Email event — pass temp password only if admin-created
    logger.debug("Firing email registration event for mentor...");
    userRegisteredEvent.fire(
        new UserRegisteredEvent(
            mentor.getEmail(),
            mentor.getUsername(),
            "MENTOR",
            mentor.getSpecialization(),
            plainTempPassword  // null for self-registration
        )
    );

    logger.info("=== Mentor Registration Completed Successfully ===");
}

    /**
     * READ - Get mentor by ID
     */
    public Mentor getById(String mentorId) throws SQLException {
        logger.debug("Fetching mentor by ID: {}", mentorId);
        return mentorDAO.findById(Long.parseLong(mentorId));
    }

    /**
     * READ - Get mentor by user ID
     */
    public Mentor getByUserId(String userId) throws SQLException {
        logger.debug("Fetching mentor by user ID: {}", userId);
        return mentorDAO.findById(Long.parseLong(userId));
    }

    /**
     * READ - Get all mentors
     */
    public List<Mentor> findAll() throws SQLException {
        logger.debug("Fetching all mentors");
        return mentorDAO.findAll();
    }

    public List<Mentor> searchMentors(
            String specialization,
            Integer minimumYearsOfExperience,
            String availability,
            String location,
            Double minimumRating
    ) throws SQLException {
        logger.debug("Searching mentors with specialization={}, minYears={}, availability={}, location={}, minRating={}",
                specialization, minimumYearsOfExperience, availability, location, minimumRating);
        return mentorDAO.searchMentors(
                specialization,
                minimumYearsOfExperience,
                availability,
                location,
                minimumRating
        );
    }

    public void applyMentorRating(String mentorId, int rating) throws SQLException {
        Mentor mentor = mentorDAO.findById(Long.parseLong(mentorId));
        if (mentor == null) {
            throw new IllegalArgumentException("Mentor not found");
        }

        int currentCount = mentor.getRatingCount() != null ? mentor.getRatingCount() : 0;
        double currentAverage = mentor.getAverageRating() != null ? mentor.getAverageRating() : 0.0;

        int newCount = currentCount + 1;
        double newAverage = ((currentAverage * currentCount) + rating) / newCount;

        mentor.setRatingCount(newCount);
        mentor.setAverageRating(newAverage);
        mentorDAO.update(mentor);
    }

    /**
     * UPDATE - Update existing mentor
     */
     public void update(String mentorId, Mentor mentor) throws SQLException {
         logger.info("=== Updating mentor ===");
         logger.info("Mentor ID: {}", mentorId);

         // Step 1: Check if mentor exists
         logger.debug("Checking if mentor exists...");
         Mentor existingMentor = mentorDAO.findById(Long.parseLong(mentorId));
         if (existingMentor == null) {
             logger.error("Mentor not found!");
             throw new IllegalArgumentException("Mentor with ID '" + mentorId + "' not found");
         }
         logger.debug("Mentor found ✓");

         mentor.setUsername(existingMentor.getUsername());
         mentor.setPassword(existingMentor.getPassword());
         mentor.setEmail(existingMentor.getEmail());
         mentor.setRole(existingMentor.getRole());
         mentor.setCreatedAt(existingMentor.getCreatedAt());
         mentor.setUpdatedAt(existingMentor.getUpdatedAt());

         // set mentor id
        mentor.setId(Long.parseLong(mentorId));

         // Step 4: Set default status if needed
         if (mentor.getStatus() == null || mentor.getStatus().isEmpty()) {
             mentor.setStatus("Active");
         }

        
         logger.debug("Validating mentor data...");
         ValidationResult validationResult = mentorValidator.validate(mentor);
         if (!validationResult.isValid()) {
             logger.error("Validation failed!");
             throw new IllegalArgumentException("Mentor validation failed: " + validationResult.getErrorMessages());
         }
         logger.debug("Validation passed ✓");

         //  Update mentor in database
         logger.debug("Updating mentor in database...");
         mentorDAO.update(mentor);
         logger.info("Mentor updated successfully");

         //  Fire CRUD event for audit trail
         auditTrailEvent.fire(new AuditTrail(
             "Mentor",
             mentorId,
             "UPDATE",
             mentorId,
             "Mentor updated: Specialization=" + mentor.getSpecialization()
         ));

         logger.info("=== Mentor Update Completed Successfully ===");
     }

    /**
     * DELETE - Delete mentor
     */
    public void delete(String mentorId) throws SQLException {
        logger.info("=== Deleting mentor ===");
        logger.info("Mentor ID: {}", mentorId);

        // Check if mentor exists
        logger.debug("Checking if mentor exists...");
        Mentor mentor = mentorDAO.findById(Long.parseLong(mentorId));
        if (mentor == null) {
            logger.error("Mentor not found!");
            throw new IllegalArgumentException("Mentor with ID '" + mentorId + "' not found");
        }
        logger.debug("Mentor found ✓");

        // Step 2: Delete mentor from database
        logger.debug("Deleting mentor from database...");
        mentorDAO.delete(Long.parseLong(mentorId));
        logger.info("Mentor deleted successfully");

        // Step 3: Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
            "Mentor",
            mentorId,
            "DELETE",
            mentor.getUserId(),
            "Mentor deleted"
        ));

        logger.info("=== Mentor Deletion Completed Successfully ===");
    }
}
