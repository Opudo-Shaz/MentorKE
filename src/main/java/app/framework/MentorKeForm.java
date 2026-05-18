package app.framework;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MentorKeForm {
    String label();
    String method() default "post";
    String actionUrl();
}