package app.filter;

import jakarta.servlet.*;

import java.io.IOException;

public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof jakarta.servlet.http.HttpServletRequest httpRequest) {
            String path = httpRequest.getPathInfo();
            if (path == null || path.isEmpty()) {
                path = httpRequest.getRequestURI();
            }

            if (path != null && (path.contains("/admin") || path.contains("/mentor") || path.contains("/mentee"))) {
                jakarta.servlet.http.HttpSession session = httpRequest.getSession(false);
                if (session == null || !Boolean.TRUE.equals(session.getAttribute("isLoggedIn"))) {
                    ((jakarta.servlet.http.HttpServletResponse) response).sendRedirect("login");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }
}
