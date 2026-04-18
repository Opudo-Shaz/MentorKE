package app;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "AdminDashboard", urlPatterns = {"/admin"})
public class AdminDashboard extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
            response.sendRedirect("login");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        if (!"admin".equalsIgnoreCase(role)) {
            response.sendRedirect("login");
            return;
        }

        // Forward to JSP
        request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
    }
}
