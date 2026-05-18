package app.action;

import app.bean.UserBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.Mentor;
import app.model.Mentee;
import app.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.inject.Inject;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;


@WebServlet(name = "UserManagement",
        urlPatterns = {"/user-management"})
public class UserManagement extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(UserManagement.class);

    @Inject
    private UserBean userBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("=== HTTP POST called ===");

        // STEP 1: Verify admin session
        if (!isLoggedIn(request)) {
            logger.warn("Session invalid or not logged in");
            redirect(response, "login");
            return;
        }

        String role = getUserRole(request);
        if (!requireRole(request, response, "admin")) {
            logger.warn("User is not admin, role={}", role);
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
                redirect(response, "admin?view=users");
                return;
            }

            // STEP 4: Redirect with result
            redirect(response, "admin?view=users&" + redirectParam);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            String errorMsg = e.getMessage().replace("User validation failed: ", "");
            redirect(response, "admin?view=users&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            redirect(response, "admin?view=users&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
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

        if ("mentor".equalsIgnoreCase(role)) {
            Mentor mentor = new Mentor();
            mentor.setUsername(username);
            mentor.setPassword(password);
            mentor.setRole(role);
            mentor.setEmail(email);
            mentor.setStatus(status);
            mentor.setSpecialization(safe(request.getParameter("specialization")));
            mentor.setExpertise(safe(request.getParameter("expertise")));
            String yearsOfExperience = safe(request.getParameter("yearsOfExperience"));
            if (!yearsOfExperience.isEmpty()) {
                mentor.setYearsOfExperience(Integer.parseInt(yearsOfExperience));
            }
            mentor.setBio(safe(request.getParameter("bio")));
            mentor.setQualifications(safe(request.getParameter("qualifications")));
            mentor.setPhoneNumber(safe(request.getParameter("phoneNumber")));

            mentorBean.addMentorAdmin(mentor);
            return "success=mentor_added";
        }

        if ("mentee".equalsIgnoreCase(role)) {
            Mentee mentee = new Mentee();
            mentee.setUsername(username);
            mentee.setPassword(password);
            mentee.setRole(role);
            mentee.setEmail(email);
            mentee.setStatus(status);
            mentee.setEducationLevel(safe(request.getParameter("educationLevel")));
            mentee.setFieldOfStudy(safe(request.getParameter("fieldOfStudy")));
            mentee.setLearningGoals(safe(request.getParameter("learningGoals")));
            mentee.setPhoneNumber(safe(request.getParameter("phoneNumber")));
            String mentorId = safe(request.getParameter("mentorId"));
            if (!mentorId.isEmpty()) {
                mentee.setMentorId(mentorId);
            }

            menteeBean.addMenteeAdmin(mentee);
            return "success=mentee_added";
        }

        throw new IllegalArgumentException("Please choose Mentor or Mentee for account creation.");
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
        User user = new User(Long.parseLong(userId), username, password, role, email, status);

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

