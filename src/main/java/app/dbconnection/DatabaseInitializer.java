package app.dbconnection;

import app.framework.DbTable;
import app.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class DatabaseInitializer {

    private static final Logger logger =
            AppLogger.getLogger(DatabaseInitializer.class);

    @Inject
    private DataSourceHelper dataSourceHelper;

    public void initializeDatabase() throws SQLException {

        logger.info("Starting database initialization...");

        Set<Class<?>> entityClasses = new HashSet<>();

        entityClasses.add(User.class);
        entityClasses.add(Mentor.class);
        entityClasses.add(Mentee.class);
        entityClasses.add(AuditTrail.class);
        entityClasses.add(app.model.Session.class);
        entityClasses.add(app.model.MatchRequest.class);
        entityClasses.add(app.model.Message.class);

        TableGenerator.generateTables(
                dataSourceHelper.getDataSource(),
                entityClasses
        );

        logger.info("Database initialization completed successfully");
    }
}