package app;

import app.dao.UserDAO;
import app.model.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "Register", urlPatterns = {"/register"})
public class RegisterPage extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Forward to JSP
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        handlePost(request, response);
    }

    private void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        System.out.println("[RegisterPage] Processing registration form submission");

        // === DYNAMICALLY EXTRACT FORM PARAMETERS USING REFLECTION ===
        Map<String, String> formData = extractFormDataUsingReflection(request);

        String username = formData.get("username");
        String password = formData.get("password");
        String email = formData.get("email");
        String role = request.getParameter("role");

        // Validate input
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {
            System.out.println("[RegisterPage] Validation failed - missing fields");
            request.setAttribute("errorMessage", "All fields are required");
            request.getRequestDispatcher("/register-error.jsp").forward(request, response);
            return;
        }

        // Initialize UserDAO
        UserDAO userDAO;
        try {
            userDAO = new UserDAO();
        } catch (SQLException e) {
            System.err.println("[RegisterPage] Error initializing UserDAO: " + e.getMessage());
            request.setAttribute("errorMessage", "Database connection error");
            request.getRequestDispatcher("/register-error.jsp").forward(request, response);
            return;
        }

        try {
            // Check if username already exists
            if (userDAO.getUserByUsername(username) != null) {
                System.out.println("[RegisterPage] Username already exists: " + username);
                request.setAttribute("errorMessage", "Username already exists. Please choose a different username.");
                request.getRequestDispatcher("/register-error.jsp").forward(request, response);
                return;
            }

            // Create new user
            User newUser = new User(null, username, password, role, email, "Active");

            System.out.println("[RegisterPage] Creating user: username=" + username + ", role=" + role + ", email=" + email);

            // Add user to database
            userDAO.addUser(newUser);

            System.out.println("[RegisterPage] User registered successfully: " + username);

            // Success - forward to success page
            request.setAttribute("username", username);
            request.setAttribute("role", role);
            request.getRequestDispatcher("/register-success.jsp").forward(request, response);

        } catch (SQLException e) {
            System.err.println("[RegisterPage] Database error during registration: " + e.getMessage());
            request.setAttribute("errorMessage", "Error registering user: " + e.getMessage());
            request.getRequestDispatcher("/register-error.jsp").forward(request, response);
        }
    }

    /**
     * Uses Java Reflection to dynamically extract form parameters from HttpServletRequest
     * Based on User class fields
     * @param request The HTTP request containing form data
     * @return Map of field names to form values
     */
    private Map<String, String> extractFormDataUsingReflection(HttpServletRequest request) {
        Map<String, String> formData = new HashMap<>();
        System.out.println("[RegisterPage] Extracting form data using Java Reflection");

        try {
            // Get all declared fields from User class
            java.lang.reflect.Field[] fields = User.class.getDeclaredFields();

            for (java.lang.reflect.Field field : fields) {
                String fieldName = field.getName();

                // Skip static and auto-generated fields
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (fieldName.equals("createdAt") || fieldName.equals("id")) {
                    continue;
                }

                // Extract the parameter value from request
                String paramValue = request.getParameter(fieldName);

                if (paramValue != null) {
                    formData.put(fieldName, paramValue);
                    System.out.println("[RegisterPage] Extracted parameter: " + fieldName + " = " + (fieldName.equals("password") ? "****" : paramValue));
                } else {
                    System.out.println("[RegisterPage] Parameter not found: " + fieldName);
                }
            }
        } catch (Exception e) {
            System.err.println("[RegisterPage] Error extracting form data: " + e.getMessage());
            e.printStackTrace();
        }

        return formData;
    }
}