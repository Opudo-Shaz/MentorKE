package app.utility.helper;

import at.favre.lib.crypto.bcrypt.BCrypt;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.security.SecureRandom;

/**
 * PasswordUtil - Utility class for password hashing and verification
 * Uses BCrypt for secure password storage
 */
public class PasswordUtil {

    private static final Logger logger = AppLogger.getLogger(PasswordUtil.class);

    // Default cost factor for BCrypt
    private static final int BCRYPT_COST = 10;

    // Characters used for temporary password generation (no ambiguous chars like 0/O, 1/l/I)
    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int TEMP_PASSWORD_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Hash a plain text password using BCrypt
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        try {
            String hashedPassword = BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray());
            logger.debug("Password hashed successfully");
            return hashedPassword;
        } catch (Exception e) {
            logger.error("Error hashing password", e);
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    /**
     * Verify a plain text password against a BCrypt hash
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
            return result.verified;
        } catch (Exception e) {
            logger.error("Error verifying password", e);
            return false;
        }
    }

    /**
     * Check if a hash is a valid BCrypt hash
     */
    public static boolean isBcryptHash(String hash) {
        if (hash == null) {
            return false;
        }
        return hash.startsWith("$2a$") || hash.startsWith("$2b$") ||
               hash.startsWith("$2x$") || hash.startsWith("$2y$");
    }

    /**
     * Check if a password needs hashing (i.e., it's not already a BCrypt hash)
     */
    public static boolean needsHashing(String password) {
        return password != null && !isBcryptHash(password);
    }

    /**
     * Generate a secure, human-friendly temporary password.
     * Used when an admin creates a user account — the plain value
     * is emailed once to the user and never stored.
     *
     * @return a random 10-character temporary password
     */
    public static String generateTempPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            sb.append(TEMP_PASSWORD_CHARS.charAt(RANDOM.nextInt(TEMP_PASSWORD_CHARS.length())));
        }
        logger.debug("Temporary password generated");
        return sb.toString();
    }
}