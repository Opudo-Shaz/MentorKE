package app.framework;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Action {
    String value();

    String label() default "";

    String pageLink() default "list";

    String icon() default "";

    String requiredRole() default "";
}