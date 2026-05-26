package app.action;

import app.bean.MentorBean;
import app.model.Mentor;
import app.security.MentorKeSecurity;
import app.utility.logging.AppLogger;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.framework.ActionPostMethod;
import app.framework.ActionResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApplicationScoped
@Action(value = "mentor-management", label = "Mentor Management")
public class MentorManagement extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(MentorManagement.class);

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MentorKeSecurity security;

    @ActionGetMethod("admin")
    @RolesAllowed({"admin"})
    public void admin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireRole("admin");
        
        try {
            List<Mentor> mentors = mentorBean.findAll();
            setAttribute(request, "mentors", mentors);
            setAttribute(request, "view", "mentors");
            forward(request, response, "/admin-dashboard.jsp");
        } catch (Exception e) {
            logger.error("Error loading mentors for admin view", e);
            setAttribute(request, "errorMessage", "Error loading mentors: " + e.getMessage());
            forward(request, response, "/admin-dashboard.jsp");
        }
    }

    @ActionPostMethod("add")
    @RolesAllowed({"admin"})
    public ActionResponse add(HttpServletRequest request, HttpServletResponse response) {
        security.requireRole("admin");
        return handleMentorMutation(request, response, "add");
    }

    @ActionPostMethod("update")
    @RolesAllowed({"admin", "mentor"})
    public ActionResponse update(HttpServletRequest request, HttpServletResponse response) {
        return handleMentorMutation(request, response, "update");
    }

    @ActionPostMethod("delete")
    @RolesAllowed({"admin"})
    public ActionResponse delete(HttpServletRequest request, HttpServletResponse response) {
        security.requireRole("admin");
        return handleMentorMutation(request, response, "delete");
    }

    private ActionResponse handleMentorMutation(HttpServletRequest request, HttpServletResponse response, String action) {
        try {
            String redirectParam;
            if ("add".equalsIgnoreCase(action)) {
                redirectParam = handleAddMentor(request);
            } else if ("update".equalsIgnoreCase(action)) {
                redirectParam = handleUpdateMentor(request);
            } else if ("delete".equalsIgnoreCase(action)) {
                redirectParam = handleDeleteMentor(request);
            } else {
                response.sendRedirect(request.getContextPath() + "/app/mentor-management/admin");
                return null;
            }

            response.sendRedirect(request.getContextPath() + "/app/mentor-management/admin?" + redirectParam);
            return null;
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            sendRedirectWithError(response, request, e.getMessage().replace("Mentor validation failed: ", ""));
            return null;
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            sendRedirectWithError(response, request, e.getMessage());
            return null;
        }
    }

    private String handleAddMentor(HttpServletRequest request) throws Exception {
        String username = safe(request.getParameter("username"));
        String email = safe(request.getParameter("email"));
        String password = safe(request.getParameter("password"));
        String specialization = safe(request.getParameter("specialization"));
        String expertise = safe(request.getParameter("expertise"));
        String yearsOfExperience = safe(request.getParameter("yearsOfExperience"));
        String bio = safe(request.getParameter("bio"));
        String qualifications = safe(request.getParameter("qualifications"));
        String phoneNumber = safe(request.getParameter("phoneNumber"));
        String status = safe(request.getParameter("status"));

        Mentor newMentor = new Mentor();
        newMentor.setUsername(username);
        newMentor.setEmail(email);
        newMentor.setPassword(password);
        newMentor.setRole("mentor");
        newMentor.setSpecialization(specialization);
        newMentor.setExpertise(expertise);
        if (!yearsOfExperience.isEmpty()) {
            newMentor.setYearsOfExperience(Integer.parseInt(yearsOfExperience));
        }
        newMentor.setBio(bio);
        newMentor.setQualifications(qualifications);
        newMentor.setPhoneNumber(phoneNumber);
        newMentor.setStatus(status.isEmpty() ? "Active" : status);

        mentorBean.addAdmin(newMentor);
        return "success=mentor_added";
    }

    private String handleUpdateMentor(HttpServletRequest request) throws Exception {
        String mentorId = safe(request.getParameter("id"));
        String username = safe(request.getParameter("username"));
        String email = safe(request.getParameter("email"));
        String password = safe(request.getParameter("password"));
        String specialization = safe(request.getParameter("specialization"));
        String expertise = safe(request.getParameter("expertise"));
        String yearsOfExperience = safe(request.getParameter("yearsOfExperience"));
        String bio = safe(request.getParameter("bio"));
        String qualifications = safe(request.getParameter("qualifications"));
        String phoneNumber = safe(request.getParameter("phoneNumber"));
        String status = safe(request.getParameter("status"));

        Mentor mentor = new Mentor();
        mentor.setUsername(username);
        mentor.setEmail(email);
        mentor.setPassword(password);
        mentor.setRole("mentor");
        mentor.setSpecialization(specialization);
        mentor.setExpertise(expertise);
        if (!yearsOfExperience.isEmpty()) {
            mentor.setYearsOfExperience(Integer.parseInt(yearsOfExperience));
        }
        mentor.setBio(bio);
        mentor.setQualifications(qualifications);
        mentor.setPhoneNumber(phoneNumber);
        mentor.setStatus(status.isEmpty() ? "Active" : status);

        mentorBean.update(mentorId, mentor);
        return "success=mentor_updated";
    }

    private String handleDeleteMentor(HttpServletRequest request) throws Exception {
        String mentorId = safe(request.getParameter("mentorId"));
        mentorBean.delete(mentorId);
        return "success=mentor_deleted";
    }

    private void sendRedirectWithError(HttpServletResponse response, HttpServletRequest request, String message) {
        try {
            response.sendRedirect(request.getContextPath() + "/app/mentor-management/admin?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
