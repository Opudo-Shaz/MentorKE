package app.action;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import app.bean.MenteeBean;
import app.bean.MentorBean;
import app.model.Mentee;
import app.model.Mentor;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "MentorDashboard", urlPatterns = { "/mentor-dashboard" })
public class MentorDashboard extends HttpServlet {
    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
            response.sendRedirect("login");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        if (!"mentor".equalsIgnoreCase(role)) {
            response.sendRedirect("login");
            return;
        }

        try {
            String userId = String.valueOf(session.getAttribute("userId"));

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

        request.getRequestDispatcher("/mentor-dashboard.jsp").forward(request, response);
    }
}
