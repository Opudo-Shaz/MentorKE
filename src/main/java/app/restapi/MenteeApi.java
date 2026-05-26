package app.restapi;

import app.bean.MenteeBean;
import app.dtos.MenteeRequestDto;
import app.dtos.MenteeResponseDto;
import app.model.Mentee;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/mentees")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class MenteeApi {

    @Inject
    private MenteeBean menteeBean;

    @GET
    public Response getAllMentees() {
        try {
            List<MenteeResponseDto> mentees = menteeBean.findAll().stream()
                    .map(MenteeResponseDto::fromEntity)
                    .toList();
            return JsonApi.ok(mentees);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/{menteeId}")
    public Response getMentee(
        @PathParam("menteeId") String menteeId) {
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
    public Response menteesByMentor(
        @PathParam("mentorId") String mentorId) {
        try {
            List<MenteeResponseDto> mentees = menteeBean.findByMentorId(mentorId).stream()
                    .map(MenteeResponseDto::fromEntity)
                    .toList();
            return JsonApi.ok(mentees);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMentee(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            Mentee mentee = JsonApi.read(body, MenteeRequestDto.class).toEntity();
            menteeBean.addAdmin(mentee);
            return JsonApi.created(MenteeResponseDto.fromEntity(mentee));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{menteeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMentee(
        @PathParam("menteeId") String menteeId, String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            Mentee mentee = JsonApi.read(body, MenteeRequestDto.class).toEntity();
            menteeBean.update(menteeId, mentee);
            return JsonApi.ok(MenteeResponseDto.fromEntity(menteeBean.getById(menteeId)));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{menteeId}")
    public Response deleteMentee(
        @PathParam("menteeId") String menteeId) {
        try {
            menteeBean.delete(menteeId);
            return Response.noContent().build();
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }
}

