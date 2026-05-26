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

@Stateless
@Named("matchRequestBean")
public class MatchRequestBean {

    private static final Logger logger =
            AppLogger.getLogger(MatchRequestBean.class);

    @Inject
    private MatchRequestDAO matchRequestDAO;

    @Inject
    private MentorDAO mentorDAO;

    @Inject
    private MenteeDAO menteeDAO;

    @Inject
    private SessionMatchingBean sessionMatchingBean;

    @Inject
    private EmailReminderBean emailBean;

    public MatchRequestBean() {
        logger.debug("CDI Bean initialized with default constructor");
    }


    public List<MatchRequest> getAllMatchRequests() {
        return matchRequestDAO.findAll();
    }

    public MatchRequest createMatchRequest(
            String menteeId,
            String mentorId,
            String specialization
    ) throws SQLException {

        MatchRequest request =
                new MatchRequest(menteeId, mentorId, specialization);

        request.setStatus("PENDING");

        matchRequestDAO.save(request);

        return request;
    }


    public void requestMentor(String menteeId, String mentorId, String specialization) {
        logger.info("Mentee {} requesting mentor {}", menteeId, mentorId);

        MatchRequest request = new MatchRequest(menteeId, mentorId, specialization);
        request.setStatus("PENDING");
        matchRequestDAO.save(request);

        logger.info("Match request created successfully");

        try {
            sendMentorRequestNotification(mentorId, menteeId, specialization);
        } catch (Exception e) {
            logger.error("Error sending mentor request notification", e);
        }
    }

    public void requestAutoMatch(String menteeId, String specialization) {
        logger.info("Mentee {} requesting auto-match for specialization: {}", menteeId, specialization);

        MatchRequest request = new MatchRequest(menteeId, null, specialization);
        request.setStatus("PENDING");
        matchRequestDAO.save(request);

        logger.info("Auto-match request created successfully");
    }

    public void approveMentorRequest(String requestId) {
        logger.info("Approving match request: {}", requestId);

        MatchRequest request = matchRequestDAO.findById(Long.parseLong(requestId));
        if (request != null) {
            request.setStatus("APPROVED");
            matchRequestDAO.update(request);

            Mentee mentee = menteeDAO.findById(Long.parseLong(request.getMenteeId()));
            if (mentee != null) {
                mentee.setMentorId(request.getMentorId());
                menteeDAO.update(mentee);
            }
        }
    }

    public void rejectMentorRequest(String requestId) {
        logger.info("Rejecting match request: {}", requestId);

        MatchRequest request = matchRequestDAO.findById(Long.parseLong(requestId));
        if (request != null) {
            request.setStatus("REJECTED");
            matchRequestDAO.update(request);
        }
    }

    public void deleteMatchRequest(String requestId) {
        logger.info("Deleting match request: {}", requestId);
        matchRequestDAO.delete(Long.parseLong(requestId));
    }

    public List<MatchRequest> getPendingRequestsForMentor(String mentorId) {
        return matchRequestDAO.getPendingRequestsForMentor(mentorId);
    }

    public List<MatchRequest> getRequestsByMentor(String mentorId) {
        return matchRequestDAO.getRequestsByMentor(mentorId);
    }

    public List<MatchRequest> getRequestsByMentee(String menteeId) {
        return matchRequestDAO.getRequestsByMentee(menteeId);
    }

    public MatchRequest getApprovedMatchForMentee(String menteeId) {
        return matchRequestDAO.getApprovedMatchForMentee(menteeId);
    }

    public MatchRequest getMatchRequest(String requestId) {
        return matchRequestDAO.findById(Long.parseLong(requestId));
    }

    public void autoMatchPendingRequests() {
        logger.info("Running auto-match for pending requests");

        List<MatchRequest> pendingRequests = matchRequestDAO.getPendingUnassignedRequests();

        for (MatchRequest request : pendingRequests) {
            try {
                Mentee mentee = menteeDAO.findById(Long.parseLong(request.getMenteeId()));
                if (mentee != null) {
                    Mentor optimalMentor = sessionMatchingBean.findOptimalMentor(mentee);

                    if (optimalMentor != null) {
                        request.setMentorId(String.valueOf(optimalMentor.getId()));
                        request.setStatus("APPROVED");
                        matchRequestDAO.update(request);

                        mentee.setMentorId(String.valueOf(optimalMentor.getId()));
                        menteeDAO.update(mentee);
                    }
                }
            } catch (Exception e) {
                logger.error("Error auto-matching request: {}", request.getId(), e);
            }
        }
    }


    private void sendMentorRequestNotification(String mentorId, String menteeId,
                                               String specialization) {

        Mentor mentor = mentorDAO.findById(Long.parseLong(mentorId));

        if (mentor != null) {
            String mentorEmail = "mentor@example.com";
            String subject = "New Mentee Request - " + specialization;

            String body = "<html><body>" +
                    "<h2>You have a new mentee request!</h2>" +
                    "<p>A mentee is interested in being mentored in: " + specialization + "</p>" +
                    "<p>Please review and approve or reject this request.</p>" +
                    "</body></html>";

            emailBean.sendEmail(mentorEmail, subject, body);
        }
    }
}