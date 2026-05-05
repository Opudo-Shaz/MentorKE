package app.bean;

import app.bean.event.CRUDEvent;
import app.bean.event.UserRegisteredEvent;
import app.dao.UserDAO;
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
@Named("userBean")
public class UserBean {

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
        System.out.println("[UserBean] CDI Bean initialized with default constructor");
    }

    // CONSTRUCTOR INJECTION (alternative)
    @Inject
    public UserBean(UserDAO userDAO) {
        this.userDAO = userDAO;
        System.out.println("[UserBean] CDI Bean initialized with constructor injection");
    }

    /**
     * CREATE - Register a new user
     */
    public void registerUser(User user) throws SQLException {
        System.out.println("[UserBean] === Starting User Registration ===");
        System.out.println("[UserBean] Username: " + user.getUsername() +
                         ", Email: " + user.getEmail() +
                         ", Role: " + user.getRole());

        // Step 1: Check if username already exists
        System.out.println("[UserBean] Checking if username exists...");
        User existingUser = userDAO.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            System.err.println("[UserBean] Username already exists!");
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' already exists");
        }
        System.out.println("[UserBean] Username check passed ✓");

        // Step 2: Validate user data
        System.out.println("[UserBean] Validating user data...");
        ValidationResult validationResult = userValidator.validate(user);
        if (!validationResult.isValid()) {
            System.err.println("[UserBean] Validation failed!");
            throw new IllegalArgumentException("User validation failed: " + validationResult.getErrorMessages());
        }
        System.out.println("[UserBean] Validation passed ✓");

        // Step 3: Add user to database
        System.out.println("[UserBean] Adding user to database...");
        user.setStatus("Active");
        userDAO.addUser(user);
        System.out.println("[UserBean] User registered successfully, ID: " + user.getId());

        // Step 4: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "User",
            user.getId(),
            "CREATE",
            "SYSTEM",
            "User registered: " + user.getUsername()
        ));

        // Step 5: Fire email event (will be handled asynchronously by EmailObserverBean)
        System.out.println("[UserBean] Firing email registration event...");
        userRegisteredEvent.fire(new UserRegisteredEvent(
            user.getEmail(),
            user.getUsername(),
            user.getRole()
        ));

        System.out.println("[UserBean] === User Registration Completed Successfully ===");
    }

    /**
     * READ - Get user by ID
     */
    public User getUserById(String userId) throws SQLException {
        System.out.println("[UserBean] Fetching user by ID: " + userId);
        return userDAO.getUser(userId);
    }

    /**
     * READ - Get user by username
     */
    public User getUserByUsername(String username) throws SQLException {
        System.out.println("[UserBean] Fetching user by username: " + username);
        return userDAO.getUserByUsername(username);
    }

    /**
     * READ - Get all users
     */
    public List<User> getAllUsers() throws SQLException {
        System.out.println("[UserBean] Fetching all users");
        return userDAO.getAllUsers();
    }

    /**
     * UPDATE - Update existing user
     */
    public void updateUser(String userId, User user) throws SQLException {
        System.out.println("[UserBean] === Updating user ===");
        System.out.println("[UserBean] User ID: " + userId);

        // Step 1: Check if user exists
        System.out.println("[UserBean] Checking if user exists...");
        User existingUser = userDAO.getUser(userId);
        if (existingUser == null) {
            System.err.println("[UserBean] User not found!");
            throw new IllegalArgumentException("User with ID '" + userId + "' not found");
        }
        System.out.println("[UserBean] User found ✓");

        // Step 2: Validate updated user data
        System.out.println("[UserBean] Validating user data...");
        user.setId(userId);
        ValidationResult validationResult = userValidator.validate(user);
        if (!validationResult.isValid()) {
            System.err.println("[UserBean] Validation failed!");
            throw new IllegalArgumentException("User validation failed: " + validationResult.getErrorMessages());
        }
        System.out.println("[UserBean] Validation passed ✓");

        // Step 3: If password is empty, keep existing password
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        }

        // Step 4: Set default status if needed
        if (user.getStatus() == null || user.getStatus().isEmpty()) {
            user.setStatus("Active");
        }

        // Step 5: Update user in database
        System.out.println("[UserBean] Updating user in database...");
        userDAO.updateUser(userId, user);
        System.out.println("[UserBean] User updated successfully");

        // Step 6: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "User",
            userId,
            "UPDATE",
            "ADMIN",
            "User updated: " + user.getUsername()
        ));

        System.out.println("[UserBean] === User Update Completed Successfully ===");
    }

    /**
     * DELETE - Delete user
     */
    public void deleteUser(String userId) throws SQLException {
        System.out.println("[UserBean] === Deleting user ===");
        System.out.println("[UserBean] User ID: " + userId);

        // Step 1: Check if user exists
        System.out.println("[UserBean] Checking if user exists...");
        User user = userDAO.getUser(userId);
        if (user == null) {
            System.err.println("[UserBean] User not found!");
            throw new IllegalArgumentException("User with ID '" + userId + "' not found");
        }
        System.out.println("[UserBean] User found ✓");

        // Step 2: Delete user from database
        System.out.println("[UserBean] Deleting user from database...");
        userDAO.deleteUser(userId);
        System.out.println("[UserBean] User deleted successfully");

        // Step 3: Fire CRUD event for audit trail
        crudEventFirer.fire(new CRUDEvent(
            "User",
            userId,
            "DELETE",
            "ADMIN",
            "User deleted: " + user.getUsername()
        ));

        System.out.println("[UserBean] === User Deletion Completed Successfully ===");
    }
}
