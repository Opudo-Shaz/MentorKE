package app.filter;

import app.utility.logging.AppLogger;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;

@WebFilter(
        filterName = "AuthenticationFilter",
        urlPatterns = {
                "/app/*"
        }
)
public class AuthenticationFilter implements Filter {

    private static final Logger logger =
            AppLogger.getLogger(AuthenticationFilter.class);

    private static final Map<String, String> ROLE_MAPPINGS = Map.of(
            "/admin", "admin",
            "/user-management", "admin",
            "/mentor-management", "admin",
            "/mentee-management", "admin",
            "/mentor-dashboard", "mentor",
            "/mentee-dashboard", "mentee"
    );

    private static final java.util.Set<String> PUBLIC_PATHS = java.util.Set.of(
            "/",
            "/home",
            "/home/",
            "/about",
            "/about/",
            "/login",
            "/login/",
            "/register",
            "/register/"
    );

    @Override
    public void init(FilterConfig filterConfig) {

        logger.debug("[AuthenticationFilter] Filter initialized");
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

                String requestPath = httpRequest.getPathInfo();
                if (requestPath == null || requestPath.isEmpty()) {
                        requestPath = httpRequest.getServletPath();
                }

                if (requestPath == null || requestPath.isEmpty()) {
                        requestPath = httpRequest.getRequestURI();
                }

                if (requestPath != null && requestPath.startsWith("/app/")) {
                        requestPath = requestPath.substring(4);
                }

        if (requestPath != null && PUBLIC_PATHS.contains(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        logger.debug(
                "[AuthenticationFilter] Checking authentication for: {}",
                requestPath
        );

        HttpSession session = httpRequest.getSession(false);

        if (session == null ||
                !Boolean.TRUE.equals(
                        session.getAttribute("isLoggedIn"))
        ) {

            if (ROLE_MAPPINGS.get(requestPath) == null) {
                chain.doFilter(request, response);
                return;
            }

            logger.warn(
                    "[AuthenticationFilter] No valid session"
            );

            redirectToLogin(httpRequest, httpResponse);

            return;
        }

        String userRole =
                String.valueOf(session.getAttribute("role"));

        String username =
                String.valueOf(session.getAttribute("username"));

        logger.info(
                "[AuthenticationFilter] User authenticated: {} ({})",
                username,
                userRole
        );

        String requiredRole = ROLE_MAPPINGS.get(requestPath);

        if (requiredRole != null &&
                !requiredRole.equalsIgnoreCase(userRole)) {

            logger.warn(
                    "[AuthenticationFilter] Access denied for user: {}",
                    username
            );

            redirectToLogin(httpRequest, httpResponse);

            return;
        }

        logger.debug(
                "[AuthenticationFilter] Access granted"
        );

        chain.doFilter(request, response);
    }

    private void redirectToLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/app/login/"
        );
    }

    @Override
    public void destroy() {

        logger.debug("[AuthenticationFilter] Filter destroyed");
    }
}