package app.framework;

import app.utility.helper.ClassScanner;
import java.lang.reflect.Method;
import java.util.*;

public class ActionRegistry {

    private static final Map<String, List<ActionMap>> mappings = new HashMap<>();

    public static void scanAndRegister(String basePackage) {
        for (Class<?> clazz : ClassScanner.scanForAction(basePackage)) {
            if (!clazz.isAnnotationPresent(Action.class))
                continue;
            try {
                Object action = clazz.getDeclaredConstructor().newInstance();
                register(action);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void register(Object action) {
        Class<?> clazz = action.getClass();
        String actionPath = clazz.getAnnotation(Action.class).value();

        for (Method method : clazz.getDeclaredMethods()) {
            String httpMethod = null;
            String methodPath = null;

            if (method.isAnnotationPresent(ActionGetMethod.class)) {
                httpMethod = "GET";
                methodPath = method.getAnnotation(ActionGetMethod.class).value();
            }
            if (method.isAnnotationPresent(ActionPostMethod.class)) {
                httpMethod = "POST";
                methodPath = method.getAnnotation(ActionPostMethod.class).value();
            }

            if (httpMethod == null)
                continue;

            String actionMethodPath = ("/" + actionPath + "/" + methodPath)
                    .replaceAll("//+", "/");
// /mentee/session
            ActionMap actionMap = new ActionMap(clazz, method, httpMethod, actionMethodPath);
            mappings.computeIfAbsent(httpMethod, k -> new ArrayList<>()).add(actionMap);
            System.out.println("Mapped: " + actionMethodPath + " [" + httpMethod + "]");
        }
    }

    public static ActionMapMatch findMatch(String actionMethodPath, String httpMethod) {
        List<ActionMap> list = mappings.get(httpMethod);
        if (list == null)
            return null;

        for (ActionMap h : list) {
            Map<String, String> vars = pathMatch(h.getPathPattern(), actionMethodPath);
            if (vars != null)
                return new ActionMapMatch(h, vars);
        }

        return null;
    }

    private static Map<String, String> pathMatch(String pattern, String path) {
        String[] p1 = pattern.split("/");
        String[] p2 = path.split("/");

        if (p1.length != p2.length)
            return null;

        Map<String, String> vars = new HashMap<>();
        for (int i = 0; i < p1.length; i++) {
            if (p1[i].startsWith("{") && p1[i].endsWith("}")) {
                String key = p1[i].substring(1, p1[i].length() - 1);
                vars.put(key, p2[i]);
            } else if (!p1[i].equals(p2[i])) {
                return null;
            }
        }

        return vars;
    }
}