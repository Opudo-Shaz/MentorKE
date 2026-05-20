package app.api;

import app.bean.MentorBean;
import app.model.Mentor;
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

@Path("/mentors")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class MentorApi {

    @Inject
    private MentorBean mentorBean;

    @GET
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
    public Response getMentor(@PathParam("mentorId") String mentorId) {
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
    public Response mentorsBySpecialization(@PathParam("specialization") String specialization) {
        try {
            List<Mentor> mentors = mentorBean.getAllMentors().stream()
                    .filter(mentor -> mentor.getSpecialization() != null && mentor.getSpecialization().equalsIgnoreCase(specialization))
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
    public Response updateMentor(@PathParam("mentorId") String mentorId, String body) {
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
    public Response deleteMentor(@PathParam("mentorId") String mentorId) {
        try {
            mentorBean.deleteMentor(mentorId);
            return Response.noContent().build();
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }
}