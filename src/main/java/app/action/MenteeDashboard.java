package app.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import app.model.Mentee;
import app.model.Mentor;
import app.bean.MenteeBean;
import app.bean.MentorBean;
import jakarta.inject.Inject;
import app.framework.Action;
import app.framework.ActionGetMethod;

@ApplicationScoped
@Action(value = "mentee-dashboard", label = "Mentee Dashboard", showLink = false)
public class MenteeDashboard extends BaseAction {

    @Inject
    private MenteeBean menteeBean;

    @Inject
    private MentorBean mentorBean;

    @ActionGetMethod("")
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!isLoggedIn(request)) { redirect(response, request.getContextPath() + "/app/login/"); return; }
        if (!requireRole(request, response, "mentee")) return;

        try {
            String userId = getUserId(request);

            // Load mentee profile
            Mentee mentee = menteeBean.getMenteeByUserId(userId);
            request.setAttribute("mentee", mentee);

            // Load assigned mentor if mentee has one
            if (mentee != null && mentee.getMentorId() != null) {
                Mentor mentor = mentorBean.getMentorById(mentee.getMentorId());
                request.setAttribute("mentor", mentor);
            }

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        forward(request, response, "/mentee-dashboard.jsp");
    }
}
