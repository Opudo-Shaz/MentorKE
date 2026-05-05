package app.dbconnection;
import jakarta.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
public @interface DatabaseQualifier {
    DatabaseType value();
    enum DatabaseType {
        PRIMARY("Primary database connection"),
        READ_REPLICA("Read-only replica connection"),
        CACHE("Cache database connection"),
        BACKUP("Backup database connection");
        private final String description;
        DatabaseType(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }
    }
}
