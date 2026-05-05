package app.action;

import app.dao.UserDAO;
import app.dao.MentorDAO;
import app.dao.MenteeDAO;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminDashboard", urlPatterns = {"/admin"})
public class AdminDashboard extends HttpServlet {

    @Inject
    private UserDAO userDAO;

    @Inject
    private MentorDAO mentorDAO;

    @Inject
    private MenteeDAO menteeDAO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String view = request.getParameter("view");
        if (view == null || view.isEmpty()) {
            view = "users";
        }

        request.setAttribute("view", view);

        try {

            if ("users".equalsIgnoreCase(view)) {
                List<?> users = userDAO.getAllUsers();
                request.setAttribute("users", users);
            }

            else if ("mentors".equalsIgnoreCase(view)) {
                request.setAttribute("mentors", mentorDAO.getAllMentors());
            }

            else if ("mentees".equalsIgnoreCase(view)) {
                request.setAttribute("mentees", menteeDAO.getAllMentees());
            }

            else {
                request.setAttribute("error", "Unknown view: " + view);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load data");
        }

        request.getRequestDispatcher("/admin-dashboard.jsp")
                .forward(request, response);
    }
}