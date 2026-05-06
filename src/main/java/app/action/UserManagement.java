package app.action;

import app.bean.UserBean;
import app.model.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.annotation.WebServlet;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

/**
 * UserManagement Servlet - Handles HTTP requests only
 *
 * Responsibilities (HTTP layer only):
 * - Extract request parameters
 * - Verify admin session
 * - Delegate business logic to UserBean
 * - Handle redirects with appropriate success/error messages
 *
 * All validation and business logic is in UserBean
 */
@WebServlet(name = "UserManagement",
        urlPatterns = {"/user-management"})
public class UserManagement extends HttpServlet {

    private static final Logger logger = AppLogger.getLogger(UserManagement.class);

    @Inject
    private UserBean userBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("=== HTTP POST called ===");

        // STEP 1: Verify admin session
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
            logger.warn("Session invalid or not logged in");
            response.sendRedirect("login");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        if (!"admin".equalsIgnoreCase(role)) {
            logger.warn("User is not admin, role={}", role);
            response.sendRedirect("login");
            return;
        }

        // STEP 2: Extract action parameter
        String action = request.getParameter("action");
        logger.info("Action requested: {}", action);

        // STEP 3: Route to bean based on action
        try {
            String redirectParam = null;

            if ("add".equalsIgnoreCase(action)) {
                redirectParam = handleAddUser(request);
            } else if ("update".equalsIgnoreCase(action)) {
                redirectParam = handleUpdateUser(request);
            } else if ("delete".equalsIgnoreCase(action)) {
                redirectParam = handleDeleteUser(request);
            } else {
                logger.warn("Unknown action, redirecting to admin");
                response.sendRedirect("admin?view=users");
                return;
            }

            // STEP 4: Redirect with result
            response.sendRedirect("admin?view=users&" + redirectParam);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            String errorMsg = e.getMessage().replace("User validation failed: ", "");
            response.sendRedirect("admin?view=users&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            response.sendRedirect("admin?view=users&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    /**
     * Handle add user request - only extracts parameters and delegates to bean
     */
    private String handleAddUser(HttpServletRequest request) throws Exception {
        logger.debug("handleAddUser - extracting parameters");

        String username = safe(request.getParameter("username"));
        String password = safe(request.getParameter("password"));
        String role = safe(request.getParameter("role"));
        String email = safe(request.getParameter("email"));
        String status = safe(request.getParameter("status"));

        // Create user object
        User newUser = new User(null, username, password, role, email, status);

        // Delegate to bean (validation & business logic)
        userBean.registerUser(newUser);
        return "success=user_added";
    }

    /**
     * Handle update user request - only extracts parameters and delegates to bean
     */
    private String handleUpdateUser(HttpServletRequest request) throws Exception {
        logger.debug("handleUpdateUser - extracting parameters");

        String userId = safe(request.getParameter("id"));
        String username = safe(request.getParameter("username"));
        String password = safe(request.getParameter("password"));
        String role = safe(request.getParameter("role"));
        String email = safe(request.getParameter("email"));
        String status = safe(request.getParameter("status"));

        // Create user object
        User user = new User(userId, username, password, role, email, status);

        // Delegate to bean (validation & business logic)
        userBean.updateUser(userId, user);
        return "success=user_updated";
    }

    /**
     * Handle delete user request - only extracts parameters and delegates to bean
     */
    private String handleDeleteUser(HttpServletRequest request) throws Exception {
        logger.debug("handleDeleteUser - extracting parameters");

        String userId = safe(request.getParameter("userId"));

        // Delegate to bean (validation & business logic)
        userBean.deleteUser(userId);
        return "success=user_deleted";
    }

    /**
     * Utility to safely extract string parameters
     */
    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}

