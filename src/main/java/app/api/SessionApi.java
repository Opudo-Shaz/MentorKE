package app.api;

import app.bean.SessionBean;
import app.model.Session;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.time.LocalDateTime;

@Path("/sessions")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class SessionApi {

    @Inject
    private SessionBean sessionBean;

    @GET
    @Path("/upcoming/{userId}")
    public Response upcoming(@PathParam("userId") String userId) {
        try {
            List<Session> sessions = sessionBean.getUpcomingSessions(userId);
            return JsonApi.ok(sessions);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/completed/{userId}")
    public Response completed(@PathParam("userId") String userId) {
        try {
            List<Session> sessions = sessionBean.getCompletedSessions(userId);
            return JsonApi.ok(sessions);
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

            return JsonApi.ok(session);
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

            SessionCreateRequest request = JsonApi.read(body, SessionCreateRequest.class);
            validateSessionCreateRequest(request);

            String sessionId = sessionBean.scheduleSession(
                    request.mentorId(),
                    request.menteeId(),
                    LocalDateTime.parse(request.scheduledDate()),
                    request.durationMinutes(),
                    request.topic());

            return JsonApi.created(new SessionCreatedResponse(sessionId));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{sessionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSession(@PathParam("sessionId") String sessionId, String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            SessionUpdateRequest request = JsonApi.read(body, SessionUpdateRequest.class);
            if (request.status() != null && !request.status().isBlank()) {
                sessionBean.updateSessionStatus(sessionId, request.status());
            }
            if (request.notes() != null && !request.notes().isBlank()) {
                sessionBean.addSessionNotes(sessionId, request.notes());
            }

            return JsonApi.ok(sessionBean.getSession(sessionId));
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

    private void validateSessionCreateRequest(SessionCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.mentorId() == null || request.mentorId().isBlank()) {
            throw new IllegalArgumentException("mentorId is required");
        }
        if (request.menteeId() == null || request.menteeId().isBlank()) {
            throw new IllegalArgumentException("menteeId is required");
        }
        if (request.scheduledDate() == null || request.scheduledDate().isBlank()) {
            throw new IllegalArgumentException("scheduledDate is required");
        }
        if (request.durationMinutes() == null) {
            throw new IllegalArgumentException("durationMinutes is required");
        }
    }

    public record SessionCreateRequest(String mentorId, String menteeId,
                                       String scheduledDate,
                                       Integer durationMinutes, String topic) {
    }

    public record SessionUpdateRequest(String status, String notes) {
    }

    public record SessionCreatedResponse(String sessionId) {
    }
}