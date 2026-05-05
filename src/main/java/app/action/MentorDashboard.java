package app.action;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "MentorDashboard", urlPatterns = {"/mentor-dashboard"})
public class MentorDashboard extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

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

        // Forward to JSP
        request.getRequestDispatcher("/mentor-dashboard.jsp").forward(request, response);
    }
}
