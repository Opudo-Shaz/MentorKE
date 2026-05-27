package app.restapi;

import app.bean.UserBean;
import app.dtos.LoginRequestDto;
import app.dtos.LoginResponseDto;
import app.model.User;
import app.security.jwt.JwtUtil;
import app.utility.logging.AppLogger;
import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;

@Path("/auth")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class AuthApi {

    private static final Logger logger = AppLogger.getLogger(AuthApi.class);

    @EJB
    private UserBean userBean;

    @Inject
    private JwtUtil jwtUtil;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            LoginRequestDto request = JsonApi.read(body, LoginRequestDto.class);

            // 1. Validate input
            if (request.getUsername() == null || request.getUsername().isBlank()) {
                return JsonApi.badRequest("Username is required");
            }
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                return JsonApi.badRequest("Password is required");
            }

            // 2. Lookup user from DB
            User user = userBean.getUserByUsername(request.getUsername());
            if (user == null) {
                logger.warn(" User not found: {}", request.getUsername());
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Invalid username or password\"}")
                        .build();
            }

            // 3. Check account is active
            if (!"active".equalsIgnoreCase(user.getStatus())) {
                logger.warn(" Inactive account: {}", request.getUsername());
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Account is inactive\"}")
                        .build();
            }

            // 4. Verify password (bcrypt or plain text for backward compatibility)
            boolean passwordValid = false;
            
            // Try bcrypt first (new format)
            if (user.getPassword() != null && user.getPassword().startsWith("$2")) {
                BCrypt.Result result = BCrypt.verifyer()
                        .verify(request.getPassword().toCharArray(),
                                user.getPassword());
                passwordValid = result.verified;
            } else {
                // Fallback to plain text comparison (existing data)
                passwordValid = request.getPassword().equals(user.getPassword());
            }

            if (!passwordValid) {
                logger.warn("[AuthApi] Wrong password for: {}", request.getUsername());
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Invalid username or password\"}")
                        .build();
            }

            // 5. Generate JWT
            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getRole().toLowerCase()
            );

            logger.info(" Login successful for: {}", user.getUsername());

            return JsonApi.ok(new LoginResponseDto(
                    true,
                    "Login successful",
                    token,
                    user.getUsername(),
                    user.getRole().toLowerCase(),
                    jwtUtil.getExpiresIn()
            ));

        } catch (Exception e) {
            logger.error(" Login error", e);
            return JsonApi.badRequest(e.getMessage());
        }
    }

    /**
     * DEBUG: Check if user exists and password format
     * GET /api/auth/debug/{username}
     */
    @GET
    @Path("/debug/{username}")
    public Response debugUser(@PathParam("username") String username) {
        try {
            User user = userBean.getUserByUsername(username);
            if (user == null) {
                return JsonApi.ok(new Object() {
                    public String status = "User not found";
                    public String username_param = username;
                });
            }

            return JsonApi.ok(new Object() {
                public String status = "User found";
                public String username = user.getUsername();
                public String role = user.getRole();
                public String account_status = user.getStatus();
                public String password_hash_start = user.getPassword() != null ? 
                    user.getPassword().substring(0, Math.min(20, user.getPassword().length())) : "null";
                public String is_bcrypt_hash = user.getPassword() != null && user.getPassword().startsWith("$2") ? "YES" : "NO";
            });
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }
}
