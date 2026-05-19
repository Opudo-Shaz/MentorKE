package app.action;

import app.bean.MatchRequestBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.MatchRequest;
import app.model.Mentor;
import app.model.Mentee;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * MentorRequest - Handles mentor's view of pending match requests
 * URL: /mentor-requests
 * Actions: pending, approve, reject
 */
@WebServlet(name = "MentorRequest", urlPatterns = {"/mentor-requests"})
public class MentorRequest extends BaseAction {

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

        if (!isLoggedIn(request)) {
            redirect(response, "login");
            return;
        }

        if (!requireRole(request, response, "mentor")) {
            return;
        }

        String userId = getUserId(request);

        try {
            if ("pending".equalsIgnoreCase(action)) {
                handlePendingRequests(request, response, userId);
            } else {
                handlePendingRequests(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error in MentorRequestAction", e);
            setAttribute(request, "errorMessage", "An error occurred: " + e.getMessage());
            try {
                forward(request, response, "/mentor-dashboard.jsp");
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

        if (!requireRole(request, response, "mentor")) {
            return;
        }

        String userId = getUserId(request);

        try {
            if ("approve".equalsIgnoreCase(action)) {
                String requestId = request.getParameter("requestId");
                MatchRequest req = matchRequestBean.getMatchRequest(requestId);
                
                if (req != null && req.getMentorId().equals(userId)) {
                    matchRequestBean.approveMentorRequest(requestId);
                    setAttribute(request, "successMessage", "Request approved! You now have a mentee.");
                }
                
                handlePendingRequests(request, response, userId);

            } else if ("reject".equalsIgnoreCase(action)) {
                String requestId = request.getParameter("requestId");
                MatchRequest req = matchRequestBean.getMatchRequest(requestId);
                
                if (req != null && userId.equals(req.getMentorId())) {
                    matchRequestBean.rejectMentorRequest(requestId);
                    setAttribute(request, "successMessage", "Request rejected.");
                }
                
                handlePendingRequests(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error processing mentor request action", e);
            setAttribute(request, "errorMessage", "An error occurred: " + e.getMessage());
            try {
                forward(request, response, "/mentor-dashboard.jsp");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
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
            
            for (MatchRequest req : pendingRequests) {
                try {
                    Mentee mentee = menteeBean.getMenteeById(req.getMenteeId());
                    req.setRequestedSpecialization(mentee != null ? mentee.getFieldOfStudy() : "Unknown");
                } catch (Exception e) {
                    logger.warn("Could not load mentee details for request {}", req.getId());
                }
            }

            setAttribute(request, "pendingRequests", pendingRequests);
            forward(request, response, "/pending-mentee-requests.jsp");

        } catch (Exception e) {
            logger.error("Error retrieving pending requests", e);
            throw new ServletException(e);
        }
    }

}
