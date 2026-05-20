package app.api;

import app.bean.MenteeBean;
import app.model.Mentee;
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

@Path("/mentees")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class MenteeApi {

    @Inject
    private MenteeBean menteeBean;

    @GET
    @Path("/{menteeId}")
    public Response getMentee(@PathParam("menteeId") String menteeId) {
        try {
            Mentee mentee = menteeBean.getMenteeById(menteeId);
            if (mentee == null) {
                return JsonApi.notFound("Mentee not found");
            }

            return JsonApi.ok(mentee);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/mentor/{mentorId}")
    public Response menteesByMentor(@PathParam("mentorId") String mentorId) {
        try {
            List<Mentee> mentees = menteeBean.getMenteesByMentorId(mentorId);
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

            Mentee mentee = JsonApi.read(body, Mentee.class);
            menteeBean.addMenteeAdmin(mentee);
            return JsonApi.created(mentee);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{menteeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMentee(@PathParam("menteeId") String menteeId, String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            Mentee mentee = JsonApi.read(body, Mentee.class);
            menteeBean.updateMentee(menteeId, mentee);
            return JsonApi.ok(menteeBean.getMenteeById(menteeId));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{menteeId}")
    public Response deleteMentee(@PathParam("menteeId") String menteeId) {
        try {
            menteeBean.deleteMentee(menteeId);
            return Response.noContent().build();
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }
}