package app;

import jakarta.servlet.*;

import java.io.IOException;

public class Index implements Servlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("Servlet initialized");
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
       ServletResponse response = res;
        response.setContentType("text/html");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html>");
        response.getWriter().println("<head>");
        response.getWriter().println("<title>Home</title>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");
        response.getWriter().println("<h1>Hello, World!</h1>");
        response.getWriter().println("<p>Welcome to the Index servlet.</p>");
        response.getWriter().println("<a href=\"about\">About Us</a>");
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");

    }

    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void destroy() {
        System.out.println("Servlet destroyed");
    }
}
