package app.action;

import app.bean.SessionBean;
import app.bean.MenteeBean;
import app.framework.*;
import app.model.Session;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@ApplicationScoped
@Action(value = "mentee", label = "Mentee", linkPosition = 2)
public class MenteeAction extends AbstractAction {

    @Inject private SessionBean sessionBean;
    @Inject private MenteeBean menteeBean;
    @Inject private MentorKeFramework framework;
    @Inject private app.dao.SessionDAO sessionDAO;

    @ActionGetMethod("sessions")
    public ActionResponse sessions(HttpServletRequest req) {
        String userId = getUserIdString(req);
        try {
            List<Session> list = sessionBean.getSessionsByMentee(userId);
            return new ActionResponse(Session.class, list);
        } catch (Exception e) {
            return new ActionResponse("<div class='error'>Error loading sessions</div>");
        }
    }

    @ActionGetMethod("book")
    public ActionResponse bookForm() {
        return new ActionResponse(framework.htmlForm(Session.class));
    }

    @ActionPostMethod("book")
    public ActionResponse create(@ActionRequestBody Session session, HttpServletRequest req) {
        String userId = getUserIdString(req);
        session.setMenteeId(userId);
        // use DAO save directly; bean scheduling uses different signature
        sessionDAO.save(session);
        try {
            return new ActionResponse(Session.class, sessionBean.getSessionsByMentee(userId));
        } catch (Exception e) {
            return new ActionResponse("<div class='notice'>Saved</div>");
        }
    }

    @ActionGetMethod("delete/{id}")
    public ActionResponse delete(@ActionPathParam("id") Long id, HttpServletRequest req) {
        String userId = getUserIdString(req);
        try {
            Session s = sessionBean.getSession(String.valueOf(id));
            if (s != null && userId.equals(s.getMenteeId())) {
                sessionDAO.delete(id);
            }
            return new ActionResponse(Session.class, sessionBean.getSessionsByMentee(userId));
        } catch (Exception e) {
            return new ActionResponse("<div class='error'>Error deleting session</div>");
        }
    }
}
