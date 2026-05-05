package app.filter;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(
    filterName = "AuthenticationFilter",
    urlPatterns = {"/admin", "/mentor-dashboard",
            "/mentee-dashboard", "/user-management"}
)
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[AuthenticationFilter] Filter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestPath = httpRequest.getRequestURI();

        System.out.println("[AuthenticationFilter] Checking authentication for: " + requestPath);

        // Get session (don't create if not exists)
        HttpSession session = httpRequest.getSession(false);

        // Check if user is logged in
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("[AuthenticationFilter] No valid session - redirecting to login");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        String userRole = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        System.out.println("[AuthenticationFilter] User authenticated: " + username + " (role: " + userRole + ")");

        // Check role-based access
        if (requestPath.contains("/admin") || requestPath.contains("/user-management")) {
            if (!"admin".equalsIgnoreCase(userRole)) {
                System.out.println("[AuthenticationFilter] Admin access denied for user: " + username);
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
                return;
            }
        } else if (requestPath.contains("/mentor-dashboard")) {
            if (!"mentor".equalsIgnoreCase(userRole)) {
                System.out.println("[AuthenticationFilter] Mentor access denied for user: " + username);
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
                return;
            }
        } else if (requestPath.contains("/mentee-dashboard")) {
            if (!"mentee".equalsIgnoreCase(userRole)) {
                System.out.println("[AuthenticationFilter] Mentee access denied for user: " + username);
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
                return;
            }
        }

        System.out.println("[AuthenticationFilter] Access granted - proceeding with request");
        // Allow request to proceed
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("[AuthenticationFilter] Filter destroyed");
    }
}

