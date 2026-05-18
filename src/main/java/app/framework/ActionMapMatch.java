package app.framework;

import java.util.Map;

public class ActionMapMatch {

    private final ActionMap actionMap;
    private final Map<String, String> pathVariables;

    public ActionMapMatch(ActionMap actionMap, Map<String, String> pathVariables) {
        this.actionMap = actionMap;
        this.pathVariables = pathVariables;
    }

    public ActionMap getActionMap()              { return actionMap; }
    public Map<String, String> getPathVariables() { return pathVariables; }
}