package app.bean;

import app.dao.MenteeDAO;
import app.dao.MentorDAO;
import app.model.AuditTrail;
import app.model.Mentee;
import app.model.Mentor;
import app.model.User;
import app.utility.helper.PasswordUtil;
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
    @ValidatorQualifier(ValidatorQualifier.ValidationChoice.USER)
    private Validator<User> userValidator;

    @Inject
    private RegistrationService registrationService;

    public UserBean(){}

    /**
     * CREATE - Register a new user
     */
    public void registerUser(User user) {
        logger.info("[UserBean] === Starting User Registration ===");
        logger.info("[UserBean] Username: {} , Email: {} , Role: {}", user.getUsername(), user.getEmail(), user.getRole());

        //  Check if username already exists
        logger.debug(" Checking if username exists...");
        User existingUser = getUserByUsername(user.getUsername());
        if (existingUser != null) {
            logger.warn("Username already exists!");
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' already exists");
        }
        logger.debug("Username check passed ✓");

        //  Validate user data
        ValidationResult validationResult = userValidator.validate(user);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("User validation failed: " + validationResult.getErrorMessages());
        }
        logger.debug("Validation passed ");

    
        user.setStatus("Active");

        // Delegate to RegistrationService which will create the subtype and persist it
        try {
            registrationService.registerUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getRole(), new java.util.HashMap<>());
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Registration failed", e);
        }

        // After delegation, resolve the created entity and set the id back on the supplied User
        Mentor createdMentor = mentorDAO.getMentorByUsername(user.getUsername());
        if (createdMentor != null) {
            user.setId(createdMentor.getId());
            logger.info("[UserBean] User registered successfully as Mentor, ID: {}", user.getId());
            return;
        }

        Mentee createdMentee = menteeDAO.getMenteeByUsername(user.getUsername());
        if (createdMentee != null) {
            user.setId(createdMentee.getId());
            logger.info("[UserBean] User registered successfully as Mentee, ID: {}", user.getId());
            return;
        }

        // If we reach here something unexpected happened
        throw new IllegalStateException("User registration succeeded but created entity not found for username: " + user.getUsername());
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

    //  Check if user exists
    User existingUser = getUserById(userId);
    if (existingUser == null) {
        logger.error("[UserBean] User not found!");
        throw new IllegalArgumentException("User with ID '" + userId + "' not found");
    }
    logger.debug("[UserBean] User found ✓");

    // Preserve existing password BEFORE validation
    if (user.getPassword() == null || user.getPassword().isEmpty()) {
        logger.debug("[UserBean] No new password provided, keeping existing password");
        user.setPassword(existingUser.getPassword());
    } else {
        // Hash new password if needed
        if (PasswordUtil.needsHashing(user.getPassword())) {
            logger.debug(" Hashing new password with BCrypt...");
            user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        }
    }

    user.setId(Long.parseLong(userId));

    // Set default status
    if (user.getStatus() == null || user.getStatus().isEmpty()) {
        user.setStatus("Active");
    }

    logger.debug("Validating user data...");
    ValidationResult validationResult = userValidator.validate(user);
    if (!validationResult.isValid()) {
        logger.error(" Validation failed!");
        throw new IllegalArgumentException("User validation failed: " + validationResult.getErrorMessages());
    }
    logger.debug("[UserBean] Validation passed ✓");

    //  Update user in database
    logger.debug("[UserBean] Updating user in database...");
    if (existingUser instanceof Mentor existingMentor) {
        Mentor mentor = new Mentor();
        mentor.setId(existingMentor.getId());
        mentor.setUsername(user.getUsername());
        mentor.setPassword(user.getPassword());
        mentor.setRole(user.getRole());
        mentor.setEmail(user.getEmail());
        mentor.setStatus(user.getStatus());
        mentor.setCreatedAt(user.getCreatedAt());
        mentor.setUpdatedAt(user.getUpdatedAt());
        mentorDAO.update(mentor);
    } else if (existingUser instanceof Mentee existingMentee) {
        Mentee mentee = new Mentee();
        mentee.setId(existingMentee.getId());
        mentee.setUsername(user.getUsername());
        mentee.setPassword(user.getPassword());
        mentee.setRole(user.getRole());
        mentee.setEmail(user.getEmail());
        mentee.setStatus(user.getStatus());
        mentee.setCreatedAt(user.getCreatedAt());
        mentee.setUpdatedAt(user.getUpdatedAt());
        menteeDAO.update(mentee);
    } else {
        throw new IllegalStateException("Unsupported account type: " + existingUser.getClass().getSimpleName());
    }
    logger.info("[UserBean] User updated successfully");

    // Fire CRUD event
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

        // Check if user exists
        logger.debug("[UserBean] Checking if user exists...");
        User user = getUserById(userId);
        if (user == null) {
            logger.error("[UserBean] User not found!");
            throw new IllegalArgumentException("User with ID '" + userId + "' not found");
        }
        logger.debug("[UserBean] User found ✓");

        // Delete user from database
        logger.debug("[UserBean] Deleting user from database...");
        if (user instanceof Mentor) {
            mentorDAO.delete(Long.parseLong(userId));
        } else if (user instanceof Mentee) {
            menteeDAO.delete(Long.parseLong(userId));
        } else {
            throw new IllegalStateException("Unsupported account type: " + user.getClass().getSimpleName());
        }
        logger.info("[UserBean] User deleted successfully");

        // Fire CRUD event for audit trail
        auditTrailEvent.fire(new AuditTrail(
            "User",
            userId,
            "DELETE",
            "ADMIN",
            "User deleted: " + user.getUsername()
        ));

        logger.info("=== User Deletion Completed Successfully ===");
    }

}
