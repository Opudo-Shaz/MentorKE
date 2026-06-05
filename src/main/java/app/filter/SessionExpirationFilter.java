package app.filter;

import app.utility.logging.AppLogger;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Set;

/**
 * This filter checks if a user tries to access a protected resource without a valid session.
 * If the session is null or expired, it redirects to the login page.
 */
@WebFilter(
        filterName = "SessionExpirationFilter",
        urlPatterns = {"/app/*"}
)
public class SessionExpirationFilter implements Filter {

    private static final Logger logger = AppLogger.getLogger(SessionExpirationFilter.class);

    // Protected paths that require authentication
    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/mentor-dashboard",
            "/mentee-dashboard",
            "/mentor-management",
            "/mentee-management",
            "/admin",
            "/user-management",
            "/sessions"
    );

    // Public paths that don't require authentication
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/",
            "/home",
            "/home/",
            "/about",
            "/about/",
            "/login",
            "/login/",
            "/register",
            "/register/",
            "/index",
            "/index/"
    );

    @Override
    public void init(FilterConfig filterConfig) {
        logger.debug("[SessionExpirationFilter] Filter initialized");
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Extract the request path
        String requestPath = httpRequest.getPathInfo();
        if (requestPath == null || requestPath.isEmpty()) {
            requestPath = httpRequest.getServletPath();
        }

        // Normalize: remove /app prefix if present
        if (requestPath != null && requestPath.startsWith("/app")) {
            requestPath = requestPath.substring(4);
        }

        // Remove trailing slashes for comparison (but keep root '/')
        if (requestPath != null && requestPath.length() > 1 && requestPath.endsWith("/")) {
            requestPath = requestPath.substring(0, requestPath.length() - 1);
        }

        logger.debug(" Request path: {}", requestPath);

        // Check if this is a public path
        if (isPublicPath(requestPath)) {
            logger.debug("Public path, allowing request");
            chain.doFilter(request, response);
            return;
        }

        // Check if this is a protected path
        if (isProtectedPath(requestPath)) {
            logger.debug(" Protected path, checking session");

            HttpSession session = httpRequest.getSession(false);

            // Session is null or expired
            if (session == null) {
                logger.warn(" Session is null/expired for path: {}", requestPath);
                logger.warn(" Redirecting to login");
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/app/login/?error=session_expired");
                return;
            }

            // Check if user is logged in via session attributes
            Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
            String userRole = (String) session.getAttribute("role");

            if (isLoggedIn == null || !isLoggedIn || userRole == null) {
                logger.warn(" User not logged in or session invalid for path: {}", requestPath);
                logger.warn(" Redirecting to login");
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/app/login/?error=not_authenticated");
                return;
            }

            logger.debug(" Session valid, user logged in");
        }

        // Allow the request to proceed
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.debug(" Filter destroyed");
    }


     // Check if a path is protected and requires authentication

    private boolean isProtectedPath(String path) {
        if (path == null) {
            return false;
        }

        for (String protectedPath : PROTECTED_PATHS) {
            if (path.startsWith(protectedPath)) {
                return true;
            }
        }
        return false;
    }


     // Check if a path is public and doesn't require authentication

    private boolean isPublicPath(String path) {
        if (path == null) {
            return false;
        }

        return PUBLIC_PATHS.contains(path);
    }
}

