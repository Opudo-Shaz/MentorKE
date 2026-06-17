package app.action;

import app.bean.MenteeBean;
import app.model.Mentee;
import app.security.websecurity.MentorKeSecurity;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.framework.ActionPostMethod;

@ApplicationScoped
@Action(value = "mentee-management", label = "Mentee Management")
@RolesAllowed({"mentor", "mentee", "admin"})
public class MenteeManagement extends BaseAction {

    private static final Logger logger =
            AppLogger.getLogger(MenteeManagement.class);

    @Inject
    private MenteeBean menteeBean;

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
            List<Mentee> mentees = menteeBean.findAll();

            setAttribute(request, "mentees", mentees);
            setAttribute(request, "view", "mentees");

            forward(request, response, "/admin-dashboard.jsp");

        } catch (Exception e) {
            logger.error("Error loading mentees", e);

            setAttribute(request, "errorMessage",
                    "Error loading mentees: " + e.getMessage());

            forward(request, response, "/admin-dashboard.jsp");
        }
    }

    /* =========================
       CREATE (ADMIN)
       ========================= */

    @ActionPostMethod("add")
    @RolesAllowed({"admin"})
    public void add(HttpServletRequest request,
                    HttpServletResponse response) throws IOException {

        security.requireRole("admin");

        try {
            String redirectParam = handleAddMentee(request);

            redirect(response,
                    request.getContextPath()
                            + "/app/mentee-management/admin?"
                            + redirectParam);

        } catch (IllegalArgumentException e) {

            redirectError(request, response, e);

        } catch (Exception e) {

            redirectError(request, response, e);
        }
    }

    /* =========================
       UPDATE
       ========================= */

    @ActionPostMethod("update")
    @RolesAllowed({"admin"})
    public void update(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {

        security.requireRole("admin");

        try {
            String redirectParam = handleUpdateMentee(request);

            redirect(response,
                    request.getContextPath()
                            + "/app/mentee-management/admin?"
                            + redirectParam);

        } catch (IllegalArgumentException e) {

            redirectError(request, response, e);

        } catch (Exception e) {

            redirectError(request, response, e);
        }
    }

    /* =========================
       DELETE
       ========================= */

    @ActionPostMethod("delete")
    @RolesAllowed({"admin"})
    public void delete(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {

        security.requireRole("admin");

        try {
            String redirectParam = handleDeleteMentee(request);

            redirect(response,
                    request.getContextPath()
                            + "/app/mentee-management/admin?"
                            + redirectParam);

        } catch (IllegalArgumentException e) {

            redirectError(request, response, e);

        } catch (Exception e) {

            redirectError(request, response, e);
        }
    }

    /* =========================
       HANDLERS
       ========================= */

    private String handleAddMentee(HttpServletRequest request) throws Exception {

        logger.debug("handleAddMentee");

        Mentee mentee = buildMenteeFromRequest(request);

        // FIXED: align with bean design
        menteeBean.add(mentee, "ADMIN");

        return "success=mentee_added";
    }

    private String handleUpdateMentee(HttpServletRequest request) throws Exception {

        logger.debug("handleUpdateMentee");

        String menteeId = safe(request.getParameter("id"));

        Mentee mentee = buildMenteeFromRequest(request);

        menteeBean.update(menteeId, mentee);

        return "success=mentee_updated";
    }

    private String handleDeleteMentee(HttpServletRequest request) throws Exception {

        logger.debug("handleDeleteMentee");

        String menteeId = safe(request.getParameter("menteeId"));

        menteeBean.delete(menteeId);

        return "success=mentee_deleted";
    }

    /* =========================
       SHARED BUILDER
       ========================= */

    private Mentee buildMenteeFromRequest(HttpServletRequest request) {

        String username = safe(request.getParameter("username"));
        String email = safe(request.getParameter("email"));
        String password = safe(request.getParameter("password"));
        String educationLevel = safe(request.getParameter("educationLevel"));
        String fieldOfStudy = safe(request.getParameter("fieldOfStudy"));
        String learningGoals = safe(request.getParameter("learningGoals"));
        String phoneNumber = safe(request.getParameter("phoneNumber"));
        String mentorId = safe(request.getParameter("mentorId"));
        String status = safe(request.getParameter("status"));

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

        return mentee;
    }

    /* =========================
       ERROR HANDLING
       ========================= */

    private void redirectError(HttpServletRequest request,
                               HttpServletResponse response,
                               Exception e) throws IOException {

        String msg = URLEncoder.encode(e.getMessage(), "UTF-8");

        redirect(response,
                request.getContextPath()
                        + "/app/mentee-management/admin?error=" + msg);
    }

    /* =========================
       UTILITY
       ========================= */

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}