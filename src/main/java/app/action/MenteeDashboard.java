package app.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.security.RolesAllowed;
import app.model.Mentee;
import app.model.Mentor;
import app.security.websecurity.MentorKeSecurity;
import app.bean.MenteeBean;
import app.bean.MentorBean;
import app.bean.SessionBean;
import jakarta.inject.Inject;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.model.Session;

import java.util.List;

@ApplicationScoped
@Action(value = "mentee-dashboard", label = "Mentee Dashboard")
@RolesAllowed({"mentor","mentee","admin"})
public class MenteeDashboard extends BaseAction {

    @Inject
    private MenteeBean menteeBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private SessionBean sessionBean;

    @Inject
    private MentorKeSecurity security;

    @ActionGetMethod("")
    @RolesAllowed({"mentee"})
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireRole("mentee");

        try {
            String userId = getUserId(request);

            // Load mentee profile
            Mentee mentee = menteeBean.getByUserId(userId);
            request.setAttribute("mentee", mentee);

            // Load assigned mentor if mentee has one
            if (mentee != null && mentee.getMentorId() != null) {
                Mentor mentor = mentorBean.getById(mentee.getMentorId());
                request.setAttribute("mentor", mentor);

                String currentMentor = mentor != null && mentor.getUsername() != null
                        ? mentor.getUsername()
                        : (mentor != null ? "Mentor #" + mentor.getId() : "Not assigned");
                request.setAttribute("analyticsCurrentMentor", currentMentor);
            } else {
                request.setAttribute("analyticsCurrentMentor", "Not assigned");
            }

            List<Session> completedSessions = sessionBean.getCompletedSessions(userId);
            int sessionsAttended = completedSessions != null ? completedSessions.size() : 0;
            int mentorshipHours = 0;

            if (completedSessions != null) {
                for (Session session : completedSessions) {
                    Integer durationMinutes = session.getDurationMinutes();
                    if (durationMinutes != null && durationMinutes > 0) {
                        mentorshipHours += durationMinutes;
                    }
                }
            }

            int mentorshipHoursRounded = mentorshipHours / 60;
            int goalsProgress = calculateGoalsProgress(mentee != null ? mentee.getLearningGoals() : null, sessionsAttended, mentorshipHoursRounded);

            request.setAttribute("analyticsSessionsAttended", sessionsAttended);
            request.setAttribute("analyticsMentorshipHours", mentorshipHoursRounded);
            request.setAttribute("analyticsGoalsProgress", goalsProgress);

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        forward(request, response, "/mentee-dashboard.jsp");
    }

    private int calculateGoalsProgress(String learningGoals, int sessionsAttended, int mentorshipHours) {
        if (learningGoals == null || learningGoals.isBlank()) {
            return 0;
        }

        int score = (sessionsAttended * 12) + (mentorshipHours * 2);
        if (score > 100) {
            return 100;
        }
        if (score < 0) {
            return 0;
        }
        return score;
    }
}
