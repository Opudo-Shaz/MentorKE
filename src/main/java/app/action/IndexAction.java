package app.action;

import app.framework.Action;
import app.framework.ActionGetMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Action("home")
public class IndexAction extends BaseAction {

    @ActionGetMethod("")
    public void index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        forward(request, response, "/index.jsp");
    }
}
