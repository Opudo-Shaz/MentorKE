package app.utility.helper;

import app.framework.Action;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public class ClassScanner {

    // called by ActionRegistry.scanAndRegister("app.action")
    public static Set<Class<?>> scanForAction(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        return new HashSet<>(reflections.getTypesAnnotatedWith(Action.class));
    }
}