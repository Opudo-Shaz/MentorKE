package app.restapi;

import app.bean.SessionBean;
import app.dtos.SessionCreateRequestDto;
import app.dtos.SessionResponseDto;
import app.model.Session;
import app.security.jwt.JwtSecured;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/sessions")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@JwtSecured
public class SessionApi {

    @Inject
    private SessionBean sessionBean;

    @GET
    public Response getAllSessions() {
        try {
            return JsonApi.ok(new Object() {
                public static final String message = "Use user-specific endpoints: /sessions/user/{userId}";
            });
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/{sessionId}")
    public Response getSession(@PathParam("sessionId") String sessionId) {
        try {
            Session session = sessionBean.getSession(sessionId);

            if (session == null) {
                return JsonApi.notFound("Session not found");
            }

            return JsonApi.ok(SessionResponseDto.fromEntity(session));

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/user/{userId}/upcoming")
    public Response getUpcomingSessions(@PathParam("userId") String userId) {
        try {
            List<SessionResponseDto> sessions =
                    sessionBean.getUpcomingSessions(userId)
                            .stream()
                            .map(SessionResponseDto::fromEntity)
                            .toList();

            return JsonApi.ok(sessions);

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/user/{userId}/completed")
    public Response getCompletedSessions(@PathParam("userId") String userId) {
        try {
            List<SessionResponseDto> sessions =
                    sessionBean.getCompletedSessions(userId)
                            .stream()
                            .map(SessionResponseDto::fromEntity)
                            .toList();

            return JsonApi.ok(sessions);

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSession(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            SessionCreateRequestDto dto =
                    JsonApi.read(body, SessionCreateRequestDto.class);

            // Convert DTO → Entity (handles LocalDateTime parsing inside DTO)
            Session session = dto.toEntity();

            String sessionId = sessionBean.scheduleSession(
                    session.getMentorId(),
                    session.getMenteeId(),
                    session.getScheduledDate(),
                    session.getDurationMinutes(),
                    session.getTopic()
            );

            return JsonApi.created(new Object() {
                public final String id = sessionId;
            });

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{sessionId}/cancel")
    public Response cancelSession(@PathParam("sessionId") String sessionId) {
        try {
            sessionBean.cancelSession(sessionId);

            return JsonApi.ok(new Object() {
                public final String status = "cancelled";
            });

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{sessionId}")
    public Response deleteSession(@PathParam("sessionId") String sessionId) {
        try {
            sessionBean.cancelSession(sessionId);
            return Response.noContent().build();

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }
}