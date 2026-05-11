package app.action;

import app.bean.UserBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.User;
import app.model.Mentor;
import app.model.Mentee;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

@WebServlet(name = "AdminDashboard", urlPatterns = {"/admin"})
public class AdminDashboard extends HttpServlet {

    private static final Logger logger = AppLogger.getLogger(AdminDashboard.class);

    @Inject
    private UserBean userBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String view = request.getParameter("view");
        if (view == null || view.isEmpty()) {
            view = "users";
        }

        logger.info("=== doGet called with view: {} ===", view);
        request.setAttribute("view", view);

        try {

            if ("users".equalsIgnoreCase(view)) {
                List<User> users = userBean.getAllUsers();
                logger.debug("Retrieved {} users", users != null ? users.size() : "null");
                request.setAttribute("users", users);
            }

            else if ("mentors".equalsIgnoreCase(view)) {
                List<Mentor> mentors = mentorBean.getAllMentors();
                logger.debug("Retrieved {} mentors", mentors != null ? mentors.size() : "null");
                request.setAttribute("mentors", mentors);
            }

            else if ("mentees".equalsIgnoreCase(view)) {
                List<Mentee> mentees = menteeBean.getAllMentees();
                logger.debug("Retrieved {} mentees", mentees != null ? mentees.size() : "null");
                request.setAttribute("mentees", mentees);
            }

            else {
                logger.warn("Unknown view: {}", view);
                request.setAttribute("error", "Unknown view: " + view);
            }

        } catch (Exception e) {
            logger.error("ERROR: {}", e.getMessage());
            request.setAttribute("error", "Failed to load data: " + e.getMessage());
        }

        request.getRequestDispatcher("/admin-dashboard.jsp")
                .forward(request, response);
    }
}