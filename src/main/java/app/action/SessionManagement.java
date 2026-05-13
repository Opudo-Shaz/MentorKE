package app.action;

import app.bean.SessionBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.Session;
import app.model.Mentor;
import app.model.Mentee;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * SessionManagement - Handles session scheduling and management
 * URL: /sessions
 * Actions: schedule, view-session, upcoming, completed, cancel
 */
@WebServlet(name = "SessionManagement", urlPatterns = {"/sessions"})
public class SessionManagement extends HttpServlet {

    private static final Logger logger = AppLogger.getLogger(SessionManagement.class);

    @Inject
    private SessionBean sessionBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession httpSession = request.getSession(false);

        // Check if user is logged in
        if (httpSession == null || !Boolean.TRUE.equals(httpSession.getAttribute("isLoggedIn"))) {
            response.sendRedirect("login");
            return;
        }

        String userId = (String) httpSession.getAttribute("userId");

        try {
            if ("upcoming".equalsIgnoreCase(action)) {
                handleUpcomingSessions(request, response, userId);
            } else if ("completed".equalsIgnoreCase(action)) {
                handleCompletedSessions(request, response, userId);
            } else if ("view".equalsIgnoreCase(action)) {
                handleViewSession(request, response, userId);
            } else if ("schedule-form".equalsIgnoreCase(action)) {
                handleScheduleForm(request, response);
            } else {
                handleUpcomingSessions(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error in SessionManagementAction", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/mentee-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || !Boolean.TRUE.equals(httpSession.getAttribute("isLoggedIn"))) {
            response.sendRedirect("login");
            return;
        }

        String userId = (String) httpSession.getAttribute("userId");

        try {
            if ("create-session".equalsIgnoreCase(action)) {
                handleCreateSession(request, response, userId);
            } else if ("cancel".equalsIgnoreCase(action)) {
                handleCancelSession(request, response, userId);
            } else if ("add-notes".equalsIgnoreCase(action)) {
                handleAddNotes(request, response, userId);
            }
        } catch (Exception e) {
            logger.error("Error processing session action", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/mentee-dashboard.jsp").forward(request, response);
        }
    }

    /**
     * Display upcoming sessions for the logged-in user
     */
    private void handleUpcomingSessions(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        logger.info("User {} viewing upcoming sessions", userId);

        try {
            List<Session> upcomingSessions = sessionBean.getUpcomingSessions(userId);

            // Enrich with user details
            for (Session session : upcomingSessions) {
                enrichSessionDetails(session);
            }

            request.setAttribute("sessions", upcomingSessions);
            request.setAttribute("sessionCount", upcomingSessions.size());
            request.getRequestDispatcher("/upcoming-sessions.jsp").forward(request, response);

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

            request.setAttribute("sessions", completedSessions);
            request.setAttribute("sessionCount", completedSessions.size());
            request.getRequestDispatcher("/completed-sessions.jsp").forward(request, response);

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
                request.setAttribute("errorMessage", "Session not found");
                response.sendRedirect("sessions?action=upcoming");
                return;
            }

            // Verify user is part of this session
            if (!session.getMentorId().equals(userId) && !session.getMenteeId().equals(userId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
                return;
            }

            enrichSessionDetails(session);
            request.setAttribute("session", session);
            request.getRequestDispatcher("/session-details.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error retrieving session", e);
            throw new ServletException(e);
        }
    }

    /**
     * Display form to schedule a new session
     */
    private void handleScheduleForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String mentorId = request.getParameter("mentorId");
        String menteeId = request.getParameter("menteeId");

        logger.info("Displaying schedule form for mentor: {}, mentee: {}", mentorId, menteeId);

        request.setAttribute("mentorId", mentorId);
        request.setAttribute("menteeId", menteeId);
        request.getRequestDispatcher("/schedule-session.jsp").forward(request, response);
    }

    /**
     * Create a new session
     */
    private void handleCreateSession(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        String mentorId = request.getParameter("mentorId");
        String menteeId = request.getParameter("menteeId");
        String scheduledDateStr = request.getParameter("scheduledDate");
        String durationStr = request.getParameter("duration");
        String topic = request.getParameter("topic");

        logger.info("Creating session - Mentor: {}, Mentee: {}, Topic: {}", mentorId, menteeId, topic);

        try {
            // Parse date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            long scheduledDate = sdf.parse(scheduledDateStr).getTime();
            Integer duration = Integer.parseInt(durationStr);

            // Verify user is either mentor or mentee
            if (!userId.equals(mentorId) && !userId.equals(menteeId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String sessionId = sessionBean.scheduleSession(mentorId, menteeId, scheduledDate, duration, topic);

            request.setAttribute("successMessage", "Session scheduled successfully! Meeting link has been sent to both parties.");
            handleViewSession(request, response, userId);
            // Alternatively, redirect to upcoming sessions
            // response.sendRedirect("sessions?action=upcoming");

        } catch (Exception e) {
            logger.error("Error creating session", e);
            request.setAttribute("errorMessage", "Error scheduling session: " + e.getMessage());
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
                request.setAttribute("errorMessage", "Session not found");
                response.sendRedirect("sessions?action=upcoming");
                return;
            }

            // Verify user is part of this session
            if (!session.getMentorId().equals(userId) && !session.getMenteeId().equals(userId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            sessionBean.cancelSession(sessionId);
            request.setAttribute("successMessage", "Session cancelled successfully.");
            response.sendRedirect("sessions?action=upcoming");

        } catch (Exception e) {
            logger.error("Error cancelling session", e);
            request.setAttribute("errorMessage", "Error cancelling session: " + e.getMessage());
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
                request.setAttribute("errorMessage", "Session not found");
                response.sendRedirect("sessions?action=completed");
                return;
            }

            // Verify user is the mentor
            if (!session.getMentorId().equals(userId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            sessionBean.addSessionNotes(sessionId, notes);
            request.setAttribute("successMessage", "Notes added successfully.");
            handleViewSession(request, response, userId);

        } catch (Exception e) {
            logger.error("Error adding session notes", e);
            request.setAttribute("errorMessage", "Error adding notes: " + e.getMessage());
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
                session.getTopic();  // Keep mentor available through session context
            }
            if (mentee != null) {
                session.getTopic();  // Keep mentee available through session context
            }
        } catch (Exception e) {
            logger.warn("Could not enrich session details", e);
        }
    }
}
