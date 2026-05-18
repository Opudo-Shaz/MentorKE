package app.framework;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MentorKeField {
    String label();
    String name()        default "";
    String placeholder() default "";
    String type()        default "text";
    String select()      default "";
}