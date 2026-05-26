package app.restapi;

import app.bean.MentorBean;
import app.dtos.MentorRequestDto;
import app.dtos.MentorResponseDto;
import app.model.Mentor;
import app.security.jwt.JwtSecured;
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

    @GET
    public Response listMentors() {
        try {
            List<MentorResponseDto> mentors = mentorBean.findAll().stream()
                    .map(MentorResponseDto::fromEntity)
                    .toList();
            return JsonApi.ok(mentors);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/{mentorId}")
    public Response getMentor(
        @PathParam("mentorId") String mentorId) {
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
    public Response mentorsBySpecialization(
        @PathParam("specialization") String specialization) {
        try {
            List<MentorResponseDto> mentors = mentorBean.findAll().stream()
                    .filter(mentor -> mentor.getSpecialization() != null
                            && mentor.getSpecialization().equalsIgnoreCase(specialization))
                .map(MentorResponseDto::fromEntity)
                    .toList();
            return JsonApi.ok(mentors);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMentor(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            Mentor mentor = JsonApi.read(body, MentorRequestDto.class).toEntity();
            mentorBean.addAdmin(mentor);
            return JsonApi.created(MentorResponseDto.fromEntity(mentor));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{mentorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMentor(
        @PathParam("mentorId") String mentorId, String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            Mentor mentor = JsonApi.read(body, MentorRequestDto.class).toEntity();
            mentorBean.update(mentorId, mentor);
            return JsonApi.ok(MentorResponseDto.fromEntity(mentorBean.getById(mentorId)));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{mentorId}")
    public Response deleteMentor(
        @PathParam("mentorId") String mentorId) {
        try {
            mentorBean.delete(mentorId);
            return Response.noContent().build();
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }
}

