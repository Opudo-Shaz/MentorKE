package app.action;

import app.bean.UserBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.Mentor;
import app.model.Mentee;
import app.model.User;
import app.security.websecurity.MentorKeSecurity;
import app.utility.logging.AppLogger;
import app.framework.Action;
import app.framework.ActionPostMethod;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
@Action(value = "user-management", label = "User Management")
@RolesAllowed({"admin"})
public class UserManagement extends BaseAction {

    private static final Logger logger =
            AppLogger.getLogger(UserManagement.class);

    @Inject
    private UserBean userBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @Inject
    private MentorKeSecurity security;

    /* =========================
       ADD USER (MENTOR / MENTEE)
       ========================= */

    @ActionPostMethod("add")
    @RolesAllowed({"admin"})
    public void add(HttpServletRequest request,
                    HttpServletResponse response) {

        security.requireRole("admin");

        try {
            String result = handleAdd(request);

            redirect(response,
                    request.getContextPath()
                            + "/app/user-management/admin?view=users&"
                            + result);

        } catch (Exception e) {
            redirectError(request, response, e);
        }
    }

    /* =========================
       UPDATE USER
       ========================= */

    @ActionPostMethod("update")
    @RolesAllowed({"admin"})
    public void update(HttpServletRequest request,
                       HttpServletResponse response) {

        security.requireRole("admin");

        try {
            String result = handleUpdate(request);

            redirect(response,
                    request.getContextPath()
                            + "/app/user-management/admin?view=users&"
                            + result);

        } catch (Exception e) {
            redirectError(request, response, e);
        }
    }

    /* =========================
       DELETE USER
       ========================= */

    @ActionPostMethod("delete")
    @RolesAllowed({"admin"})
    public void delete(HttpServletRequest request,
                       HttpServletResponse response) {

        security.requireRole("admin");

        try {
            String result = handleDelete(request);

            redirect(response,
                    request.getContextPath()
                            + "/app/user-management/admin?view=users&"
                            + result);

        } catch (Exception e) {
            redirectError(request, response, e);
        }
    }

    /* =========================
       CORE LOGIC
       ========================= */

    private String handleAdd(HttpServletRequest request) throws Exception {

        String role = safe(request.getParameter("role"));

        if ("mentor".equalsIgnoreCase(role)) {

            Mentor mentor = buildMentor(request);

            mentorBean.add(mentor, "ADMIN");

            return "success=mentor_added";
        }

        if ("mentee".equalsIgnoreCase(role)) {

            Mentee mentee = buildMentee(request);

            menteeBean.add(mentee, "ADMIN");

            return "success=mentee_added";
        }

        throw new IllegalArgumentException(
                "Invalid role selected. Choose mentor or mentee.");
    }

    private String handleUpdate(HttpServletRequest request) throws Exception {

        String id = safe(request.getParameter("id"));
        String role = safe(request.getParameter("role"));

        if ("mentor".equalsIgnoreCase(role)) {

            Mentor mentor = buildMentor(request);

            mentorBean.update(id, mentor);

            return "success=mentor_updated";
        }

        if ("mentee".equalsIgnoreCase(role)) {

            Mentee mentee = buildMentee(request);

            menteeBean.update(id, mentee);

            return "success=mentee_updated";
        }

        throw new IllegalArgumentException("Invalid role for update");
    }

    private String handleDelete(HttpServletRequest request) throws Exception {

        String id = safe(request.getParameter("id"));
        String role = safe(request.getParameter("role"));

        if ("mentor".equalsIgnoreCase(role)) {

            mentorBean.delete(id);

            return "success=mentor_deleted";
        }

        if ("mentee".equalsIgnoreCase(role)) {

            menteeBean.delete(id);

            return "success=mentee_deleted";
        }

        throw new IllegalArgumentException("Invalid role for delete");
    }

    /* =========================
       BUILDERS
       ========================= */

    private Mentor buildMentor(HttpServletRequest request) {

        Mentor m = new Mentor();

        m.setUsername(safe(request.getParameter("username")));
        m.setEmail(safe(request.getParameter("email")));
        m.setPassword(safe(request.getParameter("password")));
        m.setRole("mentor");

        m.setSpecialization(safe(request.getParameter("specialization")));
        m.setExpertise(safe(request.getParameter("expertise")));

        String yoe = safe(request.getParameter("yearsOfExperience"));
        if (!yoe.isEmpty()) {
            m.setYearsOfExperience(Integer.parseInt(yoe));
        }

        m.setBio(safe(request.getParameter("bio")));
        m.setQualifications(safe(request.getParameter("qualifications")));
        m.setPhoneNumber(safe(request.getParameter("phoneNumber")));
        m.setLocation(safe(request.getParameter("location")));
        m.setAvailability(safe(request.getParameter("availability")));

        String status = safe(request.getParameter("status"));
        m.setStatus(status.isEmpty() ? "Active" : status);

        return m;
    }

    private Mentee buildMentee(HttpServletRequest request) {

        Mentee m = new Mentee();

        m.setUsername(safe(request.getParameter("username")));
        m.setEmail(safe(request.getParameter("email")));
        m.setPassword(safe(request.getParameter("password")));
        m.setRole("mentee");

        m.setEducationLevel(safe(request.getParameter("educationLevel")));
        m.setFieldOfStudy(safe(request.getParameter("fieldOfStudy")));
        m.setLearningGoals(safe(request.getParameter("learningGoals")));
        m.setPhoneNumber(safe(request.getParameter("phoneNumber")));

        String mentorId = safe(request.getParameter("mentorId"));
        if (!mentorId.isEmpty()) {
            m.setMentorId(mentorId);
        }

        String status = safe(request.getParameter("status"));
        m.setStatus(status.isEmpty() ? "Active" : status);

        return m;
    }

    /* =========================
       ERROR HANDLING
       ========================= */

    private void redirectError(HttpServletRequest request,
                               HttpServletResponse response,
                               Exception e) {

        try {
            String msg = e.getMessage() == null
                    ? "error"
                    : e.getMessage();

            response.sendRedirect(
                    request.getContextPath()
                            + "/app/user-management/admin?view=users&error="
                            + URLEncoder.encode(msg, StandardCharsets.UTF_8)
            );

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String safe(String v) {
        return v == null ? "" : v.trim();
    }
}