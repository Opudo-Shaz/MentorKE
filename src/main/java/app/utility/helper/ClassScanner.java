package app.utility.helper;
import app.framework.DbTable;
import app.framework.PageMenuItem;
import org.reflections.Reflections;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;


public class ClassScanner {
    public static Set<Class<?>> scanForDbTables(String basePackage) {
        System.out.println("[ClassScanner] Scanning for @DbTable classes in package: " + basePackage);
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(DbTable.class);
        System.out.println("[ClassScanner] Found " + annotatedClasses.size() + " @DbTable classes");
        for (Class<?> clazz : annotatedClasses) {
            System.out.println("[ClassScanner]   - " + clazz.getName());
        }
        return new HashSet<>(annotatedClasses);
    }
    public static Set<Class<?>> scanForMenuItem(String basePackage) {
        System.out.println("[ClassScanner] Scanning for @PageMenuItem classes in package: " + basePackage);
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(PageMenuItem.class);
        System.out.println("[ClassScanner] Found " + annotatedClasses.size() + " @PageMenuItem classes");
        for (Class<?> clazz : annotatedClasses) {
            System.out.println("[ClassScanner]   - " + clazz.getName());
        }
        return new HashSet<>(annotatedClasses);
    }
    public static Set<Class<?>> scanForAnnotation(String basePackage, Class<? extends Annotation> annotationClass) {
        System.out.println("[ClassScanner] Scanning for @" + annotationClass.getSimpleName() + " classes in package: " + basePackage);
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotationClass);
        System.out.println("[ClassScanner] Found " + annotatedClasses.size() + " @" + annotationClass.getSimpleName() + " classes");
        return new HashSet<>(annotatedClasses);
    }
}
