package app.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;

import app.bean.MenteeBean;
import app.bean.MentorBean;
import app.security.MentorKeSecurity;
import app.model.Mentee;
import app.model.Mentor;
import app.framework.Action;
import app.framework.ActionGetMethod;

@ApplicationScoped
@Action(value = "mentor-dashboard", label = "Mentor Dashboard")
@RolesAllowed({"mentor","mentee","admin"})
public class MentorDashboard extends BaseAction {
    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @Inject
    private MentorKeSecurity security;

    @ActionGetMethod("")
    @RolesAllowed({"mentor"})
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireRole("mentor");

        try {
            String userId = getUserId(request);

            // Load mentor profile
            Mentor mentor = mentorBean.getByUserId(userId);
            request.setAttribute("mentor", mentor);

            // Load this mentor's mentees
            if (mentor != null) {
                List<Mentee> mentees = menteeBean.findByMentorId(String.valueOf(mentor.getId()));
                request.setAttribute("mentees", mentees);
            }

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
        }

        forward(request, response, "/mentor-dashboard.jsp");
    }
}
