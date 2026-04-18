package app;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        System.out.println("\n========== APPLICATION STARTED ==========");
        System.out.println("[AppContextListener] ServletContext initialized");
        System.out.println("[AppContextListener] Application Name: " + context.getServletContextName());
        System.out.println("[AppContextListener] Server Info: " + context.getServerInfo());

        // Initialize UserStore in application scope
        UserStore userStore = new UserStore();
        context.setAttribute("userStore", userStore);
        System.out.println("[AppContextListener] UserStore initialized with " + userStore.getTotalUsers() + " users");

        // Initialize request counter
        context.setAttribute("requestCount", 0);
        context.setAttribute("sessionCount", 0);

        System.out.println("[AppContextListener] Global attributes initialized");
        System.out.println("==========================================\n");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        System.out.println("\n========== APPLICATION SHUTDOWN ==========");
        System.out.println("[AppContextListener] ServletContext destroyed");

        UserStore userStore = (UserStore) context.getAttribute("userStore");
        if (userStore != null) {
            System.out.println("[AppContextListener] Final user count: " + userStore.getTotalUsers());
        }

        Integer requestCount = (Integer) context.getAttribute("requestCount");
        if (requestCount != null) {
            System.out.println("[AppContextListener] Total requests processed: " + requestCount);
        }

        Integer sessionCount = (Integer) context.getAttribute("sessionCount");
        if (sessionCount != null) {
            System.out.println("[AppContextListener] Peak session count: " + sessionCount);
        }

        System.out.println("==========================================\n");
    }
}

