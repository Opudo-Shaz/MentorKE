package app.filter;

import app.utility.logging.AppLogger;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import java.io.IOException;

@WebFilter(
    filterName = "LoggingFilter",
    urlPatterns = {"/*"}
)
public class LoggingFilter implements Filter {

    private static final Logger logger = AppLogger.getLogger(LoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("[LoggingFilter] Filter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();
        String method = httpRequest.getMethod();
        String path = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String remoteIP = httpRequest.getRemoteAddr();

        String fullPath = queryString != null ? path + "?" + queryString : path;
        logger.debug("[LoggingFilter] [REQUEST] {} {} from {}", method, fullPath, remoteIP);

        try {
            // Continue with the request
            chain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            int statusCode = httpResponse.getStatus();
            logger.info("[LoggingFilter] [RESPONSE] {} {} - Status: {} - Duration: {}ms", method, path, statusCode, duration);
        }
    }

    @Override
    public void destroy() {
        logger.debug("[LoggingFilter] Filter destroyed");
    }
}

