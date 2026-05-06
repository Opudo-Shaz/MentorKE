package app.listener;

import app.dbconnection.DatabaseInitializer;
import app.dbconnection.DataSourceHelper;
import app.utility.logging.AppLogger;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = AppLogger.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        logger.info("\n========================================");
        logger.info("[AppContextListener] Application Starting");
        logger.info("========================================\n");

        try {
            DatabaseInitializer initializer =
                    CDI.current().select(DatabaseInitializer.class).get();

            initializer.initializeDatabase();

            logger.info("[AppContextListener] Database initialized successfully");
            logger.info("[AppContextListener] Application ready\n");

        } catch (Exception e) {
            logger.error("[AppContextListener] CRITICAL ERROR initializing DB", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        logger.info("\n========================================");
        logger.info("[AppContextListener] Application Stopping");
        logger.info("========================================\n");

        try {
            DataSourceHelper helper =
                    CDI.current().select(DataSourceHelper.class).get();

            helper.close();

            logger.info("[AppContextListener] DataSource closed");

        } catch (Exception e) {
            logger.error("[AppContextListener] Error closing DataSource: " + e.getMessage(), e);
        }
    }
}