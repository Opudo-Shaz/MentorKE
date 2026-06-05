package app.action;

import app.bean.UserBean;
import app.model.User;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.framework.ActionPostMethod;
import app.utility.helper.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Action("login")
public class LoginAction extends BaseAction {

    @Inject
    private UserBean userBean;

    @ActionGetMethod("")
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String action = request.getParameter("action");
        if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();
            redirect(response, request.getContextPath() + "/app/login/");
            return;
        }

        if (isLoggedIn(request)) {
            redirectToDashboard(request, response, getUserRole(request));
            return;
        }

        forward(request, response, "/login.jsp");
    }

    @ActionPostMethod("")
    public void post(HttpServletRequest request, HttpServletResponse response) throws Exception {
        handleLogin(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = safe(request.getParameter("username"));
        String password = safe(request.getParameter("password"));
        String role     = safe(request.getParameter("role")).toLowerCase();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            setAttribute(request, "errorMessage", "All fields are required.");
            forward(request, response, "/login.jsp");
            return;
        }

        // Admin via web.xml params
        if ("admin".equals(role)) {
            String adminUsername = request.getServletContext().getInitParameter("app.admin.username");
            String adminPassword = request.getServletContext().getInitParameter("app.admin.password");

            if (!username.equals(adminUsername) || !password.equals(adminPassword)) {
                setAttribute(request, "errorMessage", "Invalid admin credentials.");
                forward(request, response, "/login.jsp");
                return;
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("username", username);
            session.setAttribute("role", "admin");
            session.setAttribute("loginTime", System.currentTimeMillis());
            redirectToDashboard(request, response, "admin");
            return;
        }

        if ("mentor".equals(role) || "mentee".equals(role)) {
            User user = userBean.getUserByUsername(username);

            if (user == null) {
                setAttribute(request, "errorMessage", "No account found with that username.");
                forward(request, response, "/login.jsp");
                return;
            }

            // Verify password using PasswordUtil (supports both plain text and BCrypt hashes)
            boolean passwordValid = false;
            if (PasswordUtil.isBcryptHash(user.getPassword())) {
                passwordValid = PasswordUtil.verifyPassword(password, user.getPassword());
            } else {
                
                // Fallback to plain text comparison for backwards compatibility
                passwordValid = password.equals(user.getPassword());
            }

            if (!passwordValid) {
                setAttribute(request, "errorMessage", "Invalid username or password.");
                forward(request, response, "/login.jsp");
                return;
            }

            if (!role.equalsIgnoreCase(user.getRole())) {
                setAttribute(request, "errorMessage", "Invalid username or password.");
                forward(request, response, "/login.jsp");
                return;
            }

            if (!"Active".equalsIgnoreCase(user.getStatus())) {
                setAttribute(request, "errorMessage", "Your account is inactive. Please contact an administrator.");
                forward(request, response, "/login.jsp");
                return;
            }

            // Establish the security principal using request.login()
            // This is required for the container to recognize the authenticated user for @RolesAllowed checks
            try {
                request.login(username, password);
            } catch (jakarta.servlet.ServletException e) {
                Logger.getLogger(LoginAction.class.getName()).log(Level.WARNING, "Failed to establish security context for user: " + username, e);
                // Continue to set session attributes even if container login fails
            }

            // Set session attributes for legacy session-based authorization
            HttpSession session = request.getSession(true);
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("username",   user.getUsername());
            session.setAttribute("role",       user.getRole().toLowerCase());
            session.setAttribute("userId",     user.getId());
            session.setAttribute("loginTime",  System.currentTimeMillis());

            Logger logger = Logger.getLogger(LoginAction.class.getName());
            logger.log(Level.INFO, "=== LOGIN SUCCESSFUL ===");
            logger.log(Level.INFO, "Session ID: " + session.getId());
            logger.log(Level.INFO, "Username: " + user.getUsername());
            logger.log(Level.INFO, "Role: " + user.getRole().toLowerCase());
            logger.log(Level.INFO, "Session isLoggedIn: " + session.getAttribute("isLoggedIn"));
            logger.log(Level.INFO, "Session role: " + session.getAttribute("role"));
            logger.log(Level.INFO, "Redirecting to: " + user.getRole());

            redirectToDashboard(request, response, user.getRole());
            return;
        }

        setAttribute(request, "errorMessage", "Invalid role selected.");
        forward(request, response, "/login.jsp");
    }

    private void redirectToDashboard(HttpServletRequest request, HttpServletResponse response, String role) throws java.io.IOException {
        String ctx = request.getContextPath();
        if ("admin".equalsIgnoreCase(role)) {
            response.sendRedirect(ctx + "/app/admin/");
        } else if ("mentor".equalsIgnoreCase(role)) {
            response.sendRedirect(ctx + "/app/mentor-dashboard/");
        } else if ("mentee".equalsIgnoreCase(role)) {
            response.sendRedirect(ctx + "/app/mentee-dashboard/");
        } else {
            response.sendRedirect(ctx + "/app/login/");
        }
    }

    private String safe(String value) {

        return value == null ? "" : value.trim();
    }
}
