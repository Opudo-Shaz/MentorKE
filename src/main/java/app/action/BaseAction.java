package app.action;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import app.framework.ActionMap;
import app.framework.ActionResponse;

import java.io.IOException;

public abstract class BaseAction {

    // ─── SESSION HELPERS ─────────────────────────────────────────────────────

    protected boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && Boolean.TRUE.equals(session.getAttribute("isLoggedIn"));
    }

    protected String getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object userId = session.getAttribute("userId");
        return userId != null ? String.valueOf(userId) : null;
    }

    protected String getUserRole(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute("role");
    }

    protected String getUsername(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute("username");
    }

    protected String getUserActualName(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute("UserActualName");
    }

    protected long getUserIdLong(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) throw new RuntimeException("Unauthenticated");
        Object userId = session.getAttribute("userId");
        if (userId == null) throw new RuntimeException("Unauthenticated");
        return Long.parseLong(String.valueOf(userId));
    }

    protected String getUserIdString(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object userId = session.getAttribute("userId");
        return userId != null ? String.valueOf(userId) : null;
    }

    // ─── AUTH GUARD HELPERS ───────────────────────────────────────────────────

    // Returns a redirect response if not logged in, null if authenticated
    protected ActionResponse requireLogin(HttpServletRequest req) {
        if (!isLoggedIn(req))
            return new ActionResponse(
                "<script>window.location='" + ActionMap.APP_PATH + "login/index'</script>"
            );
        return null;
    }

    // Returns a 403 response if role doesn't match, null if authorized
    protected ActionResponse requireRole(HttpServletRequest req, String requiredRole) {
        String role = getUserRole(req);
        if (role == null || !role.equalsIgnoreCase(requiredRole))
            return new ActionResponse("""
                <div class='container'>
                    <div class='card' style='text-align:center;'>
                        <h2 style='color:#d32f2f;'>Access Denied</h2>
                        <p>You do not have permission to view this page.</p>
                        <a href='%s' class='btn'>Go Home</a>
                    </div>
                </div>
                """.formatted(ActionMap.APP_PATH + "home/index"));
        return null;
    }

    protected boolean requireRole(HttpServletRequest req, HttpServletResponse resp, String requiredRole)
            throws IOException {
        ActionResponse denied = requireRole(req, requiredRole);
        if (denied == null) {
            return true;
        }
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write(denied.getResponseText());
        return false;
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(path);
        dispatcher.forward(req, resp);
    }

    protected void redirect(HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(path);
    }

    protected void setAttribute(HttpServletRequest req, String name, Object value) {
        req.setAttribute(name, value);
    }
}