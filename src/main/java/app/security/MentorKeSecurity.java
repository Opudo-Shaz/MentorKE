package app.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;


@RequestScoped
public class MentorKeSecurity {

    private static final Logger logger = AppLogger.getLogger(MentorKeSecurity.class);

    @Inject
    private SecurityContext securityContext;

    @Inject
    private HttpServletRequest request;

    /**
     * Get currently logged-in username
     */
    public String getCurrentUsername() {
        try {
            if (securityContext != null && securityContext.getCallerPrincipal() != null) {
                String username = securityContext.getCallerPrincipal().getName();
                logger.debug("Current user: {}", username);
                return username;
            }
        } catch (Exception e) {
            logger.debug(" Error getting current user", e);
        }
        return null;
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {

        return getCurrentUsername() != null;
    }

    /**
     * Check if current user is a mentor
     */
    public boolean isMentor() {
        return hasRole("mentor");
    }

    /**
     * Check if current user is a mentee
     */
    public boolean isMentee() {

        return hasRole("mentee");
    }

    /**
     * Check if current user is an admin
     */
    public boolean isAdmin() {

        return hasRole("admin");
    }

    /**
     * Check if current user has specific role
     */
    public boolean hasRole(String role) {
        try {
            // First try SecurityContext
            if (securityContext != null && role != null) {
                boolean hasRole = securityContext.isCallerInRole(role);
                if (hasRole) {
                    logger.debug(" User has role (SecurityContext): {}", role);
                    return hasRole;
                }
            }
        } catch (Exception e) {
            logger.debug("Error checking role in SecurityContext: {}", role, e);
        }

        // Fallback to session-based role
        try {
            HttpSession session = request.getSession(false);
            if (session != null && role != null) {
                String sessionRole = (String) session.getAttribute("role");
                logger.info("Checking session for role '{}'. Session ID: {}. Session role: {}",
                           role, session.getId(), sessionRole);

                if (sessionRole != null && sessionRole.equalsIgnoreCase(role)) {
                    logger.info("User has role (Session): {}", role);
                    return true;
                } else {
                    logger.warn("Session role '{}' does not match required role '{}'", sessionRole, role);
                }
            } else {
                logger.warn("Session is null or role is null. Session: {}, Role: {}", session, role);
            }
        } catch (Exception e) {
            logger.error("Error checking role in session: {}", role, e);
        }

        logger.warn("User does NOT have required role: {}", role);
        return false;
    }

    /**
     * Check if current user has any of the given roles
     */
    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current user has all the given roles
     */
    public boolean hasAllRoles(String... roles) {
        for (String role : roles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Require authentication - throws SecurityException if not logged in
     */
    public void requireAuthentication() {
        if (!isAuthenticated()) {
            logger.warn(" Authentication required but user not logged in");
            throw new SecurityException("User must be authenticated");
        }
    }

    /**
     * Require specific role - throws SecurityException if user lacks role
     */
    public void requireRole(String role) {
        if (!hasRole(role)) {
            logger.warn(" User lacks required role: {}", role);
            throw new SecurityException("User must have role: " + role);
        }
    }

    /**
     * Require any of the given roles
     */
    public void requireAnyRole(String... roles) {
        if (!hasAnyRole(roles)) {
            logger.warn(" User lacks any of required roles");
            throw new SecurityException("User must have one of the roles: " + String.join(", ", roles));
        }
    }


     // Require all the given roles

    public void requireAllRoles(String... roles) {
        if (!hasAllRoles(roles)) {
            logger.warn(" User lacks all required roles");
            throw new SecurityException("User must have all of the roles: " + String.join(", ", roles));
        }
    }
}

