package app;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SessionTrackingListener implements HttpSessionListener {

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

        System.out.println("[SessionTrackingListener] Session created: " + se.getSession().getId());
        System.out.println("[SessionTrackingListener] Active session count: " + sessionCount);
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

        System.out.println("[SessionTrackingListener] Session destroyed: " + se.getSession().getId());
        if (username != null) {
            System.out.println("[SessionTrackingListener] Logged out user: " + username + " (role: " + role + ")");
        }
        System.out.println("[SessionTrackingListener] Active sessions now: " + (sessionCount != null ? sessionCount : 0));
    }
}

