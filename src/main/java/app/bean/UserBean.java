package app.bean;

import app.bean.event.UserRegisteredEvent;
import app.dao.MenteeDAO;
import app.dao.MentorDAO;
import app.model.AuditTrail;
import app.model.Mentee;
import app.model.Mentor;
import app.model.User;
import app.utility.logging.AppLogger;
import app.utility.validation.ValidationResult;
import app.utility.validation.Validator;
import app.utility.validation.ValidatorQualifier;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ejb.Stateless;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;


@Stateless
@Named("userBean")
public class UserBean {

    private static final Logger logger = AppLogger.getLogger(UserBean.class);

    @Inject
    private MentorDAO mentorDAO;

    @Inject
    private MenteeDAO menteeDAO;

    @Inject
    private Event<AuditTrail> auditTrailEvent;

    @Inject
    private Event<UserRegisteredEvent> userRegisteredEvent;

    @Inject
    @ValidatorQualifier(ValidatorQualifier.ValidationChoice.USER)
    private Validator<User> userValidator;

    // CONSTRUCTOR INJECTION (alternative)
    @Inject
    public UserBean(MentorDAO mentorDAO, MenteeDAO menteeDAO) {
        this.mentorDAO = mentorDAO;
        this.menteeDAO = menteeDAO;
        logger.debug("[UserBean] CDI Bean initialized with constructor injection");
    }

    public UserBean(){}

