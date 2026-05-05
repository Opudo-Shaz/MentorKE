package app.action;

import app.bean.MentorBean;
import app.model.Mentor;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.inject.Inject;
import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;

/**
 * MentorManagement Servlet - Handles HTTP requests for Mentor CRUD operations
 *
 * Responsibilities (HTTP layer only):
 * - Extract request parameters
 * - Verify admin session
 * - Delegate business logic to MentorBean
 * - Handle redirects with appropriate success/error messages
 *
 * All validation and business logic is in MentorBean
 */
@WebServlet(name = "MentorManagement",
        urlPatterns = {"/mentor-management"})
public class MentorManagement extends HttpServlet {

    @Inject
    private MentorBean mentorBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("\n[MentorManagement] === HTTP POST called ===");

        // STEP 1: Verify admin session
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("[MentorManagement] Session invalid or not logged in");
            response.sendRedirect("login");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        if (!"admin".equalsIgnoreCase(role)) {
            System.out.println("[MentorManagement] User is not admin, role=" + role);
            response.sendRedirect("login");
            return;
        }

        // STEP 2: Extract action parameter
        String action = request.getParameter("action");
        System.out.println("[MentorManagement] Action requested: " + action);

        // STEP 3: Route to bean based on action
        try {
            String redirectParam = null;

            if ("add".equalsIgnoreCase(action)) {
                redirectParam = handleAddMentor(request);
            } else if ("update".equalsIgnoreCase(action)) {
                redirectParam = handleUpdateMentor(request);
            } else if ("delete".equalsIgnoreCase(action)) {
                redirectParam = handleDeleteMentor(request);
            } else {
                System.out.println("[MentorManagement] Unknown action, redirecting to admin");
                response.sendRedirect("admin?view=mentors");
                return;
            }

            // STEP 4: Redirect with result
            response.sendRedirect("admin?view=mentors&" + redirectParam);

        } catch (IllegalArgumentException e) {
            System.err.println("[MentorManagement] Validation error: " + e.getMessage());
            response.sendRedirect("admin?view=mentors&error=validation_failed");
        } catch (Exception e) {
            System.err.println("[MentorManagement] Error: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("admin?view=mentors&error=operation_failed");
        }
    }

    /**
     * Handle add mentor request - only extracts parameters and delegates to bean
     */
    private String handleAddMentor(HttpServletRequest request) throws Exception {
        System.out.println("[MentorManagement] handleAddMentor - extracting parameters");

        String userId = safe(request.getParameter("userId"));
        String specialization = safe(request.getParameter("specialization"));
        String expertise = safe(request.getParameter("expertise"));
        String yearsOfExperience = safe(request.getParameter("yearsOfExperience"));
        String bio = safe(request.getParameter("bio"));
        String qualifications = safe(request.getParameter("qualifications"));
        String phoneNumber = safe(request.getParameter("phoneNumber"));
        String status = safe(request.getParameter("status"));

        // Create mentor object
        Mentor newMentor = new Mentor();
        newMentor.setUserId(userId);
        newMentor.setSpecialization(specialization);
        newMentor.setExpertise(expertise);
        if (!yearsOfExperience.isEmpty()) {
            newMentor.setYearsOfExperience(Integer.parseInt(yearsOfExperience));
        }
        newMentor.setBio(bio);
        newMentor.setQualifications(qualifications);
        newMentor.setPhoneNumber(phoneNumber);
        newMentor.setStatus(status.isEmpty() ? "Active" : status);

        // Delegate to bean (validation & business logic)
        mentorBean.addMentorAdmin(newMentor);
        return "success=mentor_added";
    }

    /**
     * Handle update mentor request - only extracts parameters and delegates to bean
     */
    private String handleUpdateMentor(HttpServletRequest request) throws Exception {
        System.out.println("[MentorManagement] handleUpdateMentor - extracting parameters");

        String mentorId = safe(request.getParameter("id"));
        String specialization = safe(request.getParameter("specialization"));
        String expertise = safe(request.getParameter("expertise"));
        String yearsOfExperience = safe(request.getParameter("yearsOfExperience"));
        String bio = safe(request.getParameter("bio"));
        String qualifications = safe(request.getParameter("qualifications"));
        String phoneNumber = safe(request.getParameter("phoneNumber"));
        String status = safe(request.getParameter("status"));

        // Create mentor object
        Mentor mentor = new Mentor();
        mentor.setSpecialization(specialization);
        mentor.setExpertise(expertise);
        if (!yearsOfExperience.isEmpty()) {
            mentor.setYearsOfExperience(Integer.parseInt(yearsOfExperience));
        }
        mentor.setBio(bio);
        mentor.setQualifications(qualifications);
        mentor.setPhoneNumber(phoneNumber);
        mentor.setStatus(status.isEmpty() ? "Active" : status);

        // Delegate to bean (validation & business logic)
        mentorBean.updateMentor(mentorId, mentor);
        return "success=mentor_updated";
    }

    /**
     * Handle delete mentor request - only extracts parameters and delegates to bean
     */
    private String handleDeleteMentor(HttpServletRequest request) throws Exception {
        System.out.println("[MentorManagement] handleDeleteMentor - extracting parameters");

        String mentorId = safe(request.getParameter("mentorId"));

        // Delegate to bean (validation & business logic)
        mentorBean.deleteMentor(mentorId);
        return "success=mentor_deleted";
    }

    /**
     * Utility to safely extract string parameters
     */
    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
