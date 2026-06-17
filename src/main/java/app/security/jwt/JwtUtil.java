package app.security.jwt;

import app.utility.logging.AppLogger;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@ApplicationScoped
public class JwtUtil {

    private static final Logger logger = AppLogger.getLogger(JwtUtil.class);

    private static final String SECRET    = "MentorKE-Super-Secret-Key-2026!!";
    private static final long   EXPIRY_MS = 24 * 60 * 60 * 1000L;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // Original — kept for backward compat (subject only, no userId claim)
    public String generateToken(String username, String role) {
        return generateToken(username, role, null);
    }

    // Preferred — includes userId claim
    public String generateToken(String username, String role, Long userId) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + EXPIRY_MS);

        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry);

        if (userId != null) {
            builder.claim("userId", userId.toString());
        }

        String token = builder.signWith(getKey()).compact();
        logger.info("[JwtUtil] Token generated for: {} role: {} userId: {}", username, role, userId);
        return token;
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return validateToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return validateToken(token).get("role", String.class);
    }

    // Extract userId stored as a claim
    public Long getUserIdFromToken(String token) {
        try {
            String userId = validateToken(token).get("userId", String.class);
            return userId != null ? Long.parseLong(userId) : null;
        } catch (Exception e) {
            logger.warn("[JwtUtil] Could not extract userId from token: {}", e.getMessage());
            return null;
        }
    }

    // Convenience — strips "Bearer " prefix automatically
    public Long extractUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return getUserIdFromToken(authHeader.substring(7));
    }

    public long getExpiresIn() {
        return EXPIRY_MS / 1000;
    }
}