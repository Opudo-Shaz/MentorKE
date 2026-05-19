package app.action;

import app.bean.*;
import app.model.Mentor;
import app.model.MatchRequest;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;


@WebServlet(name = "MenteeSession", urlPatterns = {"/mentee-sessions"})
public class MenteeSession extends HttpServlet {

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
        HttpSession httpSession = request.getSession(false);

        // Check if user is logged in
        if (httpSession == null || !Boolean.TRUE.equals(httpSession.getAttribute("isLoggedIn"))) {
            response.sendRedirect("login");
            return;
        }

        String userId = String.valueOf(httpSession.getAttribute("userId"));
        String role = (String) httpSession.getAttribute("role");

        // Verify user is a mentee
        if (!"mentee".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only mentees can access this page");
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
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/mentee-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || !Boolean.TRUE.equals(httpSession.getAttribute("isLoggedIn"))) {
            response.sendRedirect("login");
            return;
        }

        String userId = String.valueOf(httpSession.getAttribute("userId"));
        String role = (String) httpSession.getAttribute("role");

        if (!"mentee".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            if ("request-mentor".equalsIgnoreCase(action)) {
                String mentorId = request.getParameter("mentorId");
                String specialization = request.getParameter("specialization");

                matchRequestBean.requestMentor(userId, mentorId, specialization);
                
                request.setAttribute("successMessage", "Request sent to mentor successfully!");
                handleBrowseMentors(request, response, userId);
            } else if ("cancel-request".equalsIgnoreCase(action)) {
                String requestId = request.getParameter("requestId");
                
                MatchRequest req = matchRequestBean.getMatchRequest(requestId);
                if (req != null && req.getMenteeId().equals(userId)) {
                    matchRequestBean.rejectMentorRequest(requestId);
                    request.setAttribute("successMessage", "Request cancelled successfully!");
                }
                handleMyRequests(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error processing mentee request", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/mentee-dashboard.jsp").forward(request, response);
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

            request.setAttribute("mentors", mentors);
            request.setAttribute("selectedSpecialization", specialization);
            request.getRequestDispatcher("/browse-mentors.jsp").forward(request, response);

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
                request.setAttribute("mentor", mentor);
                request.getRequestDispatcher("/request-mentor.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Mentor not found");
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

            request.setAttribute("requests", requests);
            request.setAttribute("approvedMatch", approvedMatch);
            request.getRequestDispatcher("/my-match-requests.jsp").forward(request, response);

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
                request.setAttribute("mentor", mentor);
                request.getRequestDispatcher("/mentor-profile.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Mentor not found");
                response.sendRedirect("mentee-dashboard");
            }
        } catch (Exception e) {
            logger.error("Error viewing mentor profile", e);
            throw new ServletException(e);
        }
    }
}
