package app.api;

import app.bean.MentorBean;
import app.model.Mentor;
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

@Path("/mentors")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Mentors", description = "Mentor management endpoints")
public class MentorApi {

    @Inject
    private MentorBean mentorBean;

    @GET
    @Operation(
        summary = "List all mentors",
        description = "Retrieves a list of all registered mentors"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of mentors",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Mentor.class))
    )
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response listMentors() {
        try {
            List<Mentor> mentors = mentorBean.getAllMentors();
            return JsonApi.ok(mentors);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/{mentorId}")
    @Operation(
        summary = "Get mentor by ID",
        description = "Retrieves a single mentor by their ID"
    )
    @APIResponse(
        responseCode = "200",
        description = "Mentor found",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Mentor.class))
    )
    @APIResponse(responseCode = "404", description = "Mentor not found")
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response getMentor(
        @Parameter(description = "ID of the mentor to retrieve", required = true)
        @PathParam("mentorId") String mentorId) {
        try {
            Mentor mentor = mentorBean.getMentorById(mentorId);
            if (mentor == null) {
                return JsonApi.notFound("Mentor not found");
            }
            return JsonApi.ok(mentor);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/specialization/{specialization}")
    @Operation(
        summary = "Get mentors by specialization",
        description = "Retrieves all mentors matching the given specialization (case-insensitive)"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of mentors with matching specialization",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Mentor.class))
    )
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response mentorsBySpecialization(
        @Parameter(description = "Specialization to filter by e.g. 'Software Engineering'", required = true)
        @PathParam("specialization") String specialization) {
        try {
            List<Mentor> mentors = mentorBean.getAllMentors().stream()
                    .filter(mentor -> mentor.getSpecialization() != null
                            && mentor.getSpecialization().equalsIgnoreCase(specialization))
                    .toList();
            return JsonApi.ok(mentors);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Create a mentor",
        description = "Creates a new mentor record"
    )
    @RequestBody(
        description = "Mentor object to create",
        required = true,
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Mentor.class))
    )
    @APIResponse(
        responseCode = "201",
        description = "Mentor created successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Mentor.class))
    )
    @APIResponse(responseCode = "400", description = "Invalid input or missing body")
    public Response createMentor(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            Mentor mentor = JsonApi.read(body, Mentor.class);
            mentorBean.addMentorAdmin(mentor);
            return JsonApi.created(mentor);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{mentorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Update a mentor",
        description = "Updates an existing mentor by their ID"
    )
    @RequestBody(
        description = "Updated mentor object",
        required = true,
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Mentor.class))
    )
    @APIResponse(
        responseCode = "200",
        description = "Mentor updated successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Mentor.class))
    )
    @APIResponse(responseCode = "400", description = "Invalid input or missing body")
    public Response updateMentor(
        @Parameter(description = "ID of the mentor to update", required = true)
        @PathParam("mentorId") String mentorId, String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            Mentor mentor = JsonApi.read(body, Mentor.class);
            mentorBean.updateMentor(mentorId, mentor);
            return JsonApi.ok(mentorBean.getMentorById(mentorId));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{mentorId}")
    @Operation(
        summary = "Delete a mentor",
        description = "Deletes a mentor record by their ID"
    )
    @APIResponse(responseCode = "204", description = "Mentor deleted successfully")
    @APIResponse(responseCode = "400", description = "Unexpected error")
    public Response deleteMentor(
        @Parameter(description = "ID of the mentor to delete", required = true)
        @PathParam("mentorId") String mentorId) {
        try {
            mentorBean.deleteMentor(mentorId);
            return Response.noContent().build();
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }
}