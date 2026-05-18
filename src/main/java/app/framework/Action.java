package app.framework;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Action {
    String value();

    String label() default "";

    boolean showLink() default true;

    String pageLink() default "list";

    int linkPosition() default 0;

    String icon() default "";

    String requiredRole() default "";
}