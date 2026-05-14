package app.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.inject.Inject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import app.bean.RegistrationService;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

@WebServlet(name = "Register", urlPatterns = {"/register"})
public class RegisterPage extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(RegisterPage.class);

    @Inject
    private RegistrationService registrationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            forward(request, response, "/register.jsp");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            handlePost(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("Processing registration form submission");

        // ===== HTTP HANDLER ONLY: Read request parameters =====
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String role = request.getParameter("role");

        // ===== BASIC NULL/EMPTY VALIDATION (UI-level, not business logic) =====
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {
            logger.warn("Validation failed - missing required fields");
            setAttribute(request, "errorMessage", "All fields are required");
            forward(request, response, "/register-error.jsp");
            return;
        }

        // ===== COLLECT ALL REQUEST PARAMETERS FOR THE SERVICE BEAN =====
        Map<String, String> allFormData = new HashMap<>();
        for (java.util.Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            if (paramValue != null && !paramValue.isEmpty()) {
                allFormData.put(paramName, paramValue);
            }
        }

        try {
            // ===== CALL SERVICE BEAN TO HANDLE BUSINESS LOGIC =====
            registrationService.registerUser(username, password, email, role, 
                                           registrationService.extractRoleSpecificData(role, allFormData));

            // ===== HTTP HANDLER ONLY: Prepare response and forward to JSP =====
            logger.info("User registered successfully: username={}, role={}", username, role);
            setAttribute(request, "username", username);
            setAttribute(request, "role", role);
            forward(request, response, "/register-success.jsp");

        } catch (IllegalArgumentException e) {
            logger.error("Validation error during registration: {}", e.getMessage());
            setAttribute(request, "errorMessage", "Registration failed: " + e.getMessage());
            forward(request, response, "/register-error.jsp");
        } catch (SQLException e) {
            logger.error("Database error during registration: {}", e.getMessage());
            setAttribute(request, "errorMessage", "Error registering user: " + e.getMessage());
            forward(request, response, "/register-error.jsp");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}