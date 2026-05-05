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

        System.out.println("\n[AdminDashboard] === doGet called with view: " + view + " ===");
        request.setAttribute("view", view);

        try {

            if ("users".equalsIgnoreCase(view)) {
                List<?> users = userDAO.getAllUsers();
                System.out.println("[AdminDashboard] Retrieved " + (users != null ? users.size() : "null") + " users");
                request.setAttribute("users", users);
            }

            else if ("mentors".equalsIgnoreCase(view)) {
                List<?> mentors = mentorDAO.getAllMentors();
                System.out.println("[AdminDashboard] Retrieved " + (mentors != null ? mentors.size() : "null") + " mentors");
                request.setAttribute("mentors", mentors);
            }

            else if ("mentees".equalsIgnoreCase(view)) {
                List<?> mentees = menteeDAO.getAllMentees();
                System.out.println("[AdminDashboard] Retrieved " + (mentees != null ? mentees.size() : "null") + " mentees");
                request.setAttribute("mentees", mentees);
            }

            else {
                System.out.println("[AdminDashboard] Unknown view: " + view);
                request.setAttribute("error", "Unknown view: " + view);
            }

        } catch (Exception e) {
            System.err.println("[AdminDashboard] ERROR: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Failed to load data: " + e.getMessage());
        }

        request.getRequestDispatcher("/admin-dashboard.jsp")
                .forward(request, response);
    }
}