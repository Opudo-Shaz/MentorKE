package app.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;

import app.model.Mentee;
import app.model.Mentor;
import jakarta.servlet.annotation.WebServlet;
import app.bean.MenteeBean;
import app.bean.MentorBean;
import jakarta.inject.Inject;

@WebServlet(name = "MenteeDashboard", urlPatterns = { "/mentee-dashboard" })
public class MenteeDashboard extends BaseAction {

    @Inject
    private MenteeBean menteeBean;

    @Inject 
    private MentorBean mentorBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
            response.sendRedirect("login");
            return;
        }

        if (!requireRole(request, response, "mentee")) {
            return;
        }

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

        request.getRequestDispatcher("/mentee-dashboard.jsp").forward(request, response);
    }
}
