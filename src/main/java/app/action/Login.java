package app.action;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "Login", urlPatterns = {"/login"})
public class Login extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String action = request.getParameter("action");
        if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("login");
            return;
        }

        HttpSession existingSession = request.getSession(false);
        if (existingSession != null && Boolean.TRUE.equals(existingSession.getAttribute("isLoggedIn"))) {
            redirectToDashboard(response, String.valueOf(existingSession.getAttribute("role")));
            return;
        }

        // Forward to JSP
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handleLogin(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = safe(request.getParameter("username"));
        String password = safe(request.getParameter("password"));
        String role = safe(request.getParameter("role")).toLowerCase();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        String adminUsername = getServletContext().getInitParameter("app.admin.username");
        String adminPassword = getServletContext().getInitParameter("app.admin.password");

        boolean isValidUser;
        if ("admin".equals(role)) {
            isValidUser = username.equals(adminUsername) && password.equals(adminPassword);
        } else if ("mentor".equals(role) || "mentee".equals(role)) {
            // Non-admin roles are accepted if fields are not empty.
            isValidUser = true;
        } else {
            isValidUser = false;
        }

        if (!isValidUser) {
            request.setAttribute("errorMessage", "Invalid credentials for selected role.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession(true);
        }

        session.setAttribute("isLoggedIn", true);
        session.setAttribute("username", username);
        session.setAttribute("role", role);
        session.setAttribute("loginTime", System.currentTimeMillis());

        redirectToDashboard(response, role);
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
