package app.action;

import app.bean.RegistrationService;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.framework.ActionPostMethod;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

@ApplicationScoped
@Action(value = "register", label = "Register", showLink = false)
public class RegisterAction extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(RegisterAction.class);

    @Inject
    private RegistrationService registrationService;

    @ActionGetMethod("")
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        forward(request, response, "/register.jsp");
    }

    @ActionPostMethod("")
    public void post(HttpServletRequest request, HttpServletResponse response) throws Exception {
        handlePost(request, response);
    }

    private void handlePost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("Processing registration form submission");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String role = request.getParameter("role");

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {
            logger.warn("Validation failed - missing required fields");
            setAttribute(request, "errorMessage", "All fields are required");
            forward(request, response, "/register-error.jsp");
            return;
        }

        java.util.Map<String, String> allFormData = new java.util.HashMap<>();
        for (java.util.Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            if (paramValue != null && !paramValue.isEmpty()) {
                allFormData.put(paramName, paramValue);
            }
        }

        try {
            registrationService.registerUser(username, password, email, role,
                    registrationService.extractRoleSpecificData(role, allFormData));

            logger.info("User registered successfully: username={}, role={}", username, role);
            setAttribute(request, "username", username);
            setAttribute(request, "role", role);
            forward(request, response, "/register-success.jsp");

        } catch (IllegalArgumentException e) {
            logger.error("Validation error during registration: {}", e.getMessage());
            setAttribute(request, "errorMessage", "Registration failed: " + e.getMessage());
            forward(request, response, "/register-error.jsp");
        } catch (java.sql.SQLException e) {
            logger.error("Database error during registration: {}", e.getMessage());
            setAttribute(request, "errorMessage", "Error registering user: " + e.getMessage());
            forward(request, response, "/register-error.jsp");
        }
    }
}
