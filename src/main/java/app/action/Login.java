package app.action;

import app.bean.UserBean;
import app.model.User;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Login", urlPatterns = {"/login"})
public class Login extends BaseAction {

    @Inject
    private UserBean userBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String action = request.getParameter("action");
        if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();
            redirect(response, "login");
            return;
        }

        if (isLoggedIn(request)) {
            redirectToDashboard(response, getUserRole(request));
            return;
        }

        try {
            forward(request, response, "/login.jsp");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            handleLogin(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = safe(request.getParameter("username"));
        String password = safe(request.getParameter("password"));
        String role     = safe(request.getParameter("role")).toLowerCase();

        // Step 1: Basic field validation
        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            setAttribute(request, "errorMessage", "All fields are required.");
            forward(request, response, "/login.jsp");
            return;
        }

        try {
            // Step 2: Admin — check web.xml credentials (no DB)
            if ("admin".equals(role)) {
                String adminUsername = getServletContext().getInitParameter("app.admin.username");
                String adminPassword = getServletContext().getInitParameter("app.admin.password");

                if (!username.equals(adminUsername) || !password.equals(adminPassword)) {
                    setAttribute(request, "errorMessage", "Invalid admin credentials.");
                    forward(request, response, "/login.jsp");
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

            // Step 3: Mentor / Mentee — look up in DB using UserBean
            if ("mentor".equals(role) || "mentee".equals(role)) {
                User user = userBean.getUserByUsername(username);

                // Check user exists
                if (user == null) {
                    setAttribute(request, "errorMessage", "No account found with that username.");
                    forward(request, response, "/login.jsp");
                    return;
                }

                // Check password matches
                if (!password.equals(user.getPassword())) {
                    setAttribute(request, "errorMessage", "Incorrect password.");
                    forward(request, response, "/login.jsp");
                    return;
                }

                // Check role matches what they selected
                if (!role.equalsIgnoreCase(user.getRole())) {
                    setAttribute(request, "errorMessage", "Selected role does not match your account.");
                    forward(request, response, "/login.jsp");
                    return;
                }

                // Check account is active
                if (!"Active".equalsIgnoreCase(user.getStatus())) {
                    setAttribute(request, "errorMessage", "Your account is inactive. Please contact an administrator.");
                    forward(request, response, "/login.jsp");
                    return;
                }

                // Step 4: All checks passed — create session WITH userId
                HttpSession session = request.getSession(true);
                session.setAttribute("isLoggedIn", true);
                session.setAttribute("username",   user.getUsername());
                session.setAttribute("role",       user.getRole().toLowerCase());
                session.setAttribute("userId",     user.getId());
                session.setAttribute("loginTime",  System.currentTimeMillis());

                redirectToDashboard(response, user.getRole());
                return;
            }

            // Unknown role
            setAttribute(request, "errorMessage", "Invalid role selected.");
            forward(request, response, "/login.jsp");

        } catch (Exception e) {
            setAttribute(request, "errorMessage", "Login failed: " + e.getMessage());
            forward(request, response, "/login.jsp");
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