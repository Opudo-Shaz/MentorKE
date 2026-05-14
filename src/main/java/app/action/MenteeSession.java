package app.action;

import app.bean.*;
import app.model.Mentor;
import app.model.MatchRequest;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;


@WebServlet(name = "MenteeSession", urlPatterns = {"/mentee-sessions"})
public class MenteeSession extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(MenteeSession.class);

    @Inject
    private SessionMatchingBean sessionMatchingBean;

    @Inject
    private MatchRequestBean matchRequestBean;

    @Inject
    private MentorBean mentorBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        // Check if user is logged in
        if (!isLoggedIn(request)) {
            redirect(response, "login");
            return;
        }

        String userId = getUserId(request);
        String role = getUserRole(request);

        // Verify user is a mentee
        if (!requireRole(request, response, "mentee")) {
            return;
        }

        try {
            if ("browse".equalsIgnoreCase(action)) {
                handleBrowseMentors(request, response, userId);
            } else if ("request".equalsIgnoreCase(action)) {
                handleRequestMentor(request, response, userId);
            } else if ("my-requests".equalsIgnoreCase(action)) {
                handleMyRequests(request, response, userId);
            } else if ("view-mentor".equalsIgnoreCase(action)) {
                handleViewMentor(request, response);
            } else {
                handleBrowseMentors(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error in MenteeSessionAction", e);
            setAttribute(request, "errorMessage", "An error occurred: " + e.getMessage());
            try {
                forward(request, response, "/mentee-dashboard.jsp");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        if (!isLoggedIn(request)) {
            redirect(response, "login");
            return;
        }

        String userId = getUserId(request);

        if (!requireRole(request, response, "mentee")) {
            return;
        }

        try {
            if ("request-mentor".equalsIgnoreCase(action)) {
                String mentorId = request.getParameter("mentorId");
                String specialization = request.getParameter("specialization");

                matchRequestBean.requestMentor(userId, mentorId, specialization);
                
                setAttribute(request, "successMessage", "Request sent to mentor successfully!");
                handleBrowseMentors(request, response, userId);
            } else if ("cancel-request".equalsIgnoreCase(action)) {
                String requestId = request.getParameter("requestId");
                
                MatchRequest req = matchRequestBean.getMatchRequest(requestId);
                if (req != null && req.getMenteeId().equals(userId)) {
                    matchRequestBean.rejectMentorRequest(requestId);
                    setAttribute(request, "successMessage", "Request cancelled successfully!");
                }
                handleMyRequests(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error processing mentee request", e);
            setAttribute(request, "errorMessage", "An error occurred: " + e.getMessage());
            try {
                forward(request, response, "/mentee-dashboard.jsp");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Browse available mentors
     */
    private void handleBrowseMentors(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        logger.info("Mentee {} browsing mentors", userId);

        String specialization = request.getParameter("specialization");
        List<Mentor> mentors;

        try {
            if (specialization != null && !specialization.isEmpty()) {
                mentors = sessionMatchingBean.findMentorsBySpecialization(specialization);
            } else {
                mentors = mentorBean.getAllMentors();
            }

            setAttribute(request, "mentors", mentors);
            setAttribute(request, "selectedSpecialization", specialization);
            forward(request, response, "/browse-mentors.jsp");

        } catch (Exception e) {
            logger.error("Error browsing mentors", e);
            throw new ServletException(e);
        }
    }

    /**
     * Handle mentor request
     */
    private void handleRequestMentor(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        String mentorId = request.getParameter("mentorId");
        logger.info("Mentee {} requesting mentor {}", userId, mentorId);

        try {
            Mentor mentor = mentorBean.getMentorById(mentorId);
            
            if (mentor != null) {
                setAttribute(request, "mentor", mentor);
                forward(request, response, "/request-mentor.jsp");
            } else {
                setAttribute(request, "errorMessage", "Mentor not found");
                handleBrowseMentors(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error viewing mentor request form", e);
            throw new ServletException(e);
        }
    }

    /**
     * View mentee's pending and approved requests
     */
    private void handleMyRequests(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        logger.info("Mentee {} viewing their requests", userId);

        try {
            List<MatchRequest> requests = matchRequestBean.getRequestsByMentee(userId);
            MatchRequest approvedMatch = matchRequestBean.getApprovedMatchForMentee(userId);

            setAttribute(request, "requests", requests);
            setAttribute(request, "approvedMatch", approvedMatch);
            forward(request, response, "/my-match-requests.jsp");

        } catch (Exception e) {
            logger.error("Error retrieving match requests", e);
            throw new ServletException(e);
        }
    }

    /**
     * View mentor profile details
     */
    private void handleViewMentor(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String mentorId = request.getParameter("mentorId");
        logger.info("Viewing mentor profile: {}", mentorId);

        try {
            Mentor mentor = mentorBean.getMentorById(mentorId);

            if (mentor != null) {
                setAttribute(request, "mentor", mentor);
                forward(request, response, "/mentor-profile.jsp");
            } else {
                setAttribute(request, "errorMessage", "Mentor not found");
                redirect(response, "mentee-dashboard");
            }
        } catch (Exception e) {
            logger.error("Error viewing mentor profile", e);
            throw new ServletException(e);
        }
    }
}
