package app.action;

import app.bean.SessionBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.Session;
import app.security.websecurity.MentorKeSecurity;
import app.model.Mentor;
import app.model.Mentee;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.framework.ActionPostMethod;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
@Action(value = "sessions", label = "Sessions")
@RolesAllowed({"mentor","mentee","admin"})
public class SessionManagement extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(SessionManagement.class);

    @Inject
    private SessionBean sessionBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @Inject
    private MentorKeSecurity security;

    @ActionGetMethod("upcoming")
    @RolesAllowed({"mentor", "mentee"})
    public void upcoming(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireAuthentication();
        String userId = getUserId(request);
        handleUpcomingSessions(request, response, userId);
    }

    @ActionGetMethod("completed")
    @RolesAllowed({"mentor", "mentee"})
    public void completed(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireAuthentication();
        String userId = getUserId(request);
        handleCompletedSessions(request, response, userId);
    }

    @ActionGetMethod("view")
    @RolesAllowed({"mentor", "mentee"})
    public void view(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireAuthentication();
        String userId = getUserId(request);
        handleViewSession(request, response, userId);
    }

    @ActionGetMethod("schedule-form")
    public void scheduleForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        // If mentorId not provided, default to current user when mentor
        String mentorId = request.getParameter("mentorId");
        if ((mentorId == null || mentorId.isEmpty()) && isLoggedIn(request)) {
            mentorId = userId;
        }
        request.setAttribute("mentorId", mentorId);
        // Provide mentee list for mentor selection
        try {
            if (mentorId != null && !mentorId.isEmpty()) {
                request.setAttribute("mentees", menteeBean.findByMentorId(mentorId));
            }
        } catch (Exception e) {
            logger.warn("Could not load mentees for mentor {}", mentorId, e);
        }
        handleScheduleForm(request, response);
    }

    @ActionGetMethod("")
    public void defaultGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleUpcomingSessions(request, response, userId);
    }

    @ActionPostMethod("create-session")
    @RolesAllowed({"mentor"})
    public void createSession(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleCreateSession(request, response, userId);
    }

    @ActionPostMethod("cancel")
    public void cancel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleCancelSession(request, response, userId);
    }

    @ActionPostMethod("complete")
    public void complete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleCompleteSession(request, response, userId);
    }

    @ActionPostMethod("add-notes")
    public void addNotes(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleAddNotes(request, response, userId);
    }

    /**
     * Display upcoming sessions for the logged-in user
     */
    private void handleUpcomingSessions(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        logger.info("User {} viewing upcoming sessions", userId);

        try {
            List<Session> upcomingSessions = sessionBean.getUpcomingSessions(userId);

            for (Session session : upcomingSessions) {
                enrichSessionDetails(session);
            }

            setAttribute(request, "sessions", upcomingSessions);
            setAttribute(request, "sessionCount", upcomingSessions.size());
            forward(request, response, "/upcoming-sessions.jsp");

        } catch (Exception e) {
            logger.error("Error retrieving upcoming sessions", e);
            throw new ServletException(e);
        }
    }

    /**
     * Display completed sessions for the logged-in user
     */
    private void handleCompletedSessions(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        logger.info("User {} viewing completed sessions", userId);

        try {
            List<Session> completedSessions = sessionBean.getCompletedSessions(userId);

            for (Session session : completedSessions) {
                enrichSessionDetails(session);
            }

            setAttribute(request, "sessions", completedSessions);
            setAttribute(request, "sessionCount", completedSessions.size());
            forward(request, response, "/completed-sessions.jsp");

        } catch (Exception e) {
            logger.error("Error retrieving completed sessions", e);
            throw new ServletException(e);
        }
    }

    /**
     * View a specific session
     */
    private void handleViewSession(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        String sessionId = request.getParameter("sessionId");
        logger.info("Viewing session: {} for user: {}", sessionId, userId);

        try {
            Session session = sessionBean.getSession(sessionId);

            if (session == null) {
                setAttribute(request, "errorMessage", "Session not found");
                redirect(response, request.getContextPath() + "/app/sessions/upcoming");
                return;
            }

            if (!session.getMentorId().equals(userId) && !session.getMenteeId().equals(userId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
                return;
            }

            enrichSessionDetails(session);
            setAttribute(request, "session", session);
            forward(request, response, "/session-details.jsp");

        } catch (Exception e) {
            logger.error("Error retrieving session", e);
            throw new ServletException(e);
        }
    }

    /**
     * Display form to schedule a new session
     */
    private void handleScheduleForm(HttpServletRequest request, HttpServletResponse response)
        throws Exception {

    String mentorId = (String) request.getAttribute("mentorId");
    if (mentorId == null) mentorId = request.getParameter("mentorId");

    String menteeId = (String) request.getAttribute("menteeId");
    if (menteeId == null) menteeId = request.getParameter("menteeId");

    logger.info("Displaying schedule form for mentor: {}, mentee: {}", mentorId, menteeId);

    setAttribute(request, "mentorId", mentorId);
    setAttribute(request, "menteeId", menteeId);

    // Always reload mentees list so dropdown is never empty
    try {
        if (mentorId != null && !mentorId.isBlank()) {
            List mentees = menteeBean.findByMentorId(mentorId);
            setAttribute(request, "mentees", mentees);
            logger.debug("Loaded {} mentees for mentor {}", mentees.size(), mentorId);
        }
    } catch (Exception e) {
        logger.warn("Could not load mentees for mentor {}", mentorId, e);
    }

    forward(request, response, "/schedule-session.jsp");
}

    /**
     * Create a new session from mentor schedule form
     */
    private void handleCreateSession(HttpServletRequest request, HttpServletResponse response, String userId)
            throws ServletException, IOException {

        String mentorId = request.getParameter("mentorId");
        String menteeId = request.getParameter("menteeId");
        String scheduledDate = request.getParameter("scheduledDate");
        String durationRaw = request.getParameter("duration");
        String topic = request.getParameter("topic");

        logger.info("Creating session for mentor {} and mentee {}", mentorId, menteeId);

        try {
            if (mentorId == null || mentorId.isBlank()) {
                mentorId = userId;
            }

            if (menteeId == null || menteeId.isBlank()) {
                throw new IllegalArgumentException("Mentee is required");
            }

            if (scheduledDate == null || scheduledDate.isBlank()) {
                throw new IllegalArgumentException("Scheduled date is required");
            }

            int duration = Integer.parseInt(durationRaw);
            if (duration <= 0) {
                throw new IllegalArgumentException("Duration must be greater than zero");
            }

            // HTML datetime-local input emits yyyy-MM-dd'T'HH:mm (no seconds).
            LocalDateTime scheduledAt = LocalDateTime.parse(
                    scheduledDate,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            );

            sessionBean.scheduleSession(mentorId, menteeId, scheduledAt, duration, topic);

            setAttribute(request, "successMessage", "Session scheduled successfully.");
            redirect(response, request.getContextPath() + "/app/sessions/upcoming");

        } catch (Exception e) {
            logger.error("Error scheduling session", e);
            setAttribute(request, "errorMessage", "Error scheduling session: " + e.getMessage());
            setAttribute(request, "mentorId", mentorId);
            setAttribute(request, "menteeId", menteeId);
            try {
                if (mentorId != null && !mentorId.isBlank()) {
                    setAttribute(request, "mentees", menteeBean.findByMentorId(mentorId));
                }
            } catch (Exception ex) {
                logger.warn("Could not reload mentee list for mentor {}", mentorId, ex);
            }
            try {
                handleScheduleForm(request, response);
            } catch (Exception ex) {
                throw new ServletException(ex);
            }
        }
    }

    /**
     * Cancel a session
     */
    private void handleCancelSession(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        String sessionId = request.getParameter("sessionId");
        logger.info("Cancelling session: {}", sessionId);

        try {
            Session session = sessionBean.getSession(sessionId);

            if (session == null) {
                setAttribute(request, "errorMessage", "Session not found");
                redirect(response, request.getContextPath() + "/app/sessions/upcoming");
                return;
            }

            if (!session.getMentorId().equals(userId) && !session.getMenteeId().equals(userId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            sessionBean.cancelSession(sessionId);
            setAttribute(request, "successMessage", "Session cancelled successfully.");
            redirect(response, request.getContextPath() + "/app/sessions/upcoming");

        } catch (Exception e) {
            logger.error("Error cancelling session", e);
            setAttribute(request, "errorMessage", "Error cancelling session: " + e.getMessage());
            handleUpcomingSessions(request, response, userId);
        }
    }

    /**
     * Mark a session as completed (can be performed by mentor or mentee)
     */
    private void handleCompleteSession(HttpServletRequest request, HttpServletResponse response, String userId)
            throws ServletException, IOException {

        String sessionId = request.getParameter("sessionId");
        logger.info("Completing session: {} by user: {}", sessionId, userId);

        try {
            Session session = sessionBean.getSession(sessionId);

            if (session == null) {
                setAttribute(request, "errorMessage", "Session not found");
                redirect(response, request.getContextPath() + "/app/sessions/upcoming");
                return;
            }

            if (!session.getMentorId().equals(userId) && !session.getMenteeId().equals(userId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Only allow completing sessions that are not already completed or cancelled
            String status = session.getStatus() != null ? session.getStatus() : "";
            if ("COMPLETED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                setAttribute(request, "errorMessage", "Session cannot be marked complete");
                redirect(response, request.getContextPath() + "/app/sessions/upcoming");
                return;
            }

            sessionBean.updateSessionStatus(sessionId, "COMPLETED");
            setAttribute(request, "successMessage", "Session marked as completed.");
            redirect(response, request.getContextPath() + "/app/sessions/upcoming");

        } catch (Exception e) {
            logger.error("Error completing session", e);
            setAttribute(request, "errorMessage", "Error completing session: " + e.getMessage());
            handleUpcomingSessions(request, response, userId);
        }
    }

    /**
     * Add notes to a completed session
     */
    private void handleAddNotes(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        String sessionId = request.getParameter("sessionId");
        String notes = request.getParameter("notes");

        logger.info("Adding notes to session: {}", sessionId);

        try {
            Session session = sessionBean.getSession(sessionId);

            if (session == null) {
                setAttribute(request, "errorMessage", "Session not found");
                redirect(response, "sessions?action=completed");
                return;
            }

            if (!session.getMentorId().equals(userId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            sessionBean.addSessionNotes(sessionId, notes);
            setAttribute(request, "successMessage", "Notes added successfully.");
            handleViewSession(request, response, userId);

        } catch (Exception e) {
            logger.error("Error adding session notes", e);
            setAttribute(request, "errorMessage", "Error adding notes: " + e.getMessage());
            handleViewSession(request, response, userId);
        }
    }

    /**
     * Helper method to enrich session with mentor/mentee details
     */
    private void enrichSessionDetails(Session session) {
        try {
            Mentor mentor = mentorBean.getById(session.getMentorId());
            Mentee mentee = menteeBean.getById(session.getMenteeId());
            
            if (mentor != null) {
                session.getTopic();
            }
            if (mentee != null) {
                session.getTopic();
            }
        } catch (Exception e) {
            logger.warn("Could not enrich session details", e);
        }
    }
}
