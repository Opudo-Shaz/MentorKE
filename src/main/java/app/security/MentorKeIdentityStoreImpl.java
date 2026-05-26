package app.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import app.dao.UserDAO;
import app.model.User;
import app.utility.helper.PasswordUtil;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.util.Collections;

@ApplicationScoped
public class MentorKeIdentityStoreImpl implements IdentityStore, MentorKeIdentityStore {

    private static final Logger logger = AppLogger.getLogger(MentorKeIdentityStoreImpl.class);

    @Inject
    private UserDAO userDAO;

    @Override
    public CredentialValidationResult validate(UsernamePasswordCredential credential) {
        try {
            String username = credential.getCaller();
            String password = credential.getPasswordAsString();

            logger.debug("Validating credential for username: {}", username);

            // Fetch user from database
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                logger.warn("User not found: {}", username);
                return CredentialValidationResult.INVALID_RESULT;
            }

            // Check if account is active
            if (user.getStatus() != null && !"Active".equalsIgnoreCase(user.getStatus())) {
                logger.warn("User account is not active: {}", username);
                return CredentialValidationResult.INVALID_RESULT;
            }

            // Verify password using PasswordUtil
            // First try BCrypt verification (recommended for hashed passwords)
            if (PasswordUtil.isBcryptHash(user.getPassword())) {
                if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
                    logger.warn("Invalid password for user: {}", username);
                    return CredentialValidationResult.INVALID_RESULT;
                }
            } else {
                // Fallback to plain text comparison for backwards compatibility during transition
                // TODO: Migrate existing passwords to BCrypt
                if (!password.equals(user.getPassword())) {
                    logger.warn("Invalid password for user: {}", username);
                    return CredentialValidationResult.INVALID_RESULT;
                }
            }

            logger.info("Authentication successful for user: {}", username);

            // Return successful validation with user role
            return new CredentialValidationResult(
                    username,
                    Collections.singleton(user.getRole().toLowerCase())
            );

        } catch (Exception e) {
            logger.error("Error validating credential", e);
            return CredentialValidationResult.INVALID_RESULT;
        }
    }
}