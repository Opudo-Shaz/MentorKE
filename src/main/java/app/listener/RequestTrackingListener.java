package app.listener;

import app.utility.logging.AppLogger;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;

@WebListener
public class RequestTrackingListener implements ServletRequestListener {

    private static final Logger logger = AppLogger.getLogger(RequestTrackingListener.class);

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletContext context = sre.getServletContext();
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();

        // Increment request count
        Integer requestCount = (Integer) context.getAttribute("requestCount");
        if (requestCount == null) {
            requestCount = 0;
        }
        requestCount++;
        context.setAttribute("requestCount", requestCount);

        // Track the current request info
        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String remoteAddr = request.getRemoteAddr();

        String fullPath = query != null ? path + "?" + query : path;
        logger.info("[RequestTrackingListener] Request #{} | {} {} | From: {}", requestCount, method, fullPath, remoteAddr);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        String method = request.getMethod();
        String path = request.getRequestURI();

        logger.info("[RequestTrackingListener] Request completed | {} {}", method, path);
    }
}

