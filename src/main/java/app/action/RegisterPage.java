package app.action;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.inject.Inject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import app.dao.UserDAO;
import app.dao.MentorDAO;
import app.dao.MenteeDAO;
import app.model.User;
import app.model.Mentor;
import app.model.Mentee;

@WebServlet(name = "Register", urlPatterns = {"/register"})
public class RegisterPage extends HttpServlet {

    @Inject
    private UserDAO userDAO;

    @Inject
    private MentorDAO mentorDAO;

    @Inject
    private MenteeDAO menteeDAO;

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

        // UserDAO is now injected via CDI
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
            String userId = newUser.getId();

            System.out.println("[RegisterPage] User registered successfully: " + username + " with ID: " + userId);

            // Save role-specific data
            try {
                if ("mentor".equalsIgnoreCase(role)) {
                    Mentor mentor = new Mentor();
                    mentor.setUserId(userId);
                    mentor.setSpecialization(request.getParameter("specialization"));
                    mentor.setExpertise(request.getParameter("expertise"));
                    String yearsExp = request.getParameter("yearsOfExperience");
                    if (yearsExp != null && !yearsExp.isEmpty()) {
                        mentor.setYearsOfExperience(Integer.parseInt(yearsExp));
                    }
                    mentor.setBio(request.getParameter("bio"));
                    mentor.setQualifications(request.getParameter("qualifications"));
                    mentor.setPhoneNumber(request.getParameter("phoneNumber"));
                    mentor.setStatus("Active");
                    
                    mentorDAO.addMentor(mentor);
                    System.out.println("[RegisterPage] Mentor profile created for user: " + userId);
                } else if ("mentee".equalsIgnoreCase(role)) {
                    Mentee mentee = new Mentee();
                    mentee.setUserId(userId);
                    mentee.setEducationLevel(request.getParameter("educationLevel"));
                    mentee.setFieldOfStudy(request.getParameter("fieldOfStudy"));
                    mentee.setLearningGoals(request.getParameter("learningGoals"));
                    mentee.setPhoneNumber(request.getParameter("phoneNumber"));
                    mentee.setStatus("Active");
                    
                    menteeDAO.addMentee(mentee);
                    System.out.println("[RegisterPage] Mentee profile created for user: " + userId);
                }
            } catch (SQLException e) {
                System.err.println("[RegisterPage] Error creating role-specific profile: " + e.getMessage());
                e.printStackTrace();
                // Note: User is already created, but role-specific data failed
                request.setAttribute("warningMessage", "User created but failed to create profile details: " + e.getMessage());
            }

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