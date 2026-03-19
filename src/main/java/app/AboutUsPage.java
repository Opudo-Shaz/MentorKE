package app;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

public class AboutUsPage extends GenericServlet {
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        ServletResponse response = res;
        response.setContentType("text/html");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html>");
        response.getWriter().println("<head>");
        response.getWriter().println("<title>About Us</title>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");
        response.getWriter().println("<h1>About Us</h1>");
        response.getWriter().println("<p>This is the About Us page of our website.</p>");
        response.getWriter().println("<a href=\"index\">Back to Home</a>");
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
    }
}
