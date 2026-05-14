package app.listener;

import app.utility.logging.AppLogger;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger =
            AppLogger.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        logger.info("\n========================================");
        logger.info("[AppContextListener] Application Starting");
        logger.info("========================================\n");

        try {

            // Database initialization is now handled by Hibernate/JPA
            logger.info("[AppContextListener] Hibernate/JPA will handle database initialization");

            logger.info("[AppContextListener] Database initialized successfully");
            logger.info("[AppContextListener] Application ready\n");

        } catch (Exception e) {

            logger.error(
                    "[AppContextListener] CRITICAL ERROR initializing DB",
                    e
            );

            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        logger.info("\n========================================");
        logger.info("[AppContextListener] Application Stopping");
        logger.info("========================================\n");

        logger.info("[AppContextListener] Shutdown complete");
    }
}