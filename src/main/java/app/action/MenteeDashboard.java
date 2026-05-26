package app.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.security.RolesAllowed;
import app.model.Mentee;
import app.model.Mentor;
import app.bean.MenteeBean;
import app.bean.MentorBean;
import app.security.MentorKeSecurity;
import jakarta.inject.Inject;
import app.framework.Action;
import app.framework.ActionGetMethod;

@ApplicationScoped
@Action(value = "mentee-dashboard", label = "Mentee Dashboard")
public class MenteeDashboard extends BaseAction {

    @Inject
    private MenteeBean menteeBean;

    @Inject
    private MentorBean mentorBean;

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
            }

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        forward(request, response, "/mentee-dashboard.jsp");
    }
}
