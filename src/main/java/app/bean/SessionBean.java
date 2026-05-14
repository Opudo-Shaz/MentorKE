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
    private EmailBean emailBean;

    public SessionBean() {
        logger.debug("CDI Bean initialized with default constructor");
    }

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

        sessionDAO.addSession(session);
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
     * Format: https://meet.jit.si/roomname
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
        Mentor mentor = mentorDAO.getMentor(mentorId);
        Mentee mentee = menteeDAO.getMentee(menteeId);

        if (mentor != null && mentee != null) {
            String mentorEmail = "mentor@example.com"; // Get from User table in production
            String menteeEmail = "mentee@example.com"; // Get from User table in production

            String subject = "Session Scheduled - " + topic;
            String body = buildSessionNotificationBody(topic, scheduledDate, meetingLink);

            emailBean.sendEmail(mentorEmail, subject, body);
            emailBean.sendEmail(menteeEmail, subject, body);

            logger.info("Session notifications sent for mentor {} and mentee {}", mentorId, menteeId);
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
        return sessionDAO.getSession(sessionId);
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

        Session session = sessionDAO.getSession(sessionId);
        if (session != null) {
            session.setStatus(status);
            session.setUpdatedAt(LocalDateTime.now());
            sessionDAO.updateSession(sessionId, session);
        }
    }

    /**
     * Add notes to a session (typically after completion)
     */
    public void addSessionNotes(String sessionId, String notes) throws SQLException {
        logger.info("Adding notes to session: {}", sessionId);

        Session session = sessionDAO.getSession(sessionId);
        if (session != null) {
            session.setNotes(notes);
            session.setUpdatedAt(LocalDateTime.now());
            sessionDAO.updateSession(sessionId, session);
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
