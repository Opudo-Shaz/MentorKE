package app.bean;

import app.dao.MatchRequestDAO;
import app.dao.MentorDAO;
import app.dao.MenteeDAO;
import app.model.MatchRequest;
import app.model.Mentor;
import app.model.Mentee;
import app.utility.logging.AppLogger;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.List;

/**
 * MatchRequestBean - EJB Stateless bean for managing match requests
 * Handles mentee requests to be matched with mentors, approvals, and rejections
 */
@Stateless
@Named("matchRequestBean")
public class MatchRequestBean {

    private static final Logger logger = AppLogger.getLogger(MatchRequestBean.class);

    @Inject
    private MatchRequestDAO matchRequestDAO;

    @Inject
    private MentorDAO mentorDAO;

    @Inject
    private MenteeDAO menteeDAO;

    @Inject
    private SessionMatchingBean sessionMatchingBean;

    @Inject
    private EmailBean emailBean;

    public MatchRequestBean() {
        logger.debug("CDI Bean initialized with default constructor");
    }

    /**
     * Create a new match request from a mentee to a specific mentor
     */
    public void requestMentor(String menteeId, String mentorId, String specialization) throws SQLException {
        logger.info("Mentee {} requesting mentor {}", menteeId, mentorId);

        MatchRequest request = new MatchRequest(menteeId, mentorId, specialization);
        request.setStatus("PENDING");
        matchRequestDAO.addMatchRequest(request);

        logger.info("Match request created successfully");

        // Send notification to mentor
        try {
            sendMentorRequestNotification(mentorId, menteeId, specialization);
        } catch (Exception e) {
            logger.error("Error sending mentor request notification", e);
        }
    }

    /**
     * Create a match request without specifying a mentor (for auto-matching later)
     */
    public void requestAutoMatch(String menteeId, String specialization) throws SQLException {
        logger.info("Mentee {} requesting auto-match for specialization: {}", menteeId, specialization);

        MatchRequest request = new MatchRequest(menteeId, null, specialization);
        request.setStatus("PENDING");
        matchRequestDAO.addMatchRequest(request);

        logger.info("Auto-match request created successfully");
    }

    /**
     * Approve a match request (mentor accepts)
     */
    public void approveMentorRequest(String requestId) throws SQLException {
        logger.info("Approving match request: {}", requestId);

        MatchRequest request = matchRequestDAO.getMatchRequest(Long.parseLong(requestId));
        if (request != null) {
            request.setStatus("APPROVED");
            matchRequestDAO.updateMatchRequest(request.getId(), request);

            // Update mentee's mentor_id
            Mentee mentee = menteeDAO.getMentee(request.getMenteeId());
            if (mentee != null) {
                mentee.setMentorId(request.getMentorId());
                menteeDAO.updateMentee(request.getMenteeId(), mentee);
            }

            logger.info("Match request approved and mentee updated");
        }
    }

    /**
     * Reject a match request
     */
    public void rejectMentorRequest(String requestId) throws SQLException {
        logger.info("Rejecting match request: {}", requestId);

        MatchRequest request = matchRequestDAO.getMatchRequest(Long.parseLong(requestId));
        if (request != null) {
            request.setStatus("REJECTED");
            matchRequestDAO.updateMatchRequest(request.getId(), request);
            logger.info("Match request rejected");
        }
    }

    /**
     * Get pending requests for a mentor
     */
    public List<MatchRequest> getPendingRequestsForMentor(String mentorId) throws SQLException {
        logger.info("Getting pending requests for mentor: {}", mentorId);
        return matchRequestDAO.getPendingRequestsForMentor(mentorId);
    }

    /**
     * Get all requests by a mentee
     */
    public List<MatchRequest> getRequestsByMentee(String menteeId) throws SQLException {
        logger.info("Getting requests by mentee: {}", menteeId);
        return matchRequestDAO.getRequestsByMentee(menteeId);
    }

    /**
     * Get approved/matched request for a mentee
     */
    public MatchRequest getApprovedMatchForMentee(String menteeId) throws SQLException {
        logger.info("Getting approved match for mentee: {}", menteeId);
        return matchRequestDAO.getApprovedMatchForMentee(menteeId);
    }

    /**
     * Get a specific match request
     */
    public MatchRequest getMatchRequest(String requestId) throws SQLException {
        return matchRequestDAO.getMatchRequest(Long.parseLong(requestId));
    }

    /**
     * Auto-match unassigned requests with available mentors
     * This would typically run as a scheduled job
     */
    public void autoMatchPendingRequests() throws SQLException {
        logger.info("Running auto-match for pending requests");

        List<MatchRequest> pendingRequests = matchRequestDAO.getPendingUnassignedRequests();

        for (MatchRequest request : pendingRequests) {
            try {
                // Find optimal mentor based on specialization
                Mentee mentee = menteeDAO.getMentee(request.getMenteeId());
                if (mentee != null) {
                    Mentor optimalMentor = sessionMatchingBean.findOptimalMentor(mentee);
                    
                    if (optimalMentor != null) {
                        request.setMentorId(optimalMentor.getId());
                        request.setStatus("APPROVED");
                        matchRequestDAO.updateMatchRequest(request.getId(), request);

                        // Update mentee
                        mentee.setMentorId(optimalMentor.getId());
                        menteeDAO.updateMentee(request.getMenteeId(), mentee);

                        logger.info("Auto-matched mentee {} with mentor {}", 
                            request.getMenteeId(), optimalMentor.getId());
                    }
                }
            } catch (SQLException e) {
                logger.error("Error auto-matching request: {}", request.getId(), e);
            }
        }

        logger.info("Auto-match process completed");
    }

    /**
     * Send notification to mentor about a new request
     */
    private void sendMentorRequestNotification(String mentorId, String menteeId, 
                                               String specialization) throws SQLException {
        Mentor mentor = mentorDAO.getMentor(mentorId);
        
        if (mentor != null) {
            String mentorEmail = "mentor@example.com"; // Get from User table in production
            String subject = "New Mentee Request - " + specialization;
            String body = "<html><body>" +
                         "<h2>You have a new mentee request!</h2>" +
                         "<p>A mentee is interested in being mentored in: " + specialization + "</p>" +
                         "<p>Please review and approve or reject this request.</p>" +
                         "</body></html>";

            emailBean.sendEmail(mentorEmail, subject, body);
            logger.info("Mentor request notification sent to {}", mentorId);
        }
    }
}
