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

    // Must be at least 32 chars for HS256
    private static final String SECRET = "MentorKE-Super-Secret-Key-2026!!";
    private static final long EXPIRY_MS = 24 * 60 * 60 * 1000L; // 24 hours

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRY_MS);

        String token = Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getKey())
                .compact();

        logger.info("[JwtUtil] Token generated for: {} with role: {}", username, role);
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

    public long getExpiresIn() {
        return EXPIRY_MS / 1000;
    }
}