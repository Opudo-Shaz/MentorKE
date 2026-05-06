package app.utility.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized logging utility for the MentorKE application.
 * Provides convenient methods for logging at different levels.
 */
public class AppLogger {
    
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("app.audit");
    
    /**
     * Get logger for a specific class
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Get logger for a specific name
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
    
    /**
     * Log audit event (security-sensitive operations)
     */
    public static void auditInfo(String message) {
        AUDIT_LOGGER.info(message);
    }
    
    /**
     * Log audit event with parameters
     */
    public static void auditInfo(String message, Object... params) {
        AUDIT_LOGGER.info(message, params);
    }
    
    /**
     * Log audit error
     */
    public static void auditError(String message, Throwable throwable) {
        AUDIT_LOGGER.error(message, throwable);
    }
    
    /**
     * Log audit warning
     */
    public static void auditWarn(String message) {
        AUDIT_LOGGER.warn(message);
    }
}
