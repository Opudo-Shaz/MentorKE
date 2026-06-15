package app.action;

import app.bean.MentorBean;
import app.dao.MenteeDAO;
import app.dao.SessionDAO;
import app.model.Mentor;
import app.utility.logging.AppLogger;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import java.util.Map;

@Named("mentorAnalytics")
public class MentorAnalytics {

    private static final Logger logger = AppLogger.getLogger(MentorAnalytics.class);

    @Inject private MentorBean mentorBean;
    @Inject private SessionDAO sessionDAO;
    @Inject private MenteeDAO  menteeDAO;

    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = (String) request.getSession().getAttribute("username");
        String mentorId = (String) request.getSession().getAttribute("userId");

        Mentor mentor = mentorBean.findAll().stream()
            .filter(m -> m.getUsername().equals(username))
            .findFirst().orElse(null);

        if (mentor == null || mentorId == null) {
            response.sendRedirect(request.getContextPath() + "/app/login/");
            return;
        }

        // Core metrics
        int completed  = sessionDAO.countCompletedSessionsByMentor(mentorId);
        int cancelled  = sessionDAO.countCancelledSessionsByMentor(mentorId);
        int pending    = sessionDAO.countPendingSessionsByMentor(mentorId);
        int hours      = sessionDAO.totalMentorshipHoursByMentor(mentorId);
        int menteeCount = menteeDAO.getMenteesByMentorId(mentorId).size();
        double rating  = sessionDAO.averageRatingByMentor(mentorId);

        // Monthly trend
        Map<String, Integer> monthly = sessionDAO.monthlySessionCountByMentor(mentorId);

        request.setAttribute("mentor",          mentor);
        request.setAttribute("completed",       completed);
        request.setAttribute("cancelled",       cancelled);
        request.setAttribute("pending",         pending);
        request.setAttribute("totalHours",      hours);
        request.setAttribute("menteeCount",     menteeCount);
        request.setAttribute("averageRating",   String.format("%.1f", rating));
        request.setAttribute("monthlyLabels",   String.join(",", monthly.keySet()));
        request.setAttribute("monthlyData",
            monthly.values().stream().map(String::valueOf).reduce((a,b) -> a+","+b).orElse("0"));

        request.getRequestDispatcher("/WEB-INF/views/mentor/mentor-analytics.jsp")
               .forward(request, response);
    }
}