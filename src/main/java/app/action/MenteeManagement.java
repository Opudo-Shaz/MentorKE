package app.action;

import app.bean.MenteeBean;
import app.model.Mentee;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import app.framework.Action;
import app.framework.ActionPostMethod;

@ApplicationScoped
@Action(value = "mentee-management", label = "Mentee Management", showLink = false)
public class MenteeManagement extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(MenteeManagement.class);

    @Inject
    private MenteeBean menteeBean;

    @ActionPostMethod("add")
    public void add(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isLoggedIn(request)) { redirect(response, "login"); return; }
        if (!requireRole(request, response, "admin")) return;

        try {
            String redirectParam = handleAddMentee(request);
            redirect(response, "admin?view=mentees&" + redirectParam);
        } catch (IllegalArgumentException e) {
            String errorMsg = e.getMessage().replace("Mentee validation failed: ", "");
            redirect(response, "admin?view=mentees&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
        } catch (Exception e) {
            redirect(response, "admin?view=mentees&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    @ActionPostMethod("update")
    public void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isLoggedIn(request)) { redirect(response, "login"); return; }
        if (!requireRole(request, response, "admin")) return;

        try {
            String redirectParam = handleUpdateMentee(request);
            redirect(response, "admin?view=mentees&" + redirectParam);
        } catch (IllegalArgumentException e) {
            String errorMsg = e.getMessage().replace("Mentee validation failed: ", "");
            redirect(response, "admin?view=mentees&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
        } catch (Exception e) {
            redirect(response, "admin?view=mentees&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    @ActionPostMethod("delete")
    public void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isLoggedIn(request)) { redirect(response, "login"); return; }
        if (!requireRole(request, response, "admin")) return;

        try {
            String redirectParam = handleDeleteMentee(request);
            redirect(response, "admin?view=mentees&" + redirectParam);
        } catch (IllegalArgumentException e) {
            String errorMsg = e.getMessage().replace("Mentee validation failed: ", "");
            redirect(response, "admin?view=mentees&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
        } catch (Exception e) {
            redirect(response, "admin?view=mentees&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    /**
     * Handle add mentee request - only extracts parameters and delegates to bean
     */
    private String handleAddMentee(HttpServletRequest request) throws Exception {
        logger.debug("handleAddMentee - extracting parameters");

        String username = safe(request.getParameter("username"));
        String email = safe(request.getParameter("email"));
        String password = safe(request.getParameter("password"));
        String educationLevel = safe(request.getParameter("educationLevel"));
        String fieldOfStudy = safe(request.getParameter("fieldOfStudy"));
        String learningGoals = safe(request.getParameter("learningGoals"));
        String phoneNumber = safe(request.getParameter("phoneNumber"));
        String mentorId = safe(request.getParameter("mentorId"));
        String status = safe(request.getParameter("status"));

        // Create mentee object
        Mentee newMentee = new Mentee();
        newMentee.setUsername(username);
        newMentee.setEmail(email);
        newMentee.setPassword(password);
        newMentee.setRole("mentee");
        newMentee.setEducationLevel(educationLevel);
        newMentee.setFieldOfStudy(fieldOfStudy);
        newMentee.setLearningGoals(learningGoals);
        newMentee.setPhoneNumber(phoneNumber);
        if (!mentorId.isEmpty()) {
            newMentee.setMentorId(mentorId);
        }
        newMentee.setStatus(status.isEmpty() ? "Active" : status);

        // Delegate to bean (validation & business logic)
        menteeBean.addMenteeAdmin(newMentee);
        return "success=mentee_added";
    }

    /**
     * Handle update mentee request - only extracts parameters and delegates to bean
     */
    private String handleUpdateMentee(HttpServletRequest request) throws Exception {
        logger.debug("handleUpdateMentee - extracting parameters");

        String menteeId = safe(request.getParameter("id"));
        String username = safe(request.getParameter("username"));
        String email = safe(request.getParameter("email"));
        String password = safe(request.getParameter("password"));
        String educationLevel = safe(request.getParameter("educationLevel"));
        String fieldOfStudy = safe(request.getParameter("fieldOfStudy"));
        String learningGoals = safe(request.getParameter("learningGoals"));
        String phoneNumber = safe(request.getParameter("phoneNumber"));
        String mentorId = safe(request.getParameter("mentorId"));
        String status = safe(request.getParameter("status"));

        // Create mentee object
        Mentee mentee = new Mentee();
        mentee.setUsername(username);
        mentee.setEmail(email);
        mentee.setPassword(password);
        mentee.setRole("mentee");
        mentee.setEducationLevel(educationLevel);
        mentee.setFieldOfStudy(fieldOfStudy);
        mentee.setLearningGoals(learningGoals);
        mentee.setPhoneNumber(phoneNumber);
        if (!mentorId.isEmpty()) {
            mentee.setMentorId(mentorId);
        }
        mentee.setStatus(status.isEmpty() ? "Active" : status);

        // Delegate to bean (validation & business logic)
        menteeBean.updateMentee(menteeId, mentee);
        return "success=mentee_updated";
    }

    /**
     * Handle delete mentee request - only extracts parameters and delegates to bean
     */
    private String handleDeleteMentee(HttpServletRequest request) throws Exception {
        logger.debug("handleDeleteMentee - extracting parameters");

        String menteeId = safe(request.getParameter("menteeId"));

        // Delegate to bean (validation & business logic)
        menteeBean.deleteMentee(menteeId);
        return "success=mentee_deleted";
    }

    /**
     * Utility to safely extract string parameters
     */
    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
