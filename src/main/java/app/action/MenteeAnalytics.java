package app.action;

import app.bean.MenteeBean;
import app.dao.GoalDAO;
import app.dao.SessionDAO;
import app.model.Mentee;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.utility.logging.AppLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import java.util.Map;

@Named("menteeAnalytics")
@ApplicationScoped
@Action(value = "mentee-analytics", label = "Mentee Analytics")
public class MenteeAnalytics {

    private static final Logger logger = AppLogger.getLogger(MenteeAnalytics.class);

    @Inject private MenteeBean menteeBean;
    @Inject private SessionDAO sessionDAO;
    @Inject private GoalDAO    goalDAO;

    @ActionGetMethod("")
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object userIdAttr = request.getSession().getAttribute("userId");
        String menteeId = userIdAttr != null ? String.valueOf(userIdAttr) : null;
        String username = (String) request.getSession().getAttribute("username");

        Mentee mentee = menteeBean.findAll().stream()
            .filter(m -> m.getUsername().equals(username))
            .findFirst().orElse(null);

        if (mentee == null || menteeId == null) {
            response.sendRedirect(request.getContextPath() + "/app/login/");
            return;
        }

        // Core metrics
        int completed  = sessionDAO.countCompletedSessionsByMentee(menteeId);
        int hours      = sessionDAO.totalMentorshipHoursByMentee(menteeId);
        long goalsSet  = goalDAO.countByMenteeId(Long.parseLong(menteeId));
        long goalsDone = goalDAO.countByMenteeIdAndStatus(Long.parseLong(menteeId), "COMPLETED");
        int avgProgress = goalDAO.averageProgress(Long.parseLong(menteeId));

        // Monthly trend
        Map<String, Integer> monthly = sessionDAO.monthlySessionCountByMentee(menteeId);

        request.setAttribute("mentee",         mentee);
        request.setAttribute("completed",      completed);
        request.setAttribute("totalHours",     hours);
        request.setAttribute("goalsSet",       goalsSet);
        request.setAttribute("goalsDone",      goalsDone);
        request.setAttribute("avgProgress",    avgProgress);
        request.setAttribute("monthlyLabels",  String.join(",", monthly.keySet()));
        request.setAttribute("monthlyData",
            monthly.values().stream().map(String::valueOf).reduce((a,b) -> a+","+b).orElse("0"));

        request.getRequestDispatcher("/mentee-analytics.jsp")
               .forward(request, response);
    }
}