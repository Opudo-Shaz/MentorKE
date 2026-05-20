package app.action;

import app.bean.SessionMatchingBean;
import app.bean.MatchRequestBean;
import app.bean.MenteeBean;
import app.bean.MentorBean;
import app.model.Mentee;
import app.model.Mentor;
import app.model.MatchRequest;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.ServletException;
import java.io.IOException;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;
import java.util.List;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.framework.ActionPostMethod;

@ApplicationScoped
@Action(value = "mentee-sessions", label = "Mentee Sessions", showLink = false)
public class MenteeSession extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(MenteeSession.class);

    @Inject
    private SessionMatchingBean sessionMatchingBean;

    @Inject
    private MatchRequestBean matchRequestBean;

    @Inject
    private MenteeBean menteeBean;

    @Inject
    private MentorBean mentorBean;

    @ActionGetMethod("browse")
    public void browse(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        if (!requireRole(request, response, "mentee")) return;
        String userId = getUserId(request);
        String specialization = request.getParameter("specialization");
        if (specialization == null || specialization.trim().isEmpty()) {
            try {
                Mentee mentee = menteeBean.getMenteeByUserId(userId);
                if (mentee != null && mentee.getFieldOfStudy() != null && !mentee.getFieldOfStudy().trim().isEmpty()) {
                    specialization = mentee.getFieldOfStudy();
                }
            } catch (Exception e) {
                logger.warn("Could not load mentee profile for specialization lookup: {}", userId, e);
            }
        }
        handleBrowseMentors(request, response, userId, specialization);
    }

    @ActionGetMethod("request")
    public void requestForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        if (!requireRole(request, response, "mentee")) return;
        String userId = getUserId(request);
        handleRequestMentor(request, response, userId);
    }

    @ActionGetMethod("my-requests")
    public void myRequests(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        if (!requireRole(request, response, "mentee")) return;
        String userId = getUserId(request);
        handleMyRequests(request, response, userId);
    }

    @ActionGetMethod("view-mentor")
    public void viewMentor(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        if (!requireRole(request, response, "mentee")) return;
        handleViewMentor(request, response);
    }

    @ActionGetMethod("")
    public void defaultGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        browse(request, response);
    }

    @ActionPostMethod("request-mentor")
    public void requestMentor(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        if (!requireRole(request, response, "mentee")) return;
        String userId = getUserId(request);

        String mentorId = request.getParameter("mentorId");
        String specialization = request.getParameter("specialization");

        matchRequestBean.requestMentor(userId, mentorId, specialization);
        request.setAttribute("successMessage", "Request sent to mentor successfully!");
        handleBrowseMentors(request, response, userId, specialization);
    }

    @ActionPostMethod("cancel-request")
    public void cancelRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        if (!requireRole(request, response, "mentee")) return;
        String userId = getUserId(request);

        String requestId = request.getParameter("requestId");
        MatchRequest req = matchRequestBean.getMatchRequest(requestId);
        if (req != null && req.getMenteeId().equals(userId)) {
            matchRequestBean.rejectMentorRequest(requestId);
            request.setAttribute("successMessage", "Request cancelled successfully!");
        }
        handleMyRequests(request, response, userId);
    }

    /**
     * Browse available mentors
     */
    private void handleBrowseMentors(HttpServletRequest request, HttpServletResponse response, String userId, String specialization) 
            throws ServletException, IOException {
        
        logger.info("Mentee {} browsing mentors", userId);
        List<Mentor> mentors;

        try {
            if (specialization != null && !specialization.trim().isEmpty()) {
                mentors = sessionMatchingBean.findMentorsBySpecialization(specialization);
            } else {
                mentors = mentorBean.getAllMentors();
            }

            request.setAttribute("mentors", mentors);
            request.setAttribute("selectedSpecialization", specialization);
            request.getRequestDispatcher("/browse-mentors.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error browsing mentors", e);
            throw new ServletException(e);
        }
    }

    /**
     * Handle mentor request
     */
    private void handleRequestMentor(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        String mentorId = request.getParameter("mentorId");
        logger.info("Mentee {} requesting mentor {}", userId, mentorId);

        try {
            Mentor mentor = mentorBean.getMentorById(mentorId);
            
            if (mentor != null) {
                request.setAttribute("mentor", mentor);
                request.getRequestDispatcher("/request-mentor.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Mentor not found");
                handleBrowseMentors(request, response, userId, request.getParameter("specialization"));
            }
        } catch (Exception e) {
            logger.error("Error viewing mentor request form", e);
            throw new ServletException(e);
        }
    }

    /**
     * View mentee's pending and approved requests
     */
    private void handleMyRequests(HttpServletRequest request, HttpServletResponse response, String userId) 
            throws ServletException, IOException {
        
        logger.info("Mentee {} viewing their requests", userId);

        try {
            List<MatchRequest> requests = matchRequestBean.getRequestsByMentee(userId);
            MatchRequest approvedMatch = matchRequestBean.getApprovedMatchForMentee(userId);

            request.setAttribute("requests", requests);
            request.setAttribute("approvedMatch", approvedMatch);
            request.getRequestDispatcher("/my-match-requests.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error retrieving match requests", e);
            throw new ServletException(e);
        }
    }

    /**
     * View mentor profile details
     */
    private void handleViewMentor(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String mentorId = request.getParameter("mentorId");
        logger.info("Viewing mentor profile: {}", mentorId);

        try {
            Mentor mentor = mentorBean.getMentorById(mentorId);

            if (mentor != null) {
                request.setAttribute("mentor", mentor);
                request.getRequestDispatcher("/mentor-profile.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Mentor not found");
                response.sendRedirect(request.getContextPath() + "/app/mentee-dashboard/");
            }
        } catch (Exception e) {
            logger.error("Error viewing mentor profile", e);
            throw new ServletException(e);
        }
    }
}
