package app.action;

import app.bean.MatchRequestBean;
import app.bean.MenteeBean;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.framework.ActionPostMethod;
import app.model.MatchRequest;
import app.model.Mentee;
import app.utility.logging.AppLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import java.util.List;

/**
 * MentorRequest - framework action for mentor pending requests.
 * The logic still forwards to the existing JSP so the UI theme stays unchanged.
 */
@ApplicationScoped
@Action(value = "mentor-requests", label = "Pending Requests", showLink = false)
public class MentorRequest extends AbstractAction {

    private static final Logger logger = AppLogger.getLogger(MentorRequest.class);

    @Inject
    private MatchRequestBean matchRequestBean;

    @Inject
    private MenteeBean menteeBean;

    @ActionGetMethod("pending")
    public app.framework.ActionResponse pending(HttpServletRequest request, HttpServletResponse response) {
        return renderPendingRequests(request, response);
    }

    @ActionPostMethod("approve")
    public app.framework.ActionResponse approve(HttpServletRequest request, HttpServletResponse response) {
        return processDecision(request, response, "APPROVED");
    }

    @ActionPostMethod("reject")
    public app.framework.ActionResponse reject(HttpServletRequest request, HttpServletResponse response) {
        return processDecision(request, response, "REJECTED");
    }

    private app.framework.ActionResponse processDecision(HttpServletRequest request, HttpServletResponse response, String decision) {
        String mentorId = getUserIdString(request);
        String requestId = request.getParameter("requestId");

        try {
            MatchRequest req = matchRequestBean.getMatchRequest(requestId);
            if (req != null && mentorId != null && mentorId.equals(req.getMentorId())) {
                if ("APPROVED".equals(decision)) {
                    matchRequestBean.approveMentorRequest(requestId);
                    request.setAttribute("successMessage", "Request approved! You now have a mentee.");
                } else {
                    matchRequestBean.rejectMentorRequest(requestId);
                    request.setAttribute("successMessage", "Request rejected.");
                }
            }
        } catch (Exception e) {
            logger.error("Error processing mentor request", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        return renderPendingRequests(request, response);
    }

    /**
     * View pending mentee requests for this mentor.
     * This forwards to the existing JSP so the existing theme stays intact.
     */
    private app.framework.ActionResponse renderPendingRequests(HttpServletRequest request, HttpServletResponse response) {
        String mentorId = getUserIdString(request);

        if (mentorId == null) {
            request.setAttribute("errorMessage", "Please log in again.");
            return forwardToJsp(request, response, "/mentor-dashboard.jsp");
        }

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

            request.setAttribute("pendingRequests", pendingRequests);
            return forwardToJsp(request, response, "/pending-mentee-requests.jsp");
        } catch (Exception e) {
            logger.error("Error retrieving pending requests", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return forwardToJsp(request, response, "/mentor-dashboard.jsp");
        }
    }

    private app.framework.ActionResponse forwardToJsp(HttpServletRequest request, HttpServletResponse response, String jspPath) {
        try {
            request.getRequestDispatcher(jspPath).forward(request, response);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
