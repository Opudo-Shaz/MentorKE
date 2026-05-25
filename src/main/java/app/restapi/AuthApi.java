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

            // 4. Verify bcrypt password
            BCrypt.Result result = BCrypt.verifyer()
                    .verify(request.getPassword().toCharArray(),
                            user.getPassword());

            if (!result.verified) {
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
}
