package app.action;

import app.bean.UserBean;
import app.bean.MentorBean;
import app.bean.MenteeBean;
import app.model.User;
import app.model.Mentor;
import app.model.Mentee;
import app.security.MentorKeSecurity;
import app.framework.Action;
import app.framework.ActionGetMethod;
import app.utility.logging.AppLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.slf4j.Logger;

@ApplicationScoped
@Action(value = "admin", label = "Admin Dashboard")
@RolesAllowed({"mentor","mentee","admin"})
public class AdminDashboard extends BaseAction {

    private static final Logger logger = AppLogger.getLogger(AdminDashboard.class);

    @Inject
    private UserBean userBean;

    @Inject
    private MentorBean mentorBean;

    @Inject
    private MenteeBean menteeBean;

    @Inject
    private MentorKeSecurity security;

    @ActionGetMethod("")
    @RolesAllowed({"admin"})
    public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        security.requireRole("admin");

        String view = request.getParameter("view");
        if (view == null || view.isEmpty()) {
            view = "users";
        }

        logger.info("=== doGet called with view: {} ===", view);
        request.setAttribute("view", view);

        try {

            // load total count
            List<User> users = userBean.getAllUsers();
            List<Mentor> mentors = mentorBean.findAll();
            List<Mentee> mentees = menteeBean.findAll();

            request.setAttribute("users", users);
            request.setAttribute("mentors", mentors);
            request.setAttribute("mentees", mentees);

            logger.debug("Users: {}", users != null ? users.size() : 0);
            logger.debug("Mentors: {}", mentors != null ? mentors.size() : 0);
            logger.debug("Mentees: {}", mentees != null ? mentees.size() : 0);

            // Validate selected view only
            if (!"users".equalsIgnoreCase(view)
                    && !"mentors".equalsIgnoreCase(view)
                    && !"mentees".equalsIgnoreCase(view)) {

                logger.warn("Unknown view: {}", view);
                request.setAttribute("error", "Unknown view: " + view);
                view = "users";
            }

            request.setAttribute("view", view);

        } catch (Exception e) {
            logger.error("ERROR: {}", e.getMessage(), e);
            request.setAttribute("error", "Failed to load data: " + e.getMessage());
        }

        forward(request, response, "/admin-dashboard.jsp");
    }
}