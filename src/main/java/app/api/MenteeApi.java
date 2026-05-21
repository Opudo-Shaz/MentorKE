package app.api;

import app.bean.MenteeBean;
import app.dtos.MenteeRequestDto;
import app.dtos.MenteeResponseDto;
import app.model.Mentee;
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

@Path("/mentees")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Mentees", description = "Mentee management endpoints")
public class MenteeApi {

    @Inject
    private MenteeBean menteeBean;

    @GET
    @Path("/{menteeId}")
    @Operation(
        summary = "Get mentee by ID",
        description = "Retrieves a single mentee by their ID"
    )
    @APIResponse(
        responseCode = "200",
        description = "Mentee found",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MenteeResponseDto.class))
    )
    @APIResponse(responseCode = "404", description = "Mentee not found")
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response getMentee(
        @Parameter(description = "ID of the mentee to retrieve", required = true)
        @PathParam("menteeId") String menteeId) {
        try {
            Mentee mentee = menteeBean.getMenteeById(menteeId);
            if (mentee == null) {
                return JsonApi.notFound("Mentee not found");
            }
            return JsonApi.ok(MenteeResponseDto.fromEntity(mentee));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/mentor/{mentorId}")
    @Operation(
        summary = "Get mentees by mentor",
        description = "Retrieves all mentees assigned to a specific mentor"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of mentees",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MenteeResponseDto.class))
    )
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response menteesByMentor(
        @Parameter(description = "ID of the mentor", required = true)
        @PathParam("mentorId") String mentorId) {
        try {
            List<MenteeResponseDto> mentees = menteeBean.getMenteesByMentorId(mentorId).stream()
                    .map(MenteeResponseDto::fromEntity)
                    .toList();
            return JsonApi.ok(mentees);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Create a mentee",
        description = "Creates a new mentee record"
    )
    @RequestBody(
        description = "Mentee object to create",
        required = true,
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MenteeRequestDto.class))
    )
    @APIResponse(
        responseCode = "201",
        description = "Mentee created successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MenteeResponseDto.class))
    )
    @APIResponse(responseCode = "400", description = "Invalid input or missing body")
    public Response createMentee(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            Mentee mentee = JsonApi.read(body, MenteeRequestDto.class).toEntity();
            menteeBean.addMenteeAdmin(mentee);
            return JsonApi.created(MenteeResponseDto.fromEntity(mentee));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{menteeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Update a mentee",
        description = "Updates an existing mentee by their ID"
    )
    @RequestBody(
        description = "Updated mentee object",
        required = true,
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MenteeRequestDto.class))
    )
    @APIResponse(
        responseCode = "200",
        description = "Mentee updated successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = MenteeResponseDto.class))
    )
    @APIResponse(responseCode = "400", description = "Invalid input or missing body")
    public Response updateMentee(
        @Parameter(description = "ID of the mentee to update", required = true)
        @PathParam("menteeId") String menteeId, String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            Mentee mentee = JsonApi.read(body, MenteeRequestDto.class).toEntity();
            menteeBean.updateMentee(menteeId, mentee);
            return JsonApi.ok(MenteeResponseDto.fromEntity(menteeBean.getMenteeById(menteeId)));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{menteeId}")
    @Operation(
        summary = "Delete a mentee",
        description = "Deletes a mentee record by their ID"
    )
    @APIResponse(responseCode = "204", description = "Mentee deleted successfully")
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response deleteMentee(
        @Parameter(description = "ID of the mentee to delete", required = true)
        @PathParam("menteeId") String menteeId) {
        try {
            menteeBean.deleteMentee(menteeId);
            return Response.noContent().build();
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }
}