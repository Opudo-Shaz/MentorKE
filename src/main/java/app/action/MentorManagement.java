package app.action;

import app.bean.MentorBean;
import app.model.Mentor;
import app.security.websecurity.MentorKeSecurity;
import app.utility.logging.AppLogger;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.framework.ActionPostMethod;
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
@RolesAllowed({"mentor", "mentee", "admin"})
public class MentorManagement extends BaseAction {

    private static final Logger logger =
            AppLogger.getLogger(MentorManagement.class);

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MentorKeSecurity security;

    /* =========================
       ADMIN VIEW
       ========================= */

    @ActionGetMethod("admin")
    @RolesAllowed({"admin"})
    public void admin(HttpServletRequest request,
                      HttpServletResponse response) throws Exception {

        security.requireRole("admin");

        try {
            List<Mentor> mentors = mentorBean.findAll();

            setAttribute(request, "mentors", mentors);
            setAttribute(request, "view", "mentors");

            forward(request, response, "/admin-dashboard.jsp");

        } catch (Exception e) {
            logger.error("Error loading mentors", e);

            setAttribute(request, "errorMessage",
                    "Error loading mentors: " + e.getMessage());

            forward(request, response, "/admin-dashboard.jsp");
        }
    }

    /* =========================
       CREATE
       ========================= */

    @ActionPostMethod("add")
    @RolesAllowed({"admin"})
    public void add(HttpServletRequest request,
                    HttpServletResponse response) throws Exception {

        security.requireRole("admin");

        try {
            Mentor mentor = buildMentor(request);

            // FIXED: matches MentorBean signature
            mentorBean.add(mentor, "ADMIN");

            redirectSuccess(response, request, "mentor_added");

        } catch (IllegalArgumentException e) {
            redirectError(response, request, e);

        } catch (Exception e) {
            redirectError(response, request, e);
        }
    }

    /* =========================
       UPDATE
       ========================= */

    @ActionPostMethod("update")
    @RolesAllowed({"admin", "mentor"})
    public void update(HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        try {
            String mentorId = safe(request.getParameter("id"));
            Mentor mentor = buildMentor(request);

            mentorBean.update(mentorId, mentor);

            redirectSuccess(response, request, "mentor_updated");

        } catch (IllegalArgumentException e) {
            redirectError(response, request, e);

        } catch (Exception e) {
            redirectError(response, request, e);
        }
    }

    /* =========================
       DELETE
       ========================= */

    @ActionPostMethod("delete")
    @RolesAllowed({"admin"})
    public void delete(HttpServletRequest request,
                       HttpServletResponse response) throws Exception {

        security.requireRole("admin");

        try {
            String mentorId = safe(request.getParameter("mentorId"));

            mentorBean.delete(mentorId);

            redirectSuccess(response, request, "mentor_deleted");

        } catch (IllegalArgumentException e) {
            redirectError(response, request, e);

        } catch (Exception e) {
            redirectError(response, request, e);
        }
    }

    /* =========================
       BUILDER
       ========================= */

    private Mentor buildMentor(HttpServletRequest request) {

        String username = safe(request.getParameter("username"));
        String email = safe(request.getParameter("email"));
        String password = safe(request.getParameter("password"));
        String specialization = safe(request.getParameter("specialization"));
        String expertise = safe(request.getParameter("expertise"));
        String yearsOfExperience = safe(request.getParameter("yearsOfExperience"));
        String bio = safe(request.getParameter("bio"));
        String qualifications = safe(request.getParameter("qualifications"));
        String phoneNumber = safe(request.getParameter("phoneNumber"));
        String location = safe(request.getParameter("location"));
        String availability = safe(request.getParameter("availability"));
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
        mentor.setLocation(location);
        mentor.setAvailability(availability);
        mentor.setStatus(status.isEmpty() ? "Active" : status);

        return mentor;
    }

    /* =========================
       REDIRECT HELPERS
       ========================= */

    private void redirectSuccess(HttpServletResponse response,
                                 HttpServletRequest request,
                                 String msg) throws Exception {

        response.sendRedirect(
                request.getContextPath()
                        + "/app/mentor-management/admin?success="
                        + msg
        );
    }

    private void redirectError(HttpServletResponse response,
                               HttpServletRequest request,
                               Exception e) throws Exception {

        String msg = e.getMessage() == null ? "error"
                : e.getMessage().replace("Mentor validation failed: ", "");

        response.sendRedirect(
                request.getContextPath()
                        + "/app/mentor-management/admin?error="
                        + URLEncoder.encode(msg, StandardCharsets.UTF_8)
        );
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}