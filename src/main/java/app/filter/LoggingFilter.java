package app;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(
    filterName = "LoggingFilter",
    urlPatterns = {"/*"}  // Apply to all requests
)
public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[LoggingFilter] Filter initialized");
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
        System.out.println("[LoggingFilter] [REQUEST] " + method + " " + fullPath + " from " + remoteIP);

        try {
            // Continue with the request
            chain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            int statusCode = httpResponse.getStatus();
            System.out.println("[LoggingFilter] [RESPONSE] " + method + " " + path + " - Status: " + statusCode + " - Duration: " + duration + "ms");
        }
    }

    @Override
    public void destroy() {
        System.out.println("[LoggingFilter] Filter destroyed");
    }
}

