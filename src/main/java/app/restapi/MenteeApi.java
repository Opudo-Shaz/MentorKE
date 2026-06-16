package app.restapi;

import app.bean.MenteeBean;
import app.dtos.MenteeRequestDto;
import app.dtos.MenteeResponseDto;
import app.model.Mentee;
import app.security.jwt.JwtSecured;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/mentees")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@JwtSecured
public class MenteeApi {

    @Inject
    private MenteeBean menteeBean;

    /* =========================
       READ OPERATIONS
       ========================= */

    @GET
    public Response getAllMentees() {
        try {
            List<MenteeResponseDto> mentees = menteeBean.findAll()
                    .stream()
                    .map(MenteeResponseDto::fromEntity)
                    .toList();

            return JsonApi.ok(mentees);

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/{menteeId}")
    public Response getMentee(@PathParam("menteeId") String menteeId) {
        try {
            Mentee mentee = menteeBean.getById(menteeId);

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
    public Response getByMentor(@PathParam("mentorId") String mentorId) {
        try {
            List<MenteeResponseDto> mentees = menteeBean.findByMentorId(mentorId)
                    .stream()
                    .map(MenteeResponseDto::fromEntity)
                    .toList();

            return JsonApi.ok(mentees);

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    /* =========================
       CREATE OPERATIONS
       ========================= */

    /**
     * ADMIN creates mentee
     * (password handled by bean logic)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMentee(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            Mentee mentee = JsonApi.read(body, MenteeRequestDto.class)
                                   .toEntity();

            menteeBean.add(mentee, "ADMIN");

            return JsonApi.created(MenteeResponseDto.fromEntity(mentee));

        } catch (IllegalArgumentException e) {
            return JsonApi.badRequest(e.getMessage());

        } catch (Exception e) {
            return JsonApi.badRequest("Failed to create mentee: " + e.getMessage());
        }
    }

    /**
     * SELF registration endpoint (public)
     */
    @POST
    @Path("/register")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerMentee(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            Mentee mentee = JsonApi.read(body, MenteeRequestDto.class)
                                   .toEntity();

            menteeBean.add(mentee, "SELF");

            return JsonApi.created(MenteeResponseDto.fromEntity(mentee));

        } catch (IllegalArgumentException e) {
            return JsonApi.badRequest(e.getMessage());

        } catch (Exception e) {
            return JsonApi.badRequest("Registration failed: " + e.getMessage());
        }
    }

    /* =========================
       UPDATE
       ========================= */

    @PUT
    @Path("/{menteeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMentee(@PathParam("menteeId") String menteeId, String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            Mentee mentee = JsonApi.read(body, MenteeRequestDto.class)
                                   .toEntity();

            menteeBean.update(menteeId, mentee);

            Mentee updated = menteeBean.getById(menteeId);

            return JsonApi.ok(MenteeResponseDto.fromEntity(updated));

        } catch (IllegalArgumentException e) {
            return JsonApi.badRequest(e.getMessage());

        } catch (Exception e) {
            return JsonApi.badRequest("Failed to update mentee: " + e.getMessage());
        }
    }

    /* =========================
       DELETE
       ========================= */

    @DELETE
    @Path("/{menteeId}")
    public Response deleteMentee(@PathParam("menteeId") String menteeId) {
        try {
            menteeBean.delete(menteeId);
            return Response.noContent().build();

        } catch (IllegalArgumentException e) {
            return JsonApi.notFound(e.getMessage());

        } catch (Exception e) {
            return JsonApi.badRequest("Failed to delete mentee: " + e.getMessage());
        }
    }
}