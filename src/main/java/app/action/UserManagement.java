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
import app.utility.logging.AppLogger;
import org.slf4j.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import app.framework.Action;
import app.framework.ActionPostMethod;

@ApplicationScoped
@Action(value = "user-management", label = "User Management", showLink = false)
public class UserManagement extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(UserManagement.class);

    @Inject
    private UserBean userBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @ActionPostMethod("add")
    public void add(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("=== HTTP POST add user ===");
        if (!isLoggedIn(request)) { redirect(response, "login"); return; }
        if (!requireRole(request, response, "admin")) return;

        try {
            String redirectParam = handleAddUser(request);
            redirect(response, "admin?view=users&" + redirectParam);
        } catch (IllegalArgumentException e) {
            String errorMsg = e.getMessage().replace("User validation failed: ", "");
            redirect(response, "admin?view=users&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
        } catch (Exception e) {
            redirect(response, "admin?view=users&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    @ActionPostMethod("update")
    public void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("=== HTTP POST update user ===");
        if (!isLoggedIn(request)) { redirect(response, "login"); return; }
        if (!requireRole(request, response, "admin")) return;

        try {
            String redirectParam = handleUpdateUser(request);
            redirect(response, "admin?view=users&" + redirectParam);
        } catch (IllegalArgumentException e) {
            String errorMsg = e.getMessage().replace("User validation failed: ", "");
            redirect(response, "admin?view=users&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
        } catch (Exception e) {
            redirect(response, "admin?view=users&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    @ActionPostMethod("delete")
    public void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("=== HTTP POST delete user ===");
        if (!isLoggedIn(request)) { redirect(response, "login"); return; }
        if (!requireRole(request, response, "admin")) return;

        try {
            String redirectParam = handleDeleteUser(request);
            redirect(response, "admin?view=users&" + redirectParam);
        } catch (IllegalArgumentException e) {
            String errorMsg = e.getMessage().replace("User validation failed: ", "");
            redirect(response, "admin?view=users&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
        } catch (Exception e) {
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
