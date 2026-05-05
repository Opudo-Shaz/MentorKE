package app.framework;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageMenuItem {
    String label();
    String url();
    String icon() default "";
    int order() default 100;
    boolean visible() default true;
    String requiredRole() default "";
}
