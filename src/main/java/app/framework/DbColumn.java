package app.framework;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbColumn {
    String name();
    String type();
    boolean primaryKey() default false;
    boolean autoIncrement() default false;
    boolean notNull() default false;
    boolean unique() default false;
    String defaultValue() default "";
}
