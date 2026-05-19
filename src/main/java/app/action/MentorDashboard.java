package app.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;

import app.bean.MenteeBean;
import app.bean.MentorBean;
import app.model.Mentee;
import app.model.Mentor;
import app.framework.Action;
import app.framework.ActionGetMethod;

@ApplicationScoped
@Action(value = "mentor-dashboard", label = "Mentor Dashboard", showLink = false)
public class MentorDashboard extends BaseAction {
    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @ActionGetMethod("")
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        if (!requireRole(request, response, "mentor")) return;

        try {
            String userId = getUserId(request);

            // Load mentor profile
            Mentor mentor = mentorBean.getMentorByUserId(userId);
            request.setAttribute("mentor", mentor);

            // Load this mentor's mentees
            if (mentor != null) {
                List<Mentee> mentees = menteeBean.getMenteesByMentorId(String.valueOf(mentor.getId()));
                request.setAttribute("mentees", mentees);
            }

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        forward(request, response, "/mentor-dashboard.jsp");
    }
}
