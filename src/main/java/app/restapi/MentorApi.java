package app.restapi;

import app.bean.MentorBean;
import app.dtos.MentorRequestDto;
import app.dtos.MentorResponseDto;
import app.model.Mentor;
import app.security.jwt.JwtSecured;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/mentors")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@JwtSecured
public class MentorApi {

    @Inject
    private MentorBean mentorBean;

    /* =========================
       READ OPERATIONS
       ========================= */

    @GET
    public Response listMentors() {
        try {
            List<MentorResponseDto> mentors = mentorBean.findAll()
                    .stream()
                    .map(MentorResponseDto::fromEntity)
                    .toList();

            return JsonApi.ok(mentors);

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/{mentorId}")
    public Response getMentor(@PathParam("mentorId") String mentorId) {
        try {
            Mentor mentor = mentorBean.getById(mentorId);

            if (mentor == null) {
                return JsonApi.notFound("Mentor not found");
            }

            return JsonApi.ok(MentorResponseDto.fromEntity(mentor));

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/specialization/{specialization}")
    public Response getBySpecialization(@PathParam("specialization") String specialization) {
        try {
            List<MentorResponseDto> mentors = mentorBean.findAll()
                    .stream()
                    .filter(m -> m.getSpecialization() != null
                            && m.getSpecialization().equalsIgnoreCase(specialization))
                    .map(MentorResponseDto::fromEntity)
                    .toList();

            return JsonApi.ok(mentors);

        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    /* =========================
       CREATE OPERATIONS
       ========================= */

    /**
     * ADMIN creates mentor (system-managed password generation logic in bean)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMentor(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            Mentor mentor = JsonApi.read(body, MentorRequestDto.class)
                                   .toEntity();

            mentorBean.add(mentor, "ADMIN");

            return JsonApi.created(MentorResponseDto.fromEntity(mentor));

        } catch (IllegalArgumentException e) {
            return JsonApi.badRequest(e.getMessage());

        } catch (Exception e) {
            return JsonApi.badRequest("Failed to create mentor: " + e.getMessage());
        }
    }

    /**
     * SELF registration endpoint (public)
     */
    @POST
    @Path("/register")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerMentor(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            Mentor mentor = JsonApi.read(body, MentorRequestDto.class)
                                   .toEntity();

            mentorBean.add(mentor, "SELF");

            return JsonApi.created(MentorResponseDto.fromEntity(mentor));

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
    @Path("/{mentorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMentor(@PathParam("mentorId") String mentorId, String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            Mentor mentor = JsonApi.read(body, MentorRequestDto.class)
                                   .toEntity();

            mentorBean.update(mentorId, mentor);

            Mentor updated = mentorBean.getById(mentorId);

            return JsonApi.ok(MentorResponseDto.fromEntity(updated));

        } catch (IllegalArgumentException e) {
            return JsonApi.badRequest(e.getMessage());

        } catch (Exception e) {
            return JsonApi.badRequest("Failed to update mentor: " + e.getMessage());
        }
    }

    /* =========================
       DELETE
       ========================= */

    @DELETE
    @Path("/{mentorId}")
    public Response deleteMentor(@PathParam("mentorId") String mentorId) {
        try {
            mentorBean.delete(mentorId);
            return Response.noContent().build();

        } catch (IllegalArgumentException e) {
            return JsonApi.notFound(e.getMessage());

        } catch (Exception e) {
            return JsonApi.badRequest("Failed to delete mentor: " + e.getMessage());
        }
    }
}