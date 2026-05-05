package app;

import app.dao.UserDAO;
import app.model.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import java.sql.SQLException;

@WebServlet(name = "UserManagement",
        urlPatterns = {"/user-management"})
public class UserManagement extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Verify admin session
        System.out.println("\n[UserManagement] === doPost called ===");

        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("[UserManagement] Session invalid or not logged in");
            response.sendRedirect("login");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        if (!"admin".equalsIgnoreCase(role)) {
            System.out.println("[UserManagement] User is not admin, role=" + role);
            response.sendRedirect("login");
            return;
        }

        UserDAO userDAO;
        userDAO = new UserDAO();

        System.out.println("[UserManagement] UserDAO initialized: " + "YES");

        String action = request.getParameter("action");
        System.out.println("[UserManagement] Action requested: " + action);

        try {
            if ("add".equalsIgnoreCase(action)) {
                handleAddUser(request, response, userDAO);
            } else if ("update".equalsIgnoreCase(action)) {
                handleUpdateUser(request, response, userDAO);
            } else if ("delete".equalsIgnoreCase(action)) {
                handleDeleteUser(request, response, userDAO);
            } else {
                System.out.println("[UserManagement] Unknown action, redirecting to admin");
                response.sendRedirect("admin");
            }
        } catch (SQLException e) {
            System.err.println("[UserManagement] Database error: " + e.getMessage());
            response.sendRedirect("admin?error=database_error");
        }
    }

    private void handleAddUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) throws IOException, SQLException {
        String username = safe(request.getParameter("username"));
        String password = safe(request.getParameter("password"));
        String role = safe(request.getParameter("role"));
        String email = safe(request.getParameter("email"));
        String status = safe(request.getParameter("status"));

        System.out.println("[UserManagement] handleAddUser called");
        System.out.println("[UserManagement] username=" + username + ", role=" + role + ", email=" + email);

        if (username.isEmpty() || password.isEmpty() || role.isEmpty() || email.isEmpty()) {
            System.out.println("[UserManagement] Validation failed - missing fields");
            response.sendRedirect("admin?error=missing_fields");
            return;
        }

        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            System.out.println("[UserManagement] Username already exists");
            response.sendRedirect("admin?error=user_exists");
            return;
        }

        User newUser = new User(null, username, password, role, email, status.isEmpty() ? "Active" : status);
        userDAO.addUser(newUser);
        System.out.println("[UserManagement] User added successfully, total users: " + userDAO.getTotalUsers());
        response.sendRedirect("admin?success=user_added");
    }

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) throws IOException, SQLException {
        String userId = safe(request.getParameter("userId"));
        String username = safe(request.getParameter("username"));
        String password = safe(request.getParameter("password"));
        String role = safe(request.getParameter("role"));
        String email = safe(request.getParameter("email"));
        String status = safe(request.getParameter("status"));

        System.out.println("[UserManagement] handleUpdateUser called for ID: " + userId);

        if (userId.isEmpty() || username.isEmpty() || role.isEmpty() || email.isEmpty()) {
            System.out.println("[UserManagement] Validation failed - missing fields");
            response.sendRedirect("admin?error=missing_fields");
            return;
        }

        User existingUser = userDAO.getUser(userId);
        if (existingUser == null) {
            System.out.println("[UserManagement] User not found with ID: " + userId);
            response.sendRedirect("admin?error=user_not_found");
            return;
        }

        User updatedUser = new User(userId, username, password.isEmpty() ? existingUser.getPassword() : password,
                                    role, email, status.isEmpty() ? "Active" : status);
        userDAO.updateUser(userId, updatedUser);
        System.out.println("[UserManagement] User updated successfully");
        response.sendRedirect("admin?success=user_updated");
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) throws IOException, SQLException {
        String userId = safe(request.getParameter("userId"));

        System.out.println("[UserManagement] handleDeleteUser called for ID: " + userId);

        if (userId.isEmpty()) {
            System.out.println("[UserManagement] Validation failed - missing user ID");
            response.sendRedirect("admin?error=user_id_required");
            return;
        }

        User user = userDAO.getUser(userId);
        if (user == null) {
            System.out.println("[UserManagement] User not found with ID: " + userId);
            response.sendRedirect("admin?error=user_not_found");
            return;
        }

        userDAO.deleteUser(userId);
        System.out.println("[UserManagement] User deleted successfully");
        response.sendRedirect("admin?success=user_deleted");
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}

