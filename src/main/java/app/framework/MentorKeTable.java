package app.framework;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MentorKeTable {
    String label();
    String addLink()    default "";
    String editLink()   default "";
    String deleteLink() default "";
}