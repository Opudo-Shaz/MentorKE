package app.bean;

import app.dao.SessionDAO;
import app.dao.MentorDAO;
import app.dao.MenteeDAO;
import app.model.Session;
import app.model.Mentor;
import app.model.Mentee;
import app.utility.logging.AppLogger;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * SessionBean - EJB Stateless bean for session management
 * Handles session creation, scheduling, and link generation
 */
@Stateless
@Named("sessionBean")
public class SessionBean {

    private static final Logger logger = AppLogger.getLogger(SessionBean.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Inject
    private SessionDAO sessionDAO;

    @Inject
    private MentorDAO mentorDAO;

    @Inject
    private MenteeDAO menteeDAO;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private EmailReminderBean emailBean;

    public SessionBean() {}

    /**
     * Schedule a new session between mentor and mentee
     */
    public String scheduleSession(String mentorId, String menteeId, LocalDateTime scheduledDate,
                                  Integer durationMinutes, String topic) throws SQLException {
        logger.info("Scheduling session - Mentor: {}, Mentee: {}, Topic: {}", 
            mentorId, menteeId, topic);

        Session session = new Session(mentorId, menteeId, scheduledDate, durationMinutes, topic);
        
        // Generate unique meeting link
        String meetingLink = generateMeetingLink(mentorId, menteeId);
        session.setSessionLink(meetingLink);
        session.setStatus("PENDING");

        sessionDAO.save(session);
        logger.info("Session created successfully with link: {}", meetingLink);

        // Send notifications to both mentor and mentee
        try {
            sendSessionScheduledNotifications(mentorId, menteeId, scheduledDate, meetingLink, topic);
        } catch (Exception e) {
            logger.error("Error sending session notifications", e);
        }

        return String.valueOf(session.getId());
    }

    /**
     * Generate a unique meeting link using Jitsi Meet
     */
    private String generateMeetingLink(String mentorId, String menteeId) {
        String roomName = "mentorke-" + mentorId + "-" + menteeId + "-" + UUID.randomUUID().toString().substring(0, 8);
        return "https://meet.jit.si/" + roomName;
    }

    /**
     * Send notifications to both mentor and mentee
     */
    private void sendSessionScheduledNotifications(String mentorId, String menteeId, 
                                                   LocalDateTime scheduledDate, String meetingLink,
                                                   String topic) throws SQLException {
        Mentor mentor = mentorDAO.findById(Long.parseLong(mentorId));
        Mentee mentee = menteeDAO.findById(Long.parseLong(menteeId));

        if (mentor != null && mentee != null) {
            String mentorEmail = "mentor@example.com";
            String menteeEmail = "mentee@example.com";

            String subject = "Session Scheduled - " + topic;
            String body = buildSessionNotificationBody(topic, scheduledDate, meetingLink);

            emailBean.sendEmail(mentorEmail, subject, body);
            emailBean.sendEmail(menteeEmail, subject, body);

            logger.info("Session notifications sent for mentor {} and mentee {}", mentorId, menteeId);
        }
    }

    /**
     * Book a session directly (used by mentee action)
     */
    public void bookSession(Session session) throws SQLException {
        logger.info("Booking session for mentee: {}", session.getMenteeId());
        sessionDAO.save(session);
    }

    /**
     * Delete a session only if the requesting user owns it
     */
    public void deleteSessionIfOwned(Long sessionId, String menteeId) throws SQLException {
        logger.info("Attempting to delete session: {} for mentee: {}", sessionId, menteeId);
        Session s = sessionDAO.findById(sessionId);
        if (s != null && menteeId.equals(s.getMenteeId())) {
            sessionDAO.delete(sessionId);
            logger.info("Session {} deleted successfully", sessionId);
        } else {
            logger.warn("Delete rejected - session {} not owned by mentee {}", sessionId, menteeId);
        }
    }

    /**
     * Build HTML body for session notification email
     */
    private String buildSessionNotificationBody(String topic, LocalDateTime scheduledDate, String meetingLink) {
        String formattedDate = scheduledDate.format(DATE_FORMATTER);

        return "<html>" +
                "<body>" +
                "<h2>Session Scheduled</h2>" +
                "<p><strong>Topic:</strong> " + topic + "</p>" +
                "<p><strong>Date & Time:</strong> " + formattedDate + "</p>" +
                "<p><a href='" + meetingLink + "' target='_blank'>Join Session</a></p>" +
                "<p>Click the link above to join your mentoring session.</p>" +
                "</body>" +
                "</html>";
    }

    /**
     * Get a session by ID
     */
    public Session getSession(String sessionId) throws SQLException {
        return sessionDAO.findById(Long.parseLong(sessionId));
    }

    /**
     * Get all upcoming sessions for a user
     */
    public List<Session> getUpcomingSessions(String userId) throws SQLException {
        logger.info("Getting upcoming sessions for user: {}", userId);
        return sessionDAO.getUpcomingSessions(userId);
    }

    /**
     * Get all completed sessions for a user
     */
    public List<Session> getCompletedSessions(String userId) throws SQLException {
        logger.info("Getting completed sessions for user: {}", userId);
        return sessionDAO.getCompletedSessions(userId);
    }

    /**
     * Get all sessions for a mentor
     */
    public List<Session> getSessionsByMentor(String mentorId) throws SQLException {
        logger.info("Getting sessions for mentor: {}", mentorId);
        return sessionDAO.getSessionsByMentor(mentorId);
    }

    /**
     * Get all sessions for a mentee
     */
    public List<Session> getSessionsByMentee(String menteeId) throws SQLException {
        logger.info("Getting sessions for mentee: {}", menteeId);
        return sessionDAO.getSessionsByMentee(menteeId);
    }


    

    /**
     * Update session status
     */
    public void updateSessionStatus(String sessionId, String status) throws SQLException {
        logger.info("Updating session {} status to: {}", sessionId, status);

        Session session = sessionDAO.findById(Long.parseLong(sessionId));
        if (session != null) {
            boolean wasCompleted = "COMPLETED".equalsIgnoreCase(session.getStatus());
            session.setStatus(status);
            session.setUpdatedAt(LocalDateTime.now());

            if (!wasCompleted && "COMPLETED".equalsIgnoreCase(status)) {
                session.setRatingRequestedAt(LocalDateTime.now());
            }

            sessionDAO.update(session);

            if (!wasCompleted && "COMPLETED".equalsIgnoreCase(status)) {
                sendMentorRatingRequest(session);
            }
        }
    }

    public void submitMentorRating(String sessionId, String menteeId, String mentorId, int rating, String feedback)
            throws SQLException {
        Session session = sessionDAO.findById(Long.parseLong(sessionId));
        if (session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        if (!"COMPLETED".equalsIgnoreCase(session.getStatus())) {
            throw new IllegalArgumentException("Only completed sessions can be rated");
        }

        if (!session.getMenteeId().equals(menteeId)) {
            throw new IllegalArgumentException("You can only rate mentors from your own sessions");
        }

        if (!session.getMentorId().equals(mentorId)) {
            throw new IllegalArgumentException("Mentor does not match this session");
        }

        if (session.getMentorRating() != null) {
            throw new IllegalArgumentException("This session has already been rated");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        session.setMentorRating(rating);
        session.setRatingFeedback(feedback == null ? null : feedback.trim());
        session.setRatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        sessionDAO.update(session);

        mentorBean.applyMentorRating(mentorId, rating);
    }

    private void sendMentorRatingRequest(Session session) {
        try {
            Mentor mentor = mentorDAO.findById(Long.parseLong(session.getMentorId()));
            Mentee mentee = menteeDAO.findById(Long.parseLong(session.getMenteeId()));
            if (mentor == null || mentee == null || mentee.getEmail() == null || mentee.getEmail().isBlank()) {
                return;
            }

            String baseUrl = System.getProperty("mentorke.baseUrl", "http://localhost:8080/MentorKE");
            String ratingUrl = baseUrl + "/app/sessions/rate-form?sessionId=" + session.getId()
                    + "&mentorId=" + mentor.getId();

            String subject = "Rate your mentor session with " + mentor.getUsername();
            String body = buildMentorRatingRequestEmailBody(mentee.getUsername(), mentor.getUsername(), session, ratingUrl);
            emailBean.sendEmail(mentee.getEmail(), subject, body);
        } catch (Exception e) {
            logger.error("Failed to send rating request email for session {}", session.getId(), e);
        }
    }

    private String buildMentorRatingRequestEmailBody(
            String menteeName,
            String mentorName,
            Session session,
            String ratingUrl
    ) {
        String sessionDateTime = session.getScheduledDate() != null
                ? session.getScheduledDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a"))
                : "recently";

        return "<html>"
                + "<body style='font-family:Arial,sans-serif;background:#f5f5f5;padding:20px;'>"
                + "<div style='background:#fff;border-radius:8px;padding:30px;max-width:600px;margin:0 auto;'>"
                + "<h2 style='color:#1e293b;border-bottom:2px solid #0d47a1;padding-bottom:10px;'>Rate Your Mentor</h2>"
                + "<p>Hi " + menteeName + ",</p>"
                + "<p>Your session with <strong>" + mentorName + "</strong> on " + sessionDateTime + " has been marked complete.</p>"
                + "<p>Please rate your mentor to help other mentees find the best match.</p>"
                + "<p><a href='" + ratingUrl + "' style='display:inline-block;background:#0d47a1;color:#fff;padding:10px 20px;border-radius:5px;text-decoration:none;'>Rate Mentor</a></p>"
                + "<p style='font-size:13px;color:#475569;'>If the button does not work, copy this link: <a href='" + ratingUrl + "'>" + ratingUrl + "</a></p>"
                + "<p style='color:#94a3b8;font-size:12px;margin-top:30px;'>This is an automated message from MentorKE.</p>"
                + "</div></body></html>";
    }

    

    /**
     * Add notes to a session (typically after completion)
     */
    public void addSessionNotes(String sessionId, String notes) throws SQLException {
        logger.info("Adding notes to session: {}", sessionId);

        Session session = sessionDAO.findById(Long.parseLong(sessionId));
        if (session != null) {
            session.setNotes(notes);
            session.setUpdatedAt(LocalDateTime.now());
            sessionDAO.update(session);
        }
    }

    /**
     * Cancel a session
     */
    public void cancelSession(String sessionId) throws SQLException {
        logger.info("Cancelling session: {}", sessionId);
        updateSessionStatus(sessionId, "CANCELLED");
    }

    /**
     * Get total sessions for a user
     */
    public int getTotalSessionsForUser(String userId) throws SQLException {
        List<Session> allSessions = sessionDAO.getSessionsByMentor(userId);
        List<Session> menteeSessions = sessionDAO.getSessionsByMentee(userId);
        return allSessions.size() + menteeSessions.size();
    }
}
