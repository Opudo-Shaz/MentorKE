package app.listener;

import app.utility.logging.AppLogger;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;

@WebListener
public class SessionTrackingListener implements HttpSessionListener {

    private static final Logger logger = AppLogger.getLogger(SessionTrackingListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();

        // Increment session count
        Integer sessionCount = (Integer) context.getAttribute("sessionCount");
        if (sessionCount == null) {
            sessionCount = 0;
        }
        sessionCount++;
        context.setAttribute("sessionCount", sessionCount);

        logger.info("[SessionTrackingListener] Session created: {}", se.getSession().getId());
        logger.info("[SessionTrackingListener] Active session count: {}", sessionCount);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();

        // Decrement session count
        Integer sessionCount = (Integer) context.getAttribute("sessionCount");
        if (sessionCount != null && sessionCount > 0) {
            sessionCount--;
            context.setAttribute("sessionCount", sessionCount);
        }

        String username = (String) se.getSession().getAttribute("username");
        String role = (String) se.getSession().getAttribute("role");

        logger.info("[SessionTrackingListener] Session destroyed: {}", se.getSession().getId());
        if (username != null) {
            logger.info("[SessionTrackingListener] Logged out user: {} (role: {})", username, role);
        }
        logger.info("[SessionTrackingListener] Active sessions now: {}", (sessionCount != null ? sessionCount : 0));
    }
}

