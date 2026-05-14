package app.action;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


public abstract class BaseAction extends HttpServlet {

    /**
     * Check if user is logged in
     */
    protected boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && Boolean.TRUE.equals(session.getAttribute("isLoggedIn"));
    }

    /**
     * Get user ID from session
     */
    protected String getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute("userId");
    }

    /**
     * Get user role from session
     */
    protected String getUserRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute("role");
    }

    /**
     * Get username from session
     */
    protected String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute("username");
    }

    /**
     * Require login - redirects to login if not authenticated
     */
    protected void requireLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        if (!isLoggedIn(request)) {
            response.sendRedirect("login");
        }
    }

    /**
     * Require specific role - returns false and sends 403 if role doesn't match
     */
    protected boolean requireRole(
            HttpServletRequest request,
            HttpServletResponse response,
            String requiredRole
    ) throws IOException {
        String role = getUserRole(request);
        if (role == null || !role.equalsIgnoreCase(requiredRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    /**
     * Forward request to JSP with request dispatcher
     */
    protected void forward(
            HttpServletRequest request,
            HttpServletResponse response,
            String jsp
    ) throws Exception {
        request.getRequestDispatcher(jsp).forward(request, response);
    }

    /**
     * Redirect to specified path
     */
    protected void redirect(
            HttpServletResponse response,
            String path
    ) throws IOException {
        response.sendRedirect(path);
    }

    /**
     * Set attribute helper
     */
    protected void setAttribute(
            HttpServletRequest request,
            String name,
            Object value
    ) {
        request.setAttribute(name, value);
    }

    /**
     * Get attribute helper
     */
    protected Object getAttribute(
            HttpServletRequest request,
            String name
    ) {
        return request.getAttribute(name);
    }
}

