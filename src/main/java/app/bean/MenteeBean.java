package app.bean;

import app.bean.event.CRUDEvent;
import app.bean.event.UserRegisteredEvent;
import app.dao.MenteeDAO;
import app.dao.UserDAO;
import app.model.Mentee;
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

/**
 * MenteeBean - EJB Stateless bean that handles mentee CRUD operations
 * Manages mentees with full validation and audit trail integration
 */
@Stateless
@Named("menteeBean")
public class MenteeBean {

    private MenteeDAO menteeDAO;
    private UserDAO userDAO;

    @Inject
    private Event<CRUDEvent> crudEventFirer;

    @Inject
    private Event<UserRegisteredEvent> userRegisteredEvent;

    @Inject
    @ValidatorQualifier(ValidatorQualifier.ValidationChoice.MENTEE)
    private Validator<Mentee> menteeValidator;

    // PUBLIC NO-ARG CONSTRUCTOR (required by CDI/EJB)
    public MenteeBean() {
        System.out.println("[MenteeBean] CDI Bean initialized with default constructor");
    }

    // CONSTRUCTOR INJECTION (alternative)
    @Inject
    public MenteeBean(MenteeDAO menteeDAO, UserDAO userDAO) {
        this.menteeDAO = menteeDAO;
        this.userDAO = userDAO;
        System.out.println("[MenteeBean] CDI Bean initialized with constructor injection");
    }

    /**
     * CREATE - Register a new mentee
     */
    public void registerMentee(Mentee mentee, User user) throws SQLException {
        System.out.println("[MenteeBean] === Starting Mentee Registration ===");
        System.out.println("[MenteeBean] Username: " + user.getUsername() +
                         ", Email: " + user.getEmail() +
                         ", Field of Study: " + mentee.getFieldOfStudy());

        // Step 1: Validate mentee data
        ValidationResult validationResult = menteeValidator.validate(mentee);
        if (!validationResult.isValid()) {
            System.err.println("[MenteeBean] Validation failed!");
            throw new IllegalArgumentException("Mentee validation failed: " + validationResult.getErrorMessages());
        }
        System.out.println("[MenteeBean] Validation passed ✓");

        // Step 2: Add user to database first
        System.out.println("[MenteeBean] Adding user to database...");
        userDAO.addUser(user);
        System.out.println("[MenteeBean] User added successfully, ID: " + user.getId());

        // Step 3: Set the user ID for mentee
        mentee.setUserId(user.getId());
        mentee.setStatus("Active");

        // Step 4: Add mentee to database
        System.out.println("[MenteeBean] Adding mentee to database...");
        menteeDAO.addMentee(mentee);
        System.out.println("[MenteeBean] Mentee added successfully, ID: " + mentee.getId());

        // Step 5: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "Mentee",
            mentee.getId(),
            "CREATE",
            user.getId(),
            "Mentee registered: " + user.getUsername() + ", Field: " + mentee.getFieldOfStudy()
        ));

        // Step 6: Fire email event for mentees
        System.out.println("[MenteeBean] Firing email registration event for mentee...");
        userRegisteredEvent.fire(
            new UserRegisteredEvent(
                user.getEmail(),
                user.getUsername(),
                "MENTEE"
            )
        );

        System.out.println("[MenteeBean] === Mentee Registration Completed Successfully ===");
    }

    /**
     * READ - Get mentee by ID
     */
    public Mentee getMenteeById(String menteeId) throws SQLException {
        System.out.println("[MenteeBean] Fetching mentee by ID: " + menteeId);
        return menteeDAO.getMentee(menteeId);
    }

    /**
     * READ - Get mentee by user ID
     */
    public Mentee getMenteeByUserId(String userId) throws SQLException {
        System.out.println("[MenteeBean] Fetching mentee for user ID: " + userId);
        return menteeDAO.getMenteeByUserId(userId);
    }

    /**
     * READ - Get all mentees
     */
    public List<Mentee> getAllMentees() throws SQLException {
        System.out.println("[MenteeBean] Fetching all mentees");
        return menteeDAO.getAllMentees();
    }

    /**
     * UPDATE - Update existing mentee
     */
    public void updateMentee(String menteeId, Mentee mentee) throws SQLException {
        System.out.println("[MenteeBean] === Updating mentee ===");
        System.out.println("[MenteeBean] Mentee ID: " + menteeId);

        // Step 1: Check if mentee exists
        System.out.println("[MenteeBean] Checking if mentee exists...");
        Mentee existingMentee = menteeDAO.getMentee(menteeId);
        if (existingMentee == null) {
            System.err.println("[MenteeBean] Mentee not found!");
            throw new IllegalArgumentException("Mentee with ID '" + menteeId + "' not found");
        }
        System.out.println("[MenteeBean] Mentee found ✓");

        // Step 2: Validate mentee data
        System.out.println("[MenteeBean] Validating mentee data...");
        mentee.setId(menteeId);
        ValidationResult validationResult = menteeValidator.validate(mentee);
        if (!validationResult.isValid()) {
            System.err.println("[MenteeBean] Validation failed!");
            throw new IllegalArgumentException("Mentee validation failed: " + validationResult.getErrorMessages());
        }
        System.out.println("[MenteeBean] Validation passed ✓");

        // Step 3: Update mentee in database
        System.out.println("[MenteeBean] Updating mentee in database...");
        menteeDAO.updateMentee(menteeId, mentee);
        System.out.println("[MenteeBean] Mentee updated successfully");

        // Step 4: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "Mentee",
            menteeId,
            "UPDATE",
            existingMentee.getUserId(),
            "Mentee updated: Field=" + mentee.getFieldOfStudy()
        ));

        System.out.println("[MenteeBean] === Mentee Update Completed Successfully ===");
    }

    /**
     * DELETE - Delete mentee
     */
    public void deleteMentee(String menteeId) throws SQLException {
        System.out.println("[MenteeBean] === Deleting mentee ===");
        System.out.println("[MenteeBean] Mentee ID: " + menteeId);

        // Step 1: Check if mentee exists
        System.out.println("[MenteeBean] Checking if mentee exists...");
        Mentee mentee = menteeDAO.getMentee(menteeId);
        if (mentee == null) {
            System.err.println("[MenteeBean] Mentee not found!");
            throw new IllegalArgumentException("Mentee with ID '" + menteeId + "' not found");
        }
        System.out.println("[MenteeBean] Mentee found ✓");

        // Step 2: Delete mentee from database
        System.out.println("[MenteeBean] Deleting mentee from database...");
        menteeDAO.deleteMentee(menteeId);
        System.out.println("[MenteeBean] Mentee deleted successfully");

        // Step 3: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "Mentee",
            menteeId,
            "DELETE",
            mentee.getUserId(),
            "Mentee deleted"
        ));

        System.out.println("[MenteeBean] === Mentee Deletion Completed Successfully ===");
    }
}

