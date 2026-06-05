package app.utility.helper;

import at.favre.lib.crypto.bcrypt.BCrypt;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

/**
 * PasswordUtil - Utility class for password hashing and verification
 * Uses BCrypt for secure password storage
 */
public class PasswordUtil {

    private static final Logger logger = AppLogger.getLogger(PasswordUtil.class);

    // Default cost factor for BCrypt
    private static final int BCRYPT_COST = 10; 

    /**
     * Hash a plain text password using BCrypt
     * @param plainPassword The plain text password
     * @return The hashed password
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
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The BCrypt hash to verify against
     * @return True if password matches, false otherwise
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
     * @param hash The hash to check
     * @return True if it's a BCrypt hash, false otherwise
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
     * @param password The password to check
     * @return True if the password needs hashing, false if it's already hashed
     */
    public static boolean needsHashing(String password) {
        return password != null && !isBcryptHash(password);
    }
}


