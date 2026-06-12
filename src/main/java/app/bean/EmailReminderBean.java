package app.bean;

import app.dao.SessionDAO;
import app.dao.MentorDAO;
import app.dao.MenteeDAO;
import app.model.Session;
import app.model.Mentor;
import app.model.Mentee;
import app.utility.logging.AppLogger;
import jakarta.annotation.Resource;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


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

    @Resource(lookup = "java:/Mail")
    private jakarta.mail.Session mailSession;

    public EmailReminderBean() {

        logger.debug("EmailReminderBean initialized");
    }



    public void sendEmail(String to, String subject, String body) {
        logger.info("Attempting to send email to: {}", to);
        try {
            MimeMessage message = new MimeMessage(mailSession);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setFrom(new InternetAddress("patikalostandfound@gmail.com", "MentorKE"));

            if (body.contains("<html>") || body.contains("<!DOCTYPE")) {
                message.setContent(body, "text/html; charset=UTF-8");
            } else {
                message.setText(body);
            }

            Transport.send(message);
            logger.info("Email sent successfully to {}", to);

        } catch (MessagingException e) {
            logger.error("Mail error: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error sending email: {}", e.getMessage(), e);
        }
    }



     // Runs every 30 minutes. Sends reminders for sessions

    @Schedule(hour = "*", minute = "*/30", persistent = false, info = "Session Reminder Scheduler")
    public void sendSessionReminders() {
        logger.info("=== Starting scheduled session reminder task ===");
        try {
            LocalDateTime now               = LocalDateTime.now();
            LocalDateTime in24Hours         = now.plusHours(24);
            LocalDateTime inOneHour         = now.plusHours(1);
            int remindersSent = 0;

            for (Session session : sessionDAO.findAll()) {
                if (session.getScheduledDate() != null
                        && session.getScheduledDate().isAfter(inOneHour)
                        && session.getScheduledDate().isBefore(in24Hours)
                        && ("PENDING".equals(session.getStatus()) || "CONFIRMED".equals(session.getStatus()))) {
                    try {
                        sendSessionReminderEmails(session);
                        remindersSent++;
                    } catch (Exception e) {
                        logger.error("Error sending reminder for session {}", session.getId(), e);
                    }
                }
            }

            logger.info("=== Session reminder task completed. Reminders sent: {} ===", remindersSent);

        } catch (Exception e) {
            logger.error("Unexpected error in session reminder scheduler", e);
        }
    }

    /**
     * Send reminder emails to both mentor and mentee for a given session.
     */
    public void sendSessionReminderEmails(Session session) {
        logger.info("Sending reminder emails for session: {}", session.getId());
        try {
            Mentor mentor = mentorDAO.findById(Long.parseLong(session.getMentorId()));
            Mentee mentee = menteeDAO.findById(Long.parseLong(session.getMenteeId()));

            if (mentor == null || mentee == null) {
                logger.warn("Mentor or mentee not found for session: {}", session.getId());
                return;
            }

            String timeRemaining = formatTimeRemaining(session.getScheduledDate(), LocalDateTime.now());
            String topic = session.getTopic();

            sendEmail(mentor.getEmail(),
                    "Reminder: Upcoming Session - " + topic,
                    buildReminderEmailBody(session, mentor.getUsername(), mentee.getUsername(), timeRemaining, true));

            sendEmail(mentee.getEmail(),
                    "Reminder: Upcoming Session - " + topic,
                    buildReminderEmailBody(session, mentee.getUsername(), mentor.getUsername(), timeRemaining, false));

            logger.info("Reminder emails sent for session {} to {} and {}",
                    session.getId(), mentor.getEmail(), mentee.getEmail());

        } catch (Exception e) {
            logger.error("Error sending session reminder emails", e);
        }
    }

    /**
     * Trigger a reminder for a single session by ID (mentor + mentee).
     */
    public void sendSessionReminder(String sessionId) {
        Session session = sessionDAO.findById(Long.parseLong(sessionId));
        if (session != null) {
            sendSessionReminderEmails(session);
        }
    }

    /**
     * Count sessions that will receive reminders on the next scheduler run.
     */
    public int getUpcomingReminderCount() {
        LocalDateTime now       = LocalDateTime.now();
        LocalDateTime in24Hours = now.plusHours(24);
        LocalDateTime inOneHour = now.plusHours(1);
        int count = 0;

        for (Session session : sessionDAO.findAll()) {
            if (session.getScheduledDate() != null
                    && session.getScheduledDate().isAfter(inOneHour)
                    && session.getScheduledDate().isBefore(in24Hours)
                    && ("PENDING".equals(session.getStatus()) || "CONFIRMED".equals(session.getStatus()))) {
                count++;
            }
        }

        logger.info("Upcoming reminders to be sent: {}", count);
        return count;
    }

    /**
     *
     * @param isMentor true  → recipient is the mentor (blue accent)
     *                 false → recipient is the mentee (green accent)
     */
    private String buildReminderEmailBody(Session session, String recipientName,
                                          String otherPartyName, String timeRemaining,
                                          boolean isMentor) {
        String accentColor  = isMentor ? "#0d47a1" : "#15803d";
        String roleLabel    = isMentor ? "mentee"  : "mentor";
        String sessionDateTime = session.getScheduledDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a"));

        return "<html>" +
                "<body style='font-family:Arial,sans-serif;background:#f5f5f5;padding:20px;'>" +
                "<div style='background:#fff;border-radius:8px;padding:30px;max-width:600px;margin:0 auto;'>" +
                "<h2 style='color:#1e293b;border-bottom:2px solid " + accentColor + ";padding-bottom:10px;'>Session Reminder</h2>" +
                "<p>Hi " + recipientName + ",</p>" +
                "<p>This is a reminder about your upcoming mentoring session with <strong>" + otherPartyName + "</strong> (" + roleLabel + ").</p>" +
                "<div style='background:#f1f5f9;border-left:4px solid " + accentColor + ";padding:15px;margin:20px 0;'>" +
                "<p><strong>Topic:</strong> " + session.getTopic() + "</p>" +
                "<p><strong>Date &amp; Time:</strong> " + sessionDateTime + "</p>" +
                "<p><strong>Duration:</strong> " + session.getDurationMinutes() + " minutes</p>" +
                "<p><strong>Time remaining:</strong> " + timeRemaining + "</p>" +
                "</div>" +
                "<p><a href='" + session.getSessionLink() + "' " +
                "style='display:inline-block;background:" + accentColor + ";color:#fff;" +
                "padding:10px 20px;border-radius:5px;text-decoration:none;'>Join Session</a></p>" +
                "<p style='font-size:13px;color:#475569;'>Or copy this link: " +
                "<a href='" + session.getSessionLink() + "'>" + session.getSessionLink() + "</a></p>" +
                "<p style='color:#94a3b8;font-size:12px;margin-top:30px;'>This is an automated reminder. Please do not reply to this email.</p>" +
                "</div></body></html>";
    }

// helper method to format remaining time to human readable form
    private String formatTimeRemaining(LocalDateTime scheduledDate, LocalDateTime now) {
        long minutes = ChronoUnit.MINUTES.between(now, scheduledDate);
        long hours   = minutes / 60;
        long days    = hours   / 24;

        if (days > 0) {
            long remainingHours = hours % 24;
            return days + " day" + (days > 1 ? "s" : "")
                    + " and " + remainingHours + " hour" + (remainingHours != 1 ? "s" : "");
        } else if (hours > 0) {
            long remainingMins = minutes % 60;
            return hours + " hour" + (hours > 1 ? "s" : "")
                    + " and " + remainingMins + " minute" + (remainingMins != 1 ? "s" : "");
        } else {
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        }
    }
}