    /**
     * CREATE - Register a new user
     */
    public void registerUser(User user) {
        logger.info("[UserBean] === Starting User Registration ===");
        logger.info("[UserBean] Username: {} , Email: {} , Role: {}", user.getUsername(), user.getEmail(), user.getRole());

        // Step 1: Check if username already exists
        logger.debug("[UserBean] Checking if username exists...");
        User existingUser = getUserByUsername(user.getUsername());
        if (existingUser != null) {
            logger.warn("[UserBean] Username already exists!");
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' already exists");
        }
        logger.debug("[UserBean] Username check passed ✓");

        // Step 2: Validate user data
        logger.debug("[UserBean] Validating user data...");
        ValidationResult validationResult = userValidator.validate(user);
        if (!validationResult.isValid()) {
            logger.error("[UserBean] Validation failed!");
            throw new IllegalArgumentException("User validation failed: " + validationResult.getErrorMessages());
        }
        logger.debug("[UserBean] Validation passed ✓");

        // Step 3: Add account to the appropriate table
        logger.debug("[UserBean] Adding user to database...");
        user.setStatus("Active");

        if ("mentor".equalsIgnoreCase(user.getRole())) {
            Mentor mentor = copyToMentor(user);
            mentorDAO.save(mentor);
            user.setId(mentor.getId());
        } else if ("mentee".equalsIgnoreCase(user.getRole())) {
            Mentee mentee = copyToMentee(user);
            menteeDAO.save(mentee);
            user.setId(mentee.getId());
        } else {
            throw new IllegalArgumentException("Role '" + user.getRole() + "' is not supported for database registration");
        }

        logger.info("[UserBean] User registered successfully, ID: {}", user.getId());

        // Step 4: Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
            "User",
            String.valueOf(user.getId()),
            "CREATE",
            "SYSTEM",
            "User registered: " + user.getUsername()
        ));

        // Step 5: Fire email event (will be handled asynchronously by EmailObserverBean)
        logger.debug("[UserBean] Firing email registration event...");
        userRegisteredEvent.fire(new UserRegisteredEvent(
            user.getEmail(),
            user.getUsername(),
            user.getRole()
        ));

        logger.info("[UserBean] === User Registration Completed Successfully ===");
    }

    /**
     * READ - Get user by ID
     */
    public User getUserById(String userId) {
        logger.debug("[UserBean] Fetching user by ID: {}", userId);
        Mentor mentor = mentorDAO.findById(Long.parseLong(userId));
        if (mentor != null) {
            return mentor;
        }

        return menteeDAO.findById(Long.parseLong(userId));
    }

    /**
     * READ - Get user by username
     */
    public User getUserByUsername(String username) {
        logger.debug("[UserBean] Fetching user by username: {}", username);
        Mentor mentor = mentorDAO.getMentorByUsername(username);
        if (mentor != null) {
            return mentor;
        }

        return menteeDAO.getMenteeByUsername(username);
    }

    /**
     * READ - Get all users
     */
    public List<User> getAllUsers() {
        logger.debug("[UserBean] Fetching all users");
        List<User> users = new ArrayList<>();
        users.addAll(mentorDAO.findAll());
        users.addAll(menteeDAO.findAll());
        return users;
    }

    /**
     * UPDATE - Update existing user
     */
    public void updateUser(String userId, User user) {
    logger.info("[UserBean] === Updating user ===");
    logger.debug("[UserBean] User ID: {}", userId);

    // Step 1: Check if user exists
    logger.debug("[UserBean] Checking if user exists...");
    User existingUser = getUserById(userId);
    if (existingUser == null) {
        logger.error("[UserBean] User not found!");
        throw new IllegalArgumentException("User with ID '" + userId + "' not found");
    }
    logger.debug("[UserBean] User found ✓");

    // Step 2: Preserve existing password BEFORE validation
    if (user.getPassword() == null || user.getPassword().isEmpty()) {
        logger.debug("[UserBean] No new password provided, keeping existing password");
        user.setPassword(existingUser.getPassword());
    }

    // Step 3: Set ID (important for validator context)
    user.setId(Long.parseLong(userId));

    // Step 4: Set default status if needed
    if (user.getStatus() == null || user.getStatus().isEmpty()) {
        user.setStatus("Active");
    }

    // Step 5: Validate AFTER fixing missing fields
    logger.debug("[UserBean] Validating user data...");
    ValidationResult validationResult = userValidator.validate(user);
    if (!validationResult.isValid()) {
        logger.error("[UserBean] Validation failed!");
        throw new IllegalArgumentException("User validation failed: " + validationResult.getErrorMessages());
    }
    logger.debug("[UserBean] Validation passed ✓");

    // Step 6: Update user in database
    logger.debug("[UserBean] Updating user in database...");
    if (existingUser instanceof Mentor existingMentor) {
        Mentor mentor = copyToMentor(user);
        mentor.setId(existingMentor.getId());
        mentorDAO.update(mentor);
    } else if (existingUser instanceof Mentee existingMentee) {
        Mentee mentee = copyToMentee(user);
        mentee.setId(existingMentee.getId());
        menteeDAO.update(mentee);
    } else {
        throw new IllegalStateException("Unsupported account type: " + existingUser.getClass().getSimpleName());
    }
    logger.info("[UserBean] User updated successfully");

    // Step 7: Fire CRUD event
        auditTrailEvent.fire(new AuditTrail(
        "User",
        userId,
        "UPDATE",
        "ADMIN",
        "User updated: " + user.getUsername()
    ));

    logger.info("[UserBean] === User Update Completed Successfully ===");
}

    /**
     * DELETE - Delete user
     */
    public void deleteUser(String userId) {
        logger.info("[UserBean] === Deleting user ===");
        logger.debug("[UserBean] User ID: {}", userId);

        // Step 1: Check if user exists
        logger.debug("[UserBean] Checking if user exists...");
        User user = getUserById(userId);
        if (user == null) {
            logger.error("[UserBean] User not found!");
            throw new IllegalArgumentException("User with ID '" + userId + "' not found");
        }
        logger.debug("[UserBean] User found ✓");

        // Step 2: Delete user from database
        logger.debug("[UserBean] Deleting user from database...");
        if (user instanceof Mentor) {
            mentorDAO.delete(Long.parseLong(userId));
        } else if (user instanceof Mentee) {
            menteeDAO.delete(Long.parseLong(userId));
        } else {
            throw new IllegalStateException("Unsupported account type: " + user.getClass().getSimpleName());
        }
        logger.info("[UserBean] User deleted successfully");

        // Step 3: Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
            "User",
            userId,
            "DELETE",
            "ADMIN",
            "User deleted: " + user.getUsername()
        ));

        logger.info("[UserBean] === User Deletion Completed Successfully ===");
    }

    private Mentor copyToMentor(User user) {
        Mentor mentor = new Mentor();
        mentor.setId(user.getId());
        mentor.setUsername(user.getUsername());
        mentor.setPassword(user.getPassword());
        mentor.setRole(user.getRole());
        mentor.setEmail(user.getEmail());
        mentor.setStatus(user.getStatus());
        mentor.setCreatedAt(user.getCreatedAt());
        mentor.setUpdatedAt(user.getUpdatedAt());
        return mentor;
    }

    private Mentee copyToMentee(User user) {
        Mentee mentee = new Mentee();
        mentee.setId(user.getId());
        mentee.setUsername(user.getUsername());
        mentee.setPassword(user.getPassword());
        mentee.setRole(user.getRole());
        mentee.setEmail(user.getEmail());
        mentee.setStatus(user.getStatus());
        mentee.setCreatedAt(user.getCreatedAt());
        mentee.setUpdatedAt(user.getUpdatedAt());
        return mentee;
    }
}
