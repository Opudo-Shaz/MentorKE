package app.action;

import app.framework.Action;
import app.framework.ActionGetMethod;
import app.framework.ActionResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
@Action(value = "about", label = "About", showLink = false)
public class AboutAction {

    @ActionGetMethod("")
    public ActionResponse index(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher("/about.jsp").forward(request, response);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
