package app.utility.helper;

import app.framework.Action;
import app.framework.DbTable;
import app.framework.PageMenuItem;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public class ClassScanner {

    public static Set<Class<?>> scanForDbTables(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        return new HashSet<>(reflections.getTypesAnnotatedWith(DbTable.class));
    }

    public static Set<Class<?>> scanForMenuItem(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        return new HashSet<>(reflections.getTypesAnnotatedWith(PageMenuItem.class));
    }

    // called by ActionRegistry.scanAndRegister("app.action")
    public static Set<Class<?>> scanForAction(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        return new HashSet<>(reflections.getTypesAnnotatedWith(Action.class));
    }
}