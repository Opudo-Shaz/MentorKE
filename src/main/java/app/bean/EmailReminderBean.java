package app.bean;

import app.dao.SessionDAO;
import app.dao.MentorDAO;
import app.dao.MenteeDAO;
import app.dao.UserDAO;
import app.model.Session;
import app.model.Mentor;
import app.model.Mentee;
import app.model.User;
import app.utility.logging.AppLogger;
import jakarta.ejb.Stateless;
import jakarta.ejb.Schedule;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * EmailReminderBean - EJB Stateless bean for sending reminder emails
 * Sends reminder emails to mentors and mentees about upcoming sessions
 * Runs on a schedule every 30 minutes
 */
@Stateless
@Named("emailReminderBean")
public class EmailReminderBean {

    private static final Logger logger = AppLogger.getLogger(EmailReminderBean.class);

    @Inject
    private SessionDAO sessionDAO;

    @Inject
    private MentorDAO mentorDAO;

    @Inject
    private MenteeDAO menteeDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private EmailBean emailBean;

    public EmailReminderBean() {
        logger.debug("CDI Bean initialized with default constructor");
    }

    /**
     * Scheduled method that runs every 30 minutes
     * Sends reminder emails for sessions happening within the next 24 hours
     * Timezone is application server's default
     */
    @Schedule(hour = "*", minute = "*/30", persistent = false, info = "Session Reminder Scheduler")
    public void sendSessionReminders() {
        logger.info("=== Starting scheduled session reminder task ===");

        try {
            // Get all sessions
            List<Session> allSessions = sessionDAO.getAllSessions();

            long currentTime = System.currentTimeMillis();
            long twentyFourHoursLater = currentTime + (24 * 60 * 60 * 1000); // 24 hours in milliseconds
            long oneHourLater = currentTime + (60 * 60 * 1000); // 1 hour in milliseconds

            int remindersSent = 0;

            for (Session session : allSessions) {
                // Check if session is:
                // 1. In PENDING or CONFIRMED status
                // 2. Not yet started
                // 3. Within the next 24 hours
                // 4. At least 1 hour away (to avoid spam)

                if (session.getScheduledDate() > currentTime && 
                    session.getScheduledDate() <= twentyFourHoursLater &&
                    session.getScheduledDate() >= oneHourLater &&
                    ("PENDING".equals(session.getStatus()) || "CONFIRMED".equals(session.getStatus()))) {

                    try {
                        sendSessionReminderEmails(session);
                        remindersSent++;
                    } catch (Exception e) {
                        logger.error("Error sending reminder for session {}", session.getId(), e);
                    }
                }
            }

            logger.info("=== Session reminder task completed. Reminders sent: {} ===", remindersSent);

        } catch (SQLException e) {
            logger.error("Error retrieving sessions for reminder", e);
        } catch (Exception e) {
            logger.error("Unexpected error in session reminder scheduler", e);
        }
    }

    /**
     * Send reminder emails to both mentor and mentee for an upcoming session
     */
    public void sendSessionReminderEmails(Session session) throws SQLException {
        logger.info("Sending reminder emails for session: {}", session.getId());

        try {
            // Get mentor and mentee
            Mentor mentor = mentorDAO.getMentor(session.getMentorId());
            Mentee mentee = menteeDAO.getMentee(session.getMenteeId());

            if (mentor == null || mentee == null) {
                logger.warn("Mentor or mentee not found for session: {}", session.getId());
                return;
            }

            // Get user emails
            User mentorUser = userDAO.getUser(mentor.getUserId());
            User menteeUser = userDAO.getUser(mentee.getUserId());

            if (mentorUser == null || menteeUser == null) {
                logger.warn("User records not found for session: {}", session.getId());
                return;
            }

            String mentorEmail = mentorUser.getEmail();
            String menteeEmail = menteeUser.getEmail();

            // Calculate time remaining
            long timeRemaining = session.getScheduledDate() - System.currentTimeMillis();
            String timeRemainingStr = formatTimeRemaining(timeRemaining);

            // Build email bodies
            String mentorSubject = "Reminder: Upcoming Session - " + session.getTopic();
            String mentorBody = buildMentorReminderEmailBody(session, mentorUser.getUsername(), 
                                                             menteeUser.getUsername(), timeRemainingStr);

            String menteeSubject = "Reminder: Upcoming Session - " + session.getTopic();
            String menteeBody = buildMenteeReminderEmailBody(session, menteeUser.getUsername(), 
                                                             mentorUser.getUsername(), timeRemainingStr);

            // Send emails
            emailBean.sendEmail(mentorEmail, mentorSubject, mentorBody);
            emailBean.sendEmail(menteeEmail, menteeSubject, menteeBody);

            logger.info("Reminder emails sent for session {} to mentor {} and mentee {}", 
                session.getId(), mentorEmail, menteeEmail);

        } catch (SQLException e) {
            logger.error("Database error sending session reminders", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error sending session reminder emails", e);
        }
    }

    /**
     * Send a reminder email to mentor about an upcoming session
     */
    public void sendMentorSessionReminder(String sessionId) throws SQLException {
        logger.info("Sending individual mentor reminder for session: {}", sessionId);

        Session session = sessionDAO.getSession(sessionId);
        if (session != null) {
            sendSessionReminderEmails(session);
        }
    }

    /**
     * Send a reminder email to mentee about an upcoming session
     */
    public void sendMenteeSessionReminder(String sessionId) throws SQLException {
        logger.info("Sending individual mentee reminder for session: {}", sessionId);

        Session session = sessionDAO.getSession(sessionId);
        if (session != null) {
            sendSessionReminderEmails(session);
        }
    }

    /**
     * Build HTML email body for mentor reminder
     */
    private String buildMentorReminderEmailBody(Session session, String mentorName, 
                                                String menteeName, String timeRemaining) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
        String sessionDateTime = sdf.format(new Date(session.getScheduledDate()));

        return "<html>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;'>" +
               "<div style='background-color: white; border-radius: 8px; padding: 30px; max-width: 600px; margin: 0 auto;'>" +
               "<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;'>Session Reminder</h2>" +
               "<p>Hi " + mentorName + ",</p>" +
               "<p>This is a reminder about your upcoming mentoring session with <strong>" + menteeName + "</strong>.</p>" +
               "<div style='background-color: #ecf0f1; border-left: 4px solid #3498db; padding: 15px; margin: 20px 0;'>" +
               "<p><strong>Session Details:</strong></p>" +
               "<p><strong>Topic:</strong> " + session.getTopic() + "</p>" +
               "<p><strong>Date & Time:</strong> " + sessionDateTime + "</p>" +
               "<p><strong>Duration:</strong> " + session.getDurationMinutes() + " minutes</p>" +
               "<p><strong>Time Remaining:</strong> " + timeRemaining + "</p>" +
               "</div>" +
               "<p><a href='" + session.getSessionLink() + "' style='display: inline-block; background-color: #3498db; color: white; padding: 10px 20px; border-radius: 5px; text-decoration: none; margin-top: 10px;'>Join Session</a></p>" +
               "<p>Meeting Link: <a href='" + session.getSessionLink() + "'>" + session.getSessionLink() + "</a></p>" +
               "<p style='color: #7f8c8d; font-size: 12px; margin-top: 30px;'>This is an automated reminder. Please do not reply to this email.</p>" +
               "</div>" +
               "</body>" +
               "</html>";
    }

