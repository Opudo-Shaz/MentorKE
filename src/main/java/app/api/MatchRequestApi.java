package app.api;

import app.bean.MatchRequestBean;
import app.model.MatchRequest;
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

@Path("/match-requests")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class MatchRequestApi {

    @Inject
    private MatchRequestBean matchRequestBean;

    @GET
    @Path("/mentee/{menteeId}")
    public Response requestsByMentee(@PathParam("menteeId") String menteeId) {
        try {
            List<MatchRequest> requests = matchRequestBean.getRequestsByMentee(menteeId);
            return JsonApi.ok(requests);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/mentor/{mentorId}")
    public Response pendingForMentor(@PathParam("mentorId") String mentorId) {
        try {
            List<MatchRequest> requests = matchRequestBean.getPendingRequestsForMentor(mentorId);
            return JsonApi.ok(requests);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/{requestId}")
    public Response getRequest(@PathParam("requestId") String requestId) {
        try {
            MatchRequest request = matchRequestBean.getMatchRequest(requestId);
            if (request == null) {
                return JsonApi.notFound("Match request not found");
            }

            return JsonApi.ok(request);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRequest(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            MatchRequestCreateRequest request = JsonApi.read(body, MatchRequestCreateRequest.class);
            validateCreateRequest(request);

            matchRequestBean.requestMentor(request.menteeId(), request.mentorId(), request.specialization());
            return JsonApi.created(new MatchRequestCreatedResponse("Request created successfully"));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{requestId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRequest(@PathParam("requestId") String requestId, String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }

            MatchRequestUpdateRequest request = JsonApi.read(body, MatchRequestUpdateRequest.class);
            String status = request.status() == null ? "" : request.status().trim().toUpperCase();

            if ("APPROVED".equals(status)) {
                matchRequestBean.approveMentorRequest(requestId);
            } else if ("REJECTED".equals(status)) {
                matchRequestBean.rejectMentorRequest(requestId);
            } else {
                return JsonApi.badRequest("status must be APPROVED or REJECTED");
            }

            return JsonApi.ok(matchRequestBean.getMatchRequest(requestId));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{requestId}")
    public Response deleteRequest(@PathParam("requestId") String requestId) {
        try {
            matchRequestBean.deleteMatchRequest(requestId);
            return Response.noContent().build();
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    private void validateCreateRequest(MatchRequestCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.menteeId() == null || request.menteeId().isBlank()) {
            throw new IllegalArgumentException("menteeId is required");
        }
        if (request.mentorId() == null || request.mentorId().isBlank()) {
            throw new IllegalArgumentException("mentorId is required");
        }
        if (request.specialization() == null || request.specialization().isBlank()) {
            throw new IllegalArgumentException("specialization is required");
        }
    }

    public record MatchRequestCreateRequest(String menteeId, String mentorId, String specialization) {
    }

    public record MatchRequestUpdateRequest(String status) {
    }

    public record MatchRequestCreatedResponse(String message) {
    }
}