package app.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class RequestTrackingListener implements ServletRequestListener {

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
        System.out.println("[RequestTrackingListener] Request #" + requestCount + " | " + method + " " + fullPath + " | From: " + remoteAddr);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        String method = request.getMethod();
        String path = request.getRequestURI();

        System.out.println("[RequestTrackingListener] Request completed | " + method + " " + path);
    }
}

