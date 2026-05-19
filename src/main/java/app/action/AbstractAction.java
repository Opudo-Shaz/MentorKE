package app.action;

import jakarta.servlet.http.HttpServletRequest;

public abstract class AbstractAction {

    protected long getUserId(HttpServletRequest req) {
        Object v = req.getSession().getAttribute("userId");
        if (v == null) throw new RuntimeException("Unauthenticated");
        return Long.parseLong(String.valueOf(v));
    }

    protected String getUserIdString(HttpServletRequest req) {
        Object v = req.getSession().getAttribute("userId");
        return v == null ? null : String.valueOf(v);
    }
}
