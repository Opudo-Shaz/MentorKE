package app.restapi;

import app.bean.MentorAvailabilityBean;
import app.model.MentorAvailability;
import app.security.jwt.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/availability")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AvailabilityApi {

    @Inject
    private MentorAvailabilityBean availabilityBean;

    @Inject
    private JwtUtil jwtUtil;

    // GET /api/availability — get my availability
    @GET
    public Response getAvailability(@Context HttpHeaders headers) {
        Long mentorId = extractMentorId(headers);
        if (mentorId == null) return unauthorized();
        List<MentorAvailability> slots = availabilityBean.getAvailability(mentorId);
        return Response.ok(slots).build();
    }

    // POST /api/availability — save my availability
    @POST
    public Response saveAvailability(@Context HttpHeaders headers, List<Map<String, String>> slots) {
        Long mentorId = extractMentorId(headers);
        if (mentorId == null) return unauthorized();
        availabilityBean.saveAvailability(mentorId, slots);
        return Response.ok(Map.of("message", "Availability saved successfully")).build();
    }

    // GET /api/availability/{mentorId} — mentee views mentor availability
    @GET
    @Path("/{mentorId}")
    public Response getMentorAvailability(@PathParam("mentorId") Long mentorId) {
        List<MentorAvailability> slots = availabilityBean.getAvailability(mentorId);
        return Response.ok(slots).build();
    }

    // ── helpers ────────────────────────────────────────────

    private Long extractMentorId(HttpHeaders headers) {
        try {
            String authHeader = headers.getHeaderString("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
            String token    = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            // username is the subject — parse the userId claim if you store it,
            // or look up by username. Adjust to match how you store the user id.
            // If you store userId as a claim, add: return Long.parseLong(jwtUtil.validateToken(token).get("userId", String.class));
            return Long.parseLong(username); // replace if subject is username not id
        } catch (Exception e) {
            return null;
        }
    }

    private Response unauthorized() {
        return Response.status(Response.Status.UNAUTHORIZED)
                       .entity(Map.of("error", "Invalid or missing token"))
                       .build();
    }
}