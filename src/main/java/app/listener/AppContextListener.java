package app.listener;

import app.dbconnection.DatabaseInitializer;
import app.dbconnection.DataSourceHelper;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println("\n========================================");
        System.out.println("[AppContextListener] Application Starting");
        System.out.println("========================================\n");

        try {
            DatabaseInitializer initializer =
                    CDI.current().select(DatabaseInitializer.class).get();

            initializer.initializeDatabase();

            System.out.println("[AppContextListener] Database initialized successfully");
            System.out.println("[AppContextListener] Application ready\n");

        } catch (Exception e) {
            System.err.println("[AppContextListener] CRITICAL ERROR initializing DB");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        System.out.println("\n========================================");
        System.out.println("[AppContextListener] Application Stopping");
        System.out.println("========================================\n");

        try {
            DataSourceHelper helper =
                    CDI.current().select(DataSourceHelper.class).get();

            helper.close();

            System.out.println("[AppContextListener] DataSource closed");

        } catch (Exception e) {
            System.err.println("[AppContextListener] Error closing DataSource: " + e.getMessage());
        }
    }
}