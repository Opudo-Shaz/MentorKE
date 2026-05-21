package app.api;

import app.bean.MatchRequestBean;
import app.dtos.MatchRequestCreateRequestDto;
import app.dtos.MatchRequestCreatedResponseDto;
import app.dtos.MatchRequestResponseDto;
import app.dtos.MatchRequestUpdateRequestDto;
import app.model.MatchRequest;
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

import java.util.List;

@Path("/match-requests")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Match Requests", description = "Mentor-mentee match request management")
public class MatchRequestApi {

    @Inject
    private MatchRequestBean matchRequestBean;

    @GET
    @Path("/mentee/{menteeId}")
    @Operation(
        summary = "Get requests by mentee",
        description = "Retrieves all match requests submitted by a specific mentee"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of match requests for the mentee",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MatchRequestResponseDto.class))
    )
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response requestsByMentee(
        @Parameter(description = "ID of the mentee", required = true)
        @PathParam("menteeId") String menteeId) {
        try {
            List<MatchRequestResponseDto> requests = matchRequestBean.getRequestsByMentee(menteeId).stream()
                    .map(MatchRequestResponseDto::fromEntity)
                    .toList();
            return JsonApi.ok(requests);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/mentor/{mentorId}")
    @Operation(
        summary = "Get pending requests for mentor",
        description = "Retrieves all pending match requests directed at a specific mentor"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of pending match requests for the mentor",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MatchRequestResponseDto.class))
    )
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response pendingForMentor(
        @Parameter(description = "ID of the mentor", required = true)
        @PathParam("mentorId") String mentorId) {
        try {
            List<MatchRequestResponseDto> requests = matchRequestBean.getPendingRequestsForMentor(mentorId).stream()
                    .map(MatchRequestResponseDto::fromEntity)
                    .toList();
            return JsonApi.ok(requests);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/{requestId}")
    @Operation(
        summary = "Get match request by ID",
        description = "Retrieves a single match request by its ID"
    )
    @APIResponse(
        responseCode = "200",
        description = "Match request found",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MatchRequestResponseDto.class))
    )
    @APIResponse(responseCode = "404", description = "Match request not found")
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response getRequest(
        @Parameter(description = "ID of the match request", required = true)
        @PathParam("requestId") String requestId) {
        try {
            MatchRequest request = matchRequestBean.getMatchRequest(requestId);
            if (request == null) {
                return JsonApi.notFound("Match request not found");
            }
            return JsonApi.ok(MatchRequestResponseDto.fromEntity(request));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Create a match request",
        description = "Submits a new mentor-mentee match request"
    )
    @RequestBody(
        description = "Match request details — menteeId, mentorId, and specialization are all required",
        required = true,
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MatchRequestCreateRequestDto.class))
    )
    @APIResponse(
        responseCode = "201",
        description = "Match request created successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MatchRequestCreatedResponseDto.class))
    )
    @APIResponse(responseCode = "400", description = "Missing or invalid fields")
    public Response createRequest(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            MatchRequestCreateRequestDto request = JsonApi.read(body, MatchRequestCreateRequestDto.class);
            validateCreateRequest(request);
            matchRequestBean.requestMentor(request.getMenteeId(), request.getMentorId(), request.getSpecialization());
            return JsonApi.created(new MatchRequestCreatedResponseDto("Request created successfully"));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{requestId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Approve or reject a match request",
        description = "Updates the status of a match request. Accepted values for `status` are `APPROVED` or `REJECTED`"
    )
    @RequestBody(
        description = "Status update — must be APPROVED or REJECTED",
        required = true,
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MatchRequestUpdateRequestDto.class))
    )
    @APIResponse(
        responseCode = "200",
        description = "Match request updated successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MatchRequestResponseDto.class))
    )
    @APIResponse(responseCode = "400", description = "Invalid status value or missing body")
    public Response updateRequest(
        @Parameter(description = "ID of the match request to update", required = true)
        @PathParam("requestId") String requestId, String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            MatchRequestUpdateRequestDto request = JsonApi.read(body, MatchRequestUpdateRequestDto.class);
            String status = request.getStatus() == null ? "" : request.getStatus().trim().toUpperCase();

            if ("APPROVED".equals(status)) {
                matchRequestBean.approveMentorRequest(requestId);
            } else if ("REJECTED".equals(status)) {
                matchRequestBean.rejectMentorRequest(requestId);
            } else {
                return JsonApi.badRequest("status must be APPROVED or REJECTED");
            }
            return JsonApi.ok(MatchRequestResponseDto.fromEntity(matchRequestBean.getMatchRequest(requestId)));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{requestId}")
    @Operation(
        summary = "Delete a match request",
        description = "Permanently deletes a match request by its ID"
    )
    @APIResponse(responseCode = "204", description = "Match request deleted successfully")
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response deleteRequest(
        @Parameter(description = "ID of the match request to delete", required = true)
        @PathParam("requestId") String requestId) {
        try {
            matchRequestBean.deleteMatchRequest(requestId);
            return Response.noContent().build();
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    private void validateCreateRequest(MatchRequestCreateRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getMenteeId() == null || request.getMenteeId().isBlank()) {
            throw new IllegalArgumentException("menteeId is required");
        }
        if (request.getMentorId() == null || request.getMentorId().isBlank()) {
            throw new IllegalArgumentException("mentorId is required");
        }
        if (request.getSpecialization() == null || request.getSpecialization().isBlank()) {
            throw new IllegalArgumentException("specialization is required");
        }
    }
}