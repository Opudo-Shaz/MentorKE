package app.security.jwt;

import app.utility.logging.AppLogger;
import io.jsonwebtoken.Claims;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;

@Provider
@JwtSecured
public class JwtFilter implements ContainerRequestFilter {

    private static final Logger logger = AppLogger.getLogger(JwtFilter.class);
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    @Inject
    private JwtUtil jwtUtil;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String authHeader = ctx.getHeaderString(AUTHORIZATION);

        // 1. Check header exists
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            logger.warn("[JwtFilter] Missing or invalid Authorization header");
            ctx.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Missing or invalid Authorization header\"}")
                    .build());
            return;
        }

        // 2. Extract token
        String token = authHeader.substring(BEARER.length());

        // 3. Validate token
        if (!jwtUtil.isTokenValid(token)) {
            logger.warn("[JwtFilter] Token validation failed");
            ctx.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Invalid or expired token\"}")
                    .build());
            return;
        }

        // 4. Inject claims into request context for use in endpoints
        Claims claims = jwtUtil.validateToken(token);
        ctx.setProperty("username", claims.getSubject());
        ctx.setProperty("role", claims.get("role", String.class));

        logger.debug("[JwtFilter] Authenticated: {} role: {}",
                claims.getSubject(), claims.get("role"));
    }
}
