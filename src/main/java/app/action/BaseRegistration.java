package app.action;
import app.model.User;
import app.dao.UserDAO;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;


public abstract class BaseRegistration<T extends User> {
    protected String username;
    protected String password;
    protected String email;
    protected String role;
    protected UserDAO userDAO;
    protected List<String> errors = new ArrayList<>();

    public BaseRegistration(String username, String password, String email, String role, UserDAO userDAO) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.userDAO = userDAO;
    }

    public boolean register() {
        System.out.println("[BaseRegistration] Starting registration for role: " + role);
        errors.clear();
        // Common validation
        validateCommon();
        if (!errors.isEmpty()) {
            System.out.println("[BaseRegistration] Common validation failed: " + errors);
            return false;
        }
        // Role-specific validation
        validateRoleSpecific();
        if (!errors.isEmpty()) {
            System.out.println("[BaseRegistration] Role-specific validation failed: " + errors);
            return false;
        }
        // Create and persist user to database
        try {
            T user = createUser();
            userDAO.addUser(user);
            System.out.println("[BaseRegistration] User registered successfully: " + username + " (" + role + ")");
            return true;
        } catch (SQLException e) {
            System.err.println("[BaseRegistration] Error persisting user to database: " + e.getMessage());
            errors.add("Database error: " + e.getMessage());
            return false;
        }
    }
    protected void validateCommon() {
        System.out.println("[BaseRegistration] Running common validation...");
        if (username == null || username.trim().isEmpty()) {
            errors.add("Username is required");
        } else if (username.length() < 3) {
            errors.add("Username must be at least 3 characters");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.add("Password is required");
        } else if (password.length() < 6) {
            errors.add("Password must be at least 6 characters");
        }
        if (email == null || email.trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!isValidEmail(email)) {
            errors.add("Invalid email format");
        }
    }
    protected abstract void validateRoleSpecific();
    protected abstract T createUser();
    protected boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    public List<String> getErrors() {
        return errors;
    }
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
