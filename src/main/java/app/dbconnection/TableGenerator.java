package app.dbconnection;
import app.framework.DbColumn;
import app.framework.DbTable;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TableGenerator {
    private static final Logger logger = AppLogger.getLogger(TableGenerator.class);

    public static void generateTables(DataSource ds, Set<Class<?>> entityClasses) {
        logger.info("Starting annotation-based table generation...");
        for (Class<?> clazz : entityClasses) {
            if (!clazz.isAnnotationPresent(DbTable.class)) {
                continue;
            }
            DbTable tableAnnotation = clazz.getAnnotation(DbTable.class);
            String tableName = tableAnnotation.name();
            try {
                String sql = generateCreateTableSQL(clazz, tableName);
                execute(ds, sql);
                logger.info("Table processed: {}", tableName);
            } catch (Exception e) {
                logger.error("Error processing table {}: {}", tableName, e.getMessage());
            }
        }
        logger.info("Table generation completed successfully");
    }
    private static String generateCreateTableSQL(Class<?> clazz, String tableName) {
        List<String> columns = new ArrayList<>();
        String primaryKey = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(DbColumn.class)) {
                continue;
            }
            DbColumn columnAnnotation = field.getAnnotation(DbColumn.class);
            StringBuilder columnDef = new StringBuilder();
            columnDef.append(columnAnnotation.name()).append(" ").append(columnAnnotation.type());
            
            // Don't add AUTO_INCREMENT for SERIAL types (PostgreSQL handles this automatically)
            if (columnAnnotation.autoIncrement() && !columnAnnotation.type().toUpperCase().contains("SERIAL")) {
                columnDef.append(" AUTO_INCREMENT");
            }
            
            if (columnAnnotation.notNull()) {
                columnDef.append(" NOT NULL");
            }
            if (columnAnnotation.unique()) {
                columnDef.append(" UNIQUE");
            }
            if (!columnAnnotation.defaultValue().isEmpty()) {
                columnDef.append(" DEFAULT ").append(columnAnnotation.defaultValue());
            }
            columns.add(columnDef.toString());
            if (columnAnnotation.primaryKey()) {
                primaryKey = columnAnnotation.name();
            }
        }
        if (primaryKey != null) {
            columns.add("PRIMARY KEY (" + primaryKey + ")");
        }
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + 
                     " (" + String.join(", ", columns) + ")";
        return sql;
    }
    private static void execute(DataSource ds, String sql) {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            logger.debug("Executed: {}", sql);
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                logger.debug("Table already exists (skipping)");
            } else {
                logger.error("Error executing SQL: {}", e.getMessage());
            }
        }
    }
}
