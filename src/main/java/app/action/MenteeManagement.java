package app.action;

import app.bean.MenteeBean;
import app.model.Mentee;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.inject.Inject;
import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;

/**
 * MenteeManagement Servlet - Handles HTTP requests for Mentee CRUD operations
 *
 * Responsibilities (HTTP layer only):
 * - Extract request parameters
 * - Verify admin session
 * - Delegate business logic to MenteeBean
 * - Handle redirects with appropriate success/error messages
 *
 * All validation and business logic is in MenteeBean
 */
@WebServlet(name = "MenteeManagement",
        urlPatterns = {"/mentee-management"})
public class MenteeManagement extends HttpServlet {

    @Inject
    private MenteeBean menteeBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("\n[MenteeManagement] === HTTP POST called ===");

        // STEP 1: Verify admin session
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
            System.out.println("[MenteeManagement] Session invalid or not logged in");
            response.sendRedirect("login");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        if (!"admin".equalsIgnoreCase(role)) {
            System.out.println("[MenteeManagement] User is not admin, role=" + role);
            response.sendRedirect("login");
            return;
        }

        // STEP 2: Extract action parameter
        String action = request.getParameter("action");
        System.out.println("[MenteeManagement] Action requested: " + action);

        // STEP 3: Route to bean based on action
        try {
            String redirectParam = null;

            if ("add".equalsIgnoreCase(action)) {
                redirectParam = handleAddMentee(request);
            } else if ("update".equalsIgnoreCase(action)) {
                redirectParam = handleUpdateMentee(request);
            } else if ("delete".equalsIgnoreCase(action)) {
                redirectParam = handleDeleteMentee(request);
            } else {
                System.out.println("[MenteeManagement] Unknown action, redirecting to admin");
                response.sendRedirect("admin?view=mentees");
                return;
            }

            // STEP 4: Redirect with result
            response.sendRedirect("admin?view=mentees&" + redirectParam);

        } catch (IllegalArgumentException e) {
            System.err.println("[MenteeManagement] Validation error: " + e.getMessage());
            response.sendRedirect("admin?view=mentees&error=validation_failed");
        } catch (Exception e) {
            System.err.println("[MenteeManagement] Error: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("admin?view=mentees&error=operation_failed");
        }
    }

    /**
     * Handle add mentee request - only extracts parameters and delegates to bean
     */
    private String handleAddMentee(HttpServletRequest request) throws Exception {
        System.out.println("[MenteeManagement] handleAddMentee - extracting parameters");

        String userId = safe(request.getParameter("userId"));
        String educationLevel = safe(request.getParameter("educationLevel"));
        String fieldOfStudy = safe(request.getParameter("fieldOfStudy"));
        String learningGoals = safe(request.getParameter("learningGoals"));
        String phoneNumber = safe(request.getParameter("phoneNumber"));
        String mentorId = safe(request.getParameter("mentorId"));
        String status = safe(request.getParameter("status"));

        // Create mentee object
        Mentee newMentee = new Mentee();
        newMentee.setUserId(userId);
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
        System.out.println("[MenteeManagement] handleUpdateMentee - extracting parameters");

        String menteeId = safe(request.getParameter("id"));
        String educationLevel = safe(request.getParameter("educationLevel"));
        String fieldOfStudy = safe(request.getParameter("fieldOfStudy"));
        String learningGoals = safe(request.getParameter("learningGoals"));
        String phoneNumber = safe(request.getParameter("phoneNumber"));
        String mentorId = safe(request.getParameter("mentorId"));
        String status = safe(request.getParameter("status"));

        // Create mentee object
        Mentee mentee = new Mentee();
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
        System.out.println("[MenteeManagement] handleDeleteMentee - extracting parameters");

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
