package app.action;

import app.bean.SessionBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.Session;
import app.model.Mentor;
import app.model.Mentee;
import jakarta.inject.Inject;
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
import java.text.SimpleDateFormat;
import java.util.List;

@ApplicationScoped
@Action(value = "sessions", label = "Sessions", showLink = false)
public class SessionManagement extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(SessionManagement.class);

    @Inject
    private SessionBean sessionBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @ActionGetMethod("upcoming")
    public void upcoming(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleUpcomingSessions(request, response, userId);
    }

    @ActionGetMethod("completed")
    public void completed(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleCompletedSessions(request, response, userId);
    }

    @ActionGetMethod("view")
    public void view(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleViewSession(request, response, userId);
    }

    @ActionGetMethod("schedule-form")
    public void scheduleForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        handleScheduleForm(request, response);
    }

    @ActionGetMethod("")
    public void defaultGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        String userId = getUserId(request);
        handleUpcomingSessions(request, response, userId);
    }

    @ActionPostMethod("create-session")
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
                redirect(response, "sessions?action=upcoming");
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
        
        String mentorId = request.getParameter("mentorId");
        String menteeId = request.getParameter("menteeId");

        logger.info("Displaying schedule form for mentor: {}, mentee: {}", mentorId, menteeId);

        setAttribute(request, "mentorId", mentorId);
        setAttribute(request, "menteeId", menteeId);
        forward(request, response, "/schedule-session.jsp");
    }

    /**
     * Create a new session
     */
    private void handleCreateSession(HttpServletRequest request, HttpServletResponse response, String userId)
            throws Exception {
        
        String mentorId = request.getParameter("mentorId");
        String menteeId = request.getParameter("menteeId");
        String scheduledDateStr = request.getParameter("scheduledDate");
        String durationStr = request.getParameter("duration");
        String topic = request.getParameter("topic");

        logger.info("Creating session - Mentor: {}, Mentee: {}, Topic: {}", mentorId, menteeId, topic);

        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            java.time.LocalDateTime scheduledDate = java.time.LocalDateTime.parse(scheduledDateStr, formatter);
            Integer duration = Integer.parseInt(durationStr);

            if (!userId.equals(mentorId) && !userId.equals(menteeId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String sessionId = sessionBean.scheduleSession(mentorId, menteeId, scheduledDate, duration, topic);

            setAttribute(request, "successMessage", "Session scheduled successfully! Meeting link has been sent to both parties.");
            handleViewSession(request, response, userId);

        } catch (Exception e) {
            logger.error("Error creating session", e);
            setAttribute(request, "errorMessage", "Error scheduling session: " + e.getMessage());
            handleScheduleForm(request, response);
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
                redirect(response, "sessions?action=upcoming");
                return;
            }

            if (!session.getMentorId().equals(userId) && !session.getMenteeId().equals(userId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            sessionBean.cancelSession(sessionId);
            setAttribute(request, "successMessage", "Session cancelled successfully.");
            redirect(response, "sessions?action=upcoming");

        } catch (Exception e) {
            logger.error("Error cancelling session", e);
            setAttribute(request, "errorMessage", "Error cancelling session: " + e.getMessage());
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
            Mentor mentor = mentorBean.getMentorById(session.getMentorId());
            Mentee mentee = menteeBean.getMenteeById(session.getMenteeId());
            
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
