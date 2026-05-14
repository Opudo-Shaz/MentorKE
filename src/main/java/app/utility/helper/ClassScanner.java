package app.utility.helper;

import app.framework.DbTable;
import app.framework.PageMenuItem;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;


public class ClassScanner {


    public static Set<Class<?>> scanForDbTables(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(DbTable.class);
        return new HashSet<>(annotatedClasses);
    }


    public static Set<Class<?>> scanForMenuItem(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(PageMenuItem.class);
        return new HashSet<>(annotatedClasses);
    }
}