    /**
     * Build HTML email body for mentee reminder
     */
    private String buildMenteeReminderEmailBody(Session session, String menteeName, 
                                                String mentorName, String timeRemaining) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
        String sessionDateTime = sdf.format(new Date(session.getScheduledDate()));

        return "<html>" +
               "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;'>" +
               "<div style='background-color: white; border-radius: 8px; padding: 30px; max-width: 600px; margin: 0 auto;'>" +
               "<h2 style='color: #2c3e50; border-bottom: 2px solid #27ae60; padding-bottom: 10px;'>Session Reminder</h2>" +
               "<p>Hi " + menteeName + ",</p>" +
               "<p>This is a reminder about your upcoming mentoring session with <strong>" + mentorName + "</strong>.</p>" +
               "<div style='background-color: #ecf0f1; border-left: 4px solid #27ae60; padding: 15px; margin: 20px 0;'>" +
               "<p><strong>Session Details:</strong></p>" +
               "<p><strong>Topic:</strong> " + session.getTopic() + "</p>" +
               "<p><strong>Date & Time:</strong> " + sessionDateTime + "</p>" +
               "<p><strong>Duration:</strong> " + session.getDurationMinutes() + " minutes</p>" +
               "<p><strong>Time Remaining:</strong> " + timeRemaining + "</p>" +
               "</div>" +
               "<p><a href='" + session.getSessionLink() + "' style='display: inline-block; background-color: #27ae60; color: white; padding: 10px 20px; border-radius: 5px; text-decoration: none; margin-top: 10px;'>Join Session</a></p>" +
               "<p>Meeting Link: <a href='" + session.getSessionLink() + "'>" + session.getSessionLink() + "</a></p>" +
               "<p style='color: #7f8c8d; font-size: 12px; margin-top: 30px;'>This is an automated reminder. Please do not reply to this email.</p>" +
               "</div>" +
               "</body>" +
               "</html>";
    }

    /**
     * Format time remaining in a human-readable format
     */
    private String formatTimeRemaining(long timeInMillis) {
        long minutes = timeInMillis / (1000 * 60);
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " and " + (hours % 24) + " hour" + ((hours % 24) != 1 ? "s" : "");
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " and " + (minutes % 60) + " minute" + ((minutes % 60) != 1 ? "s" : "");
        } else {
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        }
    }

    /**
     * Get count of reminders that will be sent in the next run
     * Useful for monitoring/logging
     */
    public int getUpcomingReminderCount() throws SQLException {
        logger.info("Calculating upcoming reminder count");

        try {
            List<Session> allSessions = sessionDAO.getAllSessions();

            long currentTime = System.currentTimeMillis();
            long twentyFourHoursLater = currentTime + (24 * 60 * 60 * 1000);
            long oneHourLater = currentTime + (60 * 60 * 1000);

            int count = 0;

            for (Session session : allSessions) {
                if (session.getScheduledDate() > currentTime && 
                    session.getScheduledDate() <= twentyFourHoursLater &&
                    session.getScheduledDate() >= oneHourLater &&
                    ("PENDING".equals(session.getStatus()) || "CONFIRMED".equals(session.getStatus()))) {
                    count++;
                }
            }

            logger.info("Upcoming reminders to be sent: {}", count);
            return count;

        } catch (SQLException e) {
            logger.error("Error calculating upcoming reminder count", e);
            throw e;
        }
    }
}
