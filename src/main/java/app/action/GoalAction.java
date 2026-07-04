package app.action;

import app.bean.GoalBean;
import app.dao.GoalDAO;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.model.MenteeGoal;
import app.security.websecurity.MentorKeSecurity;
import app.utility.logging.AppLogger;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import java.util.List;

@ApplicationScoped
@Action(value = "mentee-goals", label = "My Goals")
@RolesAllowed({"mentee"})
public class GoalAction extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(GoalAction.class);

    @Inject private GoalBean goalService;
    @Inject private MentorKeSecurity security;

    @ActionGetMethod("")
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireRole("mentee");

        String userId = getUserId(request);

        try {
            // get mentee id from session or user lookup
            Long menteeId = Long.parseLong(userId);

            List<MenteeGoal> goals = goalService.getGoalsForMentee(menteeId);
            request.setAttribute("goals", goals);

            // summary stats for the page header
            long completed  = goals.stream().filter(g -> "COMPLETED".equals(g.getStatus())).count();
            long inProgress = goals.size() - completed;
            int  avgProgress = goals.isEmpty() ? 0
                : goals.stream().mapToInt(MenteeGoal::getOverallProgress).sum() / goals.size();

            request.setAttribute("totalGoals",     goals.size());
            request.setAttribute("completedGoals", completed);
            request.setAttribute("inProgress",     inProgress);
            request.setAttribute("avgProgress",    avgProgress);

            logger.debug("Goals loaded for menteeId:{} total:{}", menteeId, goals.size());

        } catch (Exception e) {
            logger.error("Failed to load goals: {}", e.getMessage(), e);
            request.setAttribute("error", "Could not load goals: " + e.getMessage());
        }

        forward(request, response, "/mentee-goals.jsp");
    }
} 
    

