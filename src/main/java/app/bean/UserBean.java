package app.bean;

import app.bean.event.CRUDEvent;
import app.bean.event.UserRegisteredEvent;
import app.dao.UserDAO;
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

import java.sql.SQLException;
import java.util.List;


@Stateless
@Named("userBean")
public class UserBean {

    private static final Logger logger = AppLogger.getLogger(UserBean.class);

    private UserDAO userDAO;

    @Inject
    private Event<CRUDEvent> crudEventFirer;

    @Inject
    private Event<UserRegisteredEvent> userRegisteredEvent;

    @Inject
    @ValidatorQualifier(ValidatorQualifier.ValidationChoice.USER)
    private Validator<User> userValidator;

    // PUBLIC NO-ARG CONSTRUCTOR (required by CDI/EJB)
    public UserBean() {
        logger.debug("[UserBean] CDI Bean initialized with default constructor");
    }

    // CONSTRUCTOR INJECTION (alternative)
    @Inject
    public UserBean(UserDAO userDAO) {
        this.userDAO = userDAO;
        logger.debug("[UserBean] CDI Bean initialized with constructor injection");
    }

    /**
     * CREATE - Register a new user
     */
    public void registerUser(User user) throws SQLException {
        logger.info("[UserBean] === Starting User Registration ===");
        logger.info("[UserBean] Username: {} , Email: {} , Role: {}", user.getUsername(), user.getEmail(), user.getRole());

        // Step 1: Check if username already exists
        logger.debug("[UserBean] Checking if username exists...");
        User existingUser = userDAO.getUserByUsername(user.getUsername());
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

        // Step 3: Add user to database
        logger.debug("[UserBean] Adding user to database...");
        user.setStatus("Active");
        userDAO.addUser(user);
        logger.info("[UserBean] User registered successfully, ID: {}", user.getId());

        // Step 4: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "User",
            user.getId(),
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
    public User getUserById(String userId) throws SQLException {
        logger.debug("[UserBean] Fetching user by ID: {}", userId);
        return userDAO.getUser(userId);
    }

    /**
     * READ - Get user by username
     */
    public User getUserByUsername(String username) throws SQLException {
        logger.debug("[UserBean] Fetching user by username: {}", username);
        return userDAO.getUserByUsername(username);
    }

    /**
     * READ - Get all users
     */
    public List<User> getAllUsers() throws SQLException {
        logger.debug("[UserBean] Fetching all users");
        return userDAO.getAllUsers();
    }

    /**
     * UPDATE - Update existing user
     */
    public void updateUser(String userId, User user) throws SQLException {
    logger.info("[UserBean] === Updating user ===");
    logger.debug("[UserBean] User ID: {}", userId);

    // Step 1: Check if user exists
    logger.debug("[UserBean] Checking if user exists...");
    User existingUser = userDAO.getUser(userId);
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
    user.setId(userId);

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
    userDAO.updateUser(userId, user);
    logger.info("[UserBean] User updated successfully");

    // Step 7: Fire CRUD event
    crudEventFirer.fire(new CRUDEvent(
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
    public void deleteUser(String userId) throws SQLException {
        logger.info("[UserBean] === Deleting user ===");
        logger.debug("[UserBean] User ID: {}", userId);

        // Step 1: Check if user exists
        logger.debug("[UserBean] Checking if user exists...");
        User user = userDAO.getUser(userId);
        if (user == null) {
            logger.error("[UserBean] User not found!");
            throw new IllegalArgumentException("User with ID '" + userId + "' not found");
        }
        logger.debug("[UserBean] User found ✓");

        // Step 2: Delete user from database
        logger.debug("[UserBean] Deleting user from database...");
        userDAO.deleteUser(userId);
        logger.info("[UserBean] User deleted successfully");

        // Step 3: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "User",
            userId,
            "DELETE",
            "ADMIN",
            "User deleted: " + user.getUsername()
        ));

        logger.info("[UserBean] === User Deletion Completed Successfully ===");
    }
}
