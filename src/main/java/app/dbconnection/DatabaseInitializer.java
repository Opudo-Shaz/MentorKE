package app.dbconnection;

import app.model.*;
import app.framework.DbTable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class DatabaseInitializer {

    @Inject
    private DataSourceHelper dataSourceHelper;

    public void initializeDatabase() throws SQLException {

        System.out.println("[DatabaseInitializer] Starting database initialization...");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found", e);
        }

        createDatabaseIfNotExists();

        // Generate tables from entity classes
        Set<Class<?>> entityClasses = new HashSet<>();
        entityClasses.add(User.class);
        entityClasses.add(Mentor.class);
        entityClasses.add(Mentee.class);
        entityClasses.add(AuditTrail.class);
        
        TableGenerator.generateTables(dataSourceHelper.getDataSource(), entityClasses);

        System.out.println("[DatabaseInitializer] Database initialization completed successfully");
    }

    private void createDatabaseIfNotExists() throws SQLException {

        String url = "jdbc:postgresql://" +
                dataSourceHelper.getHost() + ":" +
                dataSourceHelper.getPort() + "/postgres";

        try (Connection conn = DriverManager.getConnection(
                url,
                dataSourceHelper.getUser(),
                dataSourceHelper.getPassword());
             Statement stmt = conn.createStatement()) {

            String checkDbQuery =
                    "SELECT 1 FROM pg_database WHERE datname = '" + dataSourceHelper.getDbName() + "'";

            var result = stmt.executeQuery(checkDbQuery);

            if (result.next()) {
                System.out.println("[DatabaseInitializer] Database already exists");
                return;
            }

            String createDbQuery =
                    "CREATE DATABASE " + dataSourceHelper.getDbName();

            stmt.executeUpdate(createDbQuery);

            System.out.println("[DatabaseInitializer] Database created successfully");
        }
    }
}