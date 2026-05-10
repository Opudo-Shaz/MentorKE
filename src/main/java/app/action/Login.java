package app.action;

import app.dao.UserDAO;
import app.model.User;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Login", urlPatterns = {"/login"})
public class Login extends HttpServlet {

    @Inject
    private UserDAO userDAO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String action = request.getParameter("action");
        if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();
            response.sendRedirect("login");
            return;
        }

        HttpSession existingSession = request.getSession(false);
        if (existingSession != null && Boolean.TRUE.equals(existingSession.getAttribute("isLoggedIn"))) {
            redirectToDashboard(response, String.valueOf(existingSession.getAttribute("role")));
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleLogin(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = safe(request.getParameter("username"));
        String password = safe(request.getParameter("password"));
        String role     = safe(request.getParameter("role")).toLowerCase();

        // Step 1: Basic field validation
        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            // Step 2: Admin — check web.xml credentials (no DB)
            if ("admin".equals(role)) {
                String adminUsername = getServletContext().getInitParameter("app.admin.username");
                String adminPassword = getServletContext().getInitParameter("app.admin.password");

                if (!username.equals(adminUsername) || !password.equals(adminPassword)) {
                    request.setAttribute("errorMessage", "Invalid admin credentials.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }

                // Admin session — no userId needed
                HttpSession session = request.getSession(true);
                session.setAttribute("isLoggedIn", true);
                session.setAttribute("username", username);
                session.setAttribute("role", "admin");
                session.setAttribute("loginTime", System.currentTimeMillis());
                redirectToDashboard(response, "admin");
                return;
            }

            // Step 3: Mentor / Mentee — look up in DB
            if ("mentor".equals(role) || "mentee".equals(role)) {
                User user = userDAO.getUserByUsername(username);

                // Check user exists
                if (user == null) {
                    request.setAttribute("errorMessage", "No account found with that username.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }

                // Check password matches
                if (!password.equals(user.getPassword())) {
                    request.setAttribute("errorMessage", "Incorrect password.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }

                // Check role matches what they selected
                if (!role.equalsIgnoreCase(user.getRole())) {
                    request.setAttribute("errorMessage", "Selected role does not match your account.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }

                // Check account is active
                if (!"Active".equalsIgnoreCase(user.getStatus())) {
                    request.setAttribute("errorMessage", "Your account is inactive. Please contact an administrator.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }

                // Step 4: All checks passed — create session WITH userId
                HttpSession session = request.getSession(true);
                session.setAttribute("isLoggedIn", true);
                session.setAttribute("username",   user.getUsername());
                session.setAttribute("role",       user.getRole().toLowerCase());
                session.setAttribute("userId",     user.getId());   // ← this is what dashboards need
                session.setAttribute("loginTime",  System.currentTimeMillis());

                redirectToDashboard(response, user.getRole());
                return;
            }

            // Unknown role
            request.setAttribute("errorMessage", "Invalid role selected.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Login failed: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void redirectToDashboard(HttpServletResponse response, String role) throws IOException {
        if ("admin".equalsIgnoreCase(role)) {
            response.sendRedirect("admin");
        } else if ("mentor".equalsIgnoreCase(role)) {
            response.sendRedirect("mentor-dashboard");
        } else if ("mentee".equalsIgnoreCase(role)) {
            response.sendRedirect("mentee-dashboard");
        } else {
            response.sendRedirect("login");
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}