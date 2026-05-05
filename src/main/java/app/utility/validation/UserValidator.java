package app.utility.validation;

import app.model.User;
import jakarta.enterprise.context.Dependent;
import java.util.regex.Pattern;

/**
 * UserValidator - Validates User entities
 *
 * Uses @Dependent scope since:
 * - Validator is stateless (no shared state)
 * - All validations are method-level
 * - Avoids Weld WELD-001435 proxy issues
 * - Consistent with other validators in the application
 */
@Dependent
@ValidatorQualifier(ValidatorQualifier.ValidationChoice.USER)
public class UserValidator implements Validator<User> {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    private static final int MIN_EMAIL_LENGTH = 5;
    private static final int MAX_EMAIL_LENGTH = 150;
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN);
    private static final String[] VALID_ROLES = {"admin", "mentor", "mentee"};

    @Override
    public ValidationResult validate(User user) {
        ValidationResult result = new ValidationResult("User");

        if (user == null) {
            result.addError("User object cannot be null");
            return result;
        }

        validateUsername(user, result);
        validatePassword(user, result);
        validateEmail(user, result);
        validateRole(user, result);
        validateStatus(user, result);

        return result;
    }

    @Override
    public ValidatorQualifier.ValidationChoice getValidationChoice() {
        return ValidatorQualifier.ValidationChoice.USER;
    }

    /**
     * Validate username field
     */
    private void validateUsername(User user, ValidationResult result) {
        String username = user.getUsername();

        if (username == null || username.trim().isEmpty()) {
            result.addError("Username is required");
            return;
        }

        username = username.trim();

        if (username.length() < MIN_USERNAME_LENGTH) {
            result.addError("Username must be at least " + MIN_USERNAME_LENGTH + " characters long");
        }

        if (username.length() > MAX_USERNAME_LENGTH) {
            result.addError("Username cannot exceed " + MAX_USERNAME_LENGTH + " characters");
        }

        if (!username.matches("^[a-zA-Z0-9_.-]+$")) {
            result.addError("Username can only contain letters, numbers, underscores, dots, and hyphens");
        }
    }

    /**
     * Validate password field
     */
    private void validatePassword(User user, ValidationResult result) {
        String password = user.getPassword();

        if (password == null || password.trim().isEmpty()) {
            result.addError("Password is required");
            return;
        }

        password = password.trim();

        if (password.length() < MIN_PASSWORD_LENGTH) {
            result.addError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            result.addError("Password cannot exceed " + MAX_PASSWORD_LENGTH + " characters");
        }

        // Check for password strength
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};:'\",.<>?/\\\\|`~].*");

        if (!hasUpperCase || !hasLowerCase || !hasDigit) {
            result.addWarning("Password should contain uppercase, lowercase, and digits for better security");
        }
    }

    /**
     * Validate email field
     */
    private void validateEmail(User user, ValidationResult result) {
        String email = user.getEmail();

        if (email == null || email.trim().isEmpty()) {
            result.addError("Email is required");
            return;
        }

        email = email.trim();

        if (email.length() < MIN_EMAIL_LENGTH) {
            result.addError("Email must be at least " + MIN_EMAIL_LENGTH + " characters long");
        }

        if (email.length() > MAX_EMAIL_LENGTH) {
            result.addError("Email cannot exceed " + MAX_EMAIL_LENGTH + " characters");
        }

        if (!EMAIL_REGEX.matcher(email).matches()) {
            result.addError("Email format is invalid. Use format like: user@example.com");
        }
    }

    /**
     * Validate role field
     */
    private void validateRole(User user, ValidationResult result) {
        String role = user.getRole();

        if (role == null || role.trim().isEmpty()) {
            result.addError("Role is required");
            return;
        }

        role = role.trim().toLowerCase();

        boolean validRole = false;
        for (String validRole_item : VALID_ROLES) {
            if (role.equals(validRole_item)) {
                validRole = true;
                break;
            }
        }

        if (!validRole) {
            result.addError("Role must be one of: admin, mentor, mentee");
        }
    }

    /**
     * Validate status field
     */
    private void validateStatus(User user, ValidationResult result) {
        String status = user.getStatus();

        if (status == null || status.trim().isEmpty()) {
            result.addError("Status is required");
            return;
        }

        status = status.trim().toLowerCase();

        if (!status.equals("active") && !status.equals("inactive") && !status.equals("pending")) {
            result.addError("Status must be one of: Active, Inactive, Pending");
        }
    }
}

