package app.api;

import app.bean.SessionBean;
import app.model.Session;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;

@Path("/sessions")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Sessions", description = "Mentoring session scheduling and management")
public class SessionApi {

    @Inject
    private SessionBean sessionBean;

    @GET
    @Path("/upcoming/{userId}")
    @Operation(
        summary = "Get upcoming sessions",
        description = "Retrieves all upcoming scheduled sessions for a user (mentor or mentee)"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of upcoming sessions",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Session.class))
    )
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response upcoming(
        @Parameter(description = "ID of the mentor or mentee", required = true)
        @PathParam("userId") String userId) {
        try {
            List<Session> sessions = sessionBean.getUpcomingSessions(userId);
            return JsonApi.ok(sessions);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/completed/{userId}")
    @Operation(
        summary = "Get completed sessions",
        description = "Retrieves all completed sessions for a user (mentor or mentee)"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of completed sessions",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Session.class))
    )
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response completed(
        @Parameter(description = "ID of the mentor or mentee", required = true)
        @PathParam("userId") String userId) {
        try {
            List<Session> sessions = sessionBean.getCompletedSessions(userId);
            return JsonApi.ok(sessions);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/{sessionId}")
    @Operation(
        summary = "Get session by ID",
        description = "Retrieves a single session by its ID"
    )
    @APIResponse(
        responseCode = "200",
        description = "Session found",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Session.class))
    )
    @APIResponse(responseCode = "404", description = "Session not found")
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response getSession(
        @Parameter(description = "ID of the session to retrieve", required = true)
        @PathParam("sessionId") String sessionId) {
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
    @Operation(
        summary = "Schedule a session",
        description = "Creates a new mentoring session. `scheduledDate` must be an ISO-8601 datetime e.g. `2025-06-01T10:00:00`"
    )
    @RequestBody(
        description = "Session details — mentorId, menteeId, scheduledDate, and durationMinutes are required",
        required = true,
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = SessionCreateRequest.class))
    )
    @APIResponse(
        responseCode = "201",
        description = "Session scheduled successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = SessionCreatedResponse.class))
    )
    @APIResponse(responseCode = "400", description = "Missing or invalid fields")
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
    @Operation(
        summary = "Update a session",
        description = "Updates the status and/or notes of an existing session. Both fields are optional — only non-blank values are applied"
    )
    @RequestBody(
        description = "Fields to update — provide status, notes, or both",
        required = true,
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = SessionUpdateRequest.class))
    )
    @APIResponse(
        responseCode = "200",
        description = "Session updated successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Session.class))
    )
    @APIResponse(responseCode = "400", description = "Invalid input or missing body")
    public Response updateSession(
        @Parameter(description = "ID of the session to update", required = true)
        @PathParam("sessionId") String sessionId, String body) {
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
    @Operation(
        summary = "Cancel a session",
        description = "Cancels and deletes a session by its ID"
    )
    @APIResponse(responseCode = "204", description = "Session cancelled successfully")
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response deleteSession(
        @Parameter(description = "ID of the session to cancel", required = true)
        @PathParam("sessionId") String sessionId) {
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

    @Schema(description = "Payload for scheduling a new session")
    public record SessionCreateRequest(
        @Schema(description = "ID of the mentor", required = true, example = "7")
        String mentorId,

        @Schema(description = "ID of the mentee", required = true, example = "42")
        String menteeId,

        @Schema(description = "ISO-8601 datetime for the session", required = true,
                example = "2025-06-01T10:00:00")
        String scheduledDate,

        @Schema(description = "Duration of the session in minutes", required = true,
                example = "60")
        Integer durationMinutes,

        @Schema(description = "Topic to be covered in the session",
                example = "Introduction to System Design")
        String topic
    ) {}

    @Schema(description = "Payload for updating an existing session")
    public record SessionUpdateRequest(
        @Schema(description = "New session status",
                example = "COMPLETED",
                enumeration = {"PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"})
        String status,

        @Schema(description = "Notes or feedback from the session",
                example = "Covered SOLID principles and reviewed code structure")
        String notes
    ) {}

    @Schema(description = "Response returned after a session is scheduled")
    public record SessionCreatedResponse(
        @Schema(description = "ID of the newly created session", example = "101")
        String sessionId
    ) {}
}