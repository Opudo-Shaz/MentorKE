package app.utility.helper;
import app.framework.DbTable;
import app.framework.PageMenuItem;
import app.utility.logging.AppLogger;
import org.reflections.Reflections;
import org.slf4j.Logger;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;


public class ClassScanner {
    private static final Logger logger = AppLogger.getLogger(ClassScanner.class);

    public static Set<Class<?>> scanForDbTables(String basePackage) {
        logger.info("[ClassScanner] Scanning for @DbTable classes in package: {}", basePackage);
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(DbTable.class);
        logger.info("[ClassScanner] Found {} @DbTable classes", annotatedClasses.size());
        for (Class<?> clazz : annotatedClasses) {
            logger.debug("[ClassScanner]   - {}", clazz.getName());
        }
        return new HashSet<>(annotatedClasses);
    }
    public static Set<Class<?>> scanForMenuItem(String basePackage) {
        logger.info("[ClassScanner] Scanning for @PageMenuItem classes in package: {}", basePackage);
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(PageMenuItem.class);
        logger.info("[ClassScanner] Found {} @PageMenuItem classes", annotatedClasses.size());
        for (Class<?> clazz : annotatedClasses) {
            logger.debug("[ClassScanner]   - {}", clazz.getName());
        }
        return new HashSet<>(annotatedClasses);
    }
    public static Set<Class<?>> scanForAnnotation(String basePackage, Class<? extends Annotation> annotationClass) {
        logger.info("[ClassScanner] Scanning for @{} classes in package: {}", annotationClass.getSimpleName(), basePackage);
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotationClass);
        logger.info("[ClassScanner] Found {} @{} classes", annotatedClasses.size(), annotationClass.getSimpleName());
        return new HashSet<>(annotatedClasses);
    }
}
