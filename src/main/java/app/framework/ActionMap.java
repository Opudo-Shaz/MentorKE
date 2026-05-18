package app.framework;

import java.lang.reflect.Method;

public class ActionMap {

    public static final String APP_PATH = "/MentorKE/app/";

    private final Class<?> action;
    private final Method method;
    private final String httpMethod;
    private final String pathPattern;

    public ActionMap(Class<?> action, Method method, String httpMethod, String pathPattern) {
        this.action = action;
        this.method = method;
        this.httpMethod = httpMethod;
        this.pathPattern = pathPattern;
    }

    public Class<?> getAction()       { return action; }
    public Method getMethod()         { return method; }
    public String getHttpMethod()     { return httpMethod; }
    public String getPathPattern()    { return pathPattern; }
}
