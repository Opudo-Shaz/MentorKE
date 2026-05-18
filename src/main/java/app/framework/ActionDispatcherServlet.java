package app.framework;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/app/*")
public class ActionDispatcherServlet extends HttpServlet {

    @Inject
    private MentorKeFramework framework;

    @Inject
    private AppPage appPage;

    @Override
    public void init() {
        ActionRegistry.scanAndRegister("app.action");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String requestPath = req.getPathInfo();
        String httpMethod = req.getMethod();

        ActionMapMatch actionMapMatch = ActionRegistry.findMatch(requestPath, httpMethod);

        if (actionMapMatch == null) {
            resp.sendError(404);
            return;
        }

        try {
            // 1. Resolve the action class instance via CDI so @Inject fields work inside it
            Object actionCtxInstance = CDI.current()
                .select(actionMapMatch.getActionMap().getAction())
                .get();

            // 2. Bind request params to method arguments via annotations
            Object[] argsParams = ActionParamBinder.bind(
                actionMapMatch.getActionMap(),
                req,
                resp,
                actionMapMatch.getPathVariables()
            );

            // 3. Invoke the matched handler method via reflection
            ActionResponse actionResponse = (ActionResponse) actionMapMatch
                .getActionMap()
                .getMethod()
                .invoke(actionCtxInstance, argsParams);

            // 4. Decide what to render
            String displayContent;
            if (actionResponse.getResponseText() != null)
                displayContent = actionResponse.getResponseText();
            else
                displayContent = framework.htmlTable(
                    actionResponse.getResponseClazz(),
                    actionResponse.getResponseDataList()
                );

            // 5. Wrap in the full HTML layout and write to response
            appPage.display(req, resp, displayContent);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}