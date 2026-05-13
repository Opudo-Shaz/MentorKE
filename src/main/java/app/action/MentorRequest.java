package app.action;

import app.bean.MatchRequestBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.MatchRequest;
import app.model.Mentor;
import app.model.Mentee;
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

/**
 * MentorRequest - Handles mentor's view of pending match requests
 * URL: /mentor-requests
 * Actions: pending, approve, reject, my-mentees
 */
@WebServlet(name = "MentorRequest", urlPatterns = {"/mentor-requests"})
public class MentorRequest extends HttpServlet {

    private static final Logger logger = AppLogger.getLogger(MentorRequest.class);

    @Inject
    private MatchRequestBean matchRequestBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

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

        String userId = (String) httpSession.getAttribute("userId");
        String role = (String) httpSession.getAttribute("role");

        // Verify user is a mentor
        if (!"mentor".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only mentors can access this page");
            return;
        }

        try {
            if ("pending".equalsIgnoreCase(action)) {
                handlePendingRequests(request, response, userId);
            } else if ("my-mentees".equalsIgnoreCase(action)) {
                handleMyMentees(request, response, userId);
            } else {
                handlePendingRequests(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error in MentorRequestAction", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/mentor-dashboard.jsp").forward(request, response);
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

        String userId = (String) httpSession.getAttribute("userId");
        String role = (String) httpSession.getAttribute("role");

        if (!"mentor".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            if ("approve".equalsIgnoreCase(action)) {
                String requestId = request.getParameter("requestId");
                MatchRequest req = matchRequestBean.getMatchRequest(requestId);
                
                // Verify this mentor is the one being requested
                if (req != null && req.getMentorId().equals(userId)) {
                    matchRequestBean.approveMentorRequest(requestId);
                    request.setAttribute("successMessage", "Request approved! You now have a mentee.");
                }
                
                handlePendingRequests(request, response, userId);

            } else if ("reject".equalsIgnoreCase(action)) {
                String requestId = request.getParameter("requestId");
                MatchRequest req = matchRequestBean.getMatchRequest(requestId);
                
                // Verify this mentor is the one being requested
                if (req != null && req.getMentorId().equals(userId)) {
                    matchRequestBean.rejectMentorRequest(requestId);
                    request.setAttribute("successMessage", "Request rejected.");
                }
                
                handlePendingRequests(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error processing mentor request action", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/mentor-dashboard.jsp").forward(request, response);
        }
    }

    /**
     * View pending mentee requests for this mentor
     */
    private void handlePendingRequests(HttpServletRequest request, HttpServletResponse response, String mentorId) 
            throws ServletException, IOException {
        
        logger.info("Mentor {} viewing pending requests", mentorId);

        try {
            List<MatchRequest> pendingRequests = matchRequestBean.getPendingRequestsForMentor(mentorId);
            
            // Enrich with mentee details
            for (MatchRequest req : pendingRequests) {
                try {
                    Mentee mentee = menteeBean.getMenteeById(req.getMenteeId());
                    req.setRequestedSpecialization(mentee != null ? mentee.getFieldOfStudy() : "Unknown");
                } catch (Exception e) {
                    logger.warn("Could not load mentee details for request {}", req.getId());
                }
            }

            request.setAttribute("pendingRequests", pendingRequests);
            request.getRequestDispatcher("/pending-mentee-requests.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error retrieving pending requests", e);
            throw new ServletException(e);
        }
    }

    /**
     * View all mentees assigned to this mentor
     */
    private void handleMyMentees(HttpServletRequest request, HttpServletResponse response, String mentorId) 
            throws ServletException, IOException {
        
        logger.info("Mentor {} viewing their mentees", mentorId);

        try {
            List<Mentee> mentees = menteeBean.getMenteesByMentorId(mentorId);

            request.setAttribute("mentees", mentees);
            request.setAttribute("menteeCount", mentees.size());
            request.getRequestDispatcher("/mentor-mentees.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error retrieving mentees", e);
            throw new ServletException(e);
        }
    }
}
