package app.restapi;

import app.bean.MatchRequestBean;
import app.dtos.MatchRequestCreateRequestDto;
import app.dtos.MatchRequestResponseDto;
import app.model.MatchRequest;
import app.security.jwt.JwtSecured;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/match-requests")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@JwtSecured
public class MatchRequestApi {

    @Inject
    private MatchRequestBean matchRequestBean;

    @GET
    public Response getAllMatchRequests() {
        try {
            List<MatchRequestResponseDto> requests = matchRequestBean.getAllMatchRequests().stream()
                    .map(MatchRequestResponseDto::fromEntity)
                    .toList();
            return JsonApi.ok(requests);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/mentor/{mentorId}")
    public Response getRequestsByMentor(@PathParam("mentorId") String mentorId) {
        try {
            if (mentorId == null || mentorId.isBlank()) {
                return JsonApi.badRequest("mentorId is required");
            }

            List<MatchRequestResponseDto> requests = matchRequestBean.getRequestsByMentor(mentorId).stream()
                    .map(MatchRequestResponseDto::fromEntity)
                    .toList();
            return JsonApi.ok(requests);
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @GET
    @Path("/{requestId}")
    public Response getMatchRequest(@PathParam("requestId") String requestId) {
        try {
            MatchRequest request = matchRequestBean.getMatchRequest(requestId);
            if (request == null) {
                return JsonApi.notFound("Match request not found");
            }
            return JsonApi.ok(MatchRequestResponseDto.fromEntity(request));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMatchRequest(String body) {
        try {
            if (body == null || body.isBlank()) {
                return JsonApi.badRequest("Request body is required");
            }
            MatchRequestCreateRequestDto dto = JsonApi.read(body, MatchRequestCreateRequestDto.class);
            MatchRequest request = matchRequestBean.createMatchRequest(dto.getMenteeId(), dto.getMentorId(), dto.getSpecialization());
            return JsonApi.created(MatchRequestResponseDto.fromEntity(request));
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{requestId}/approve")
    public Response approveRequest(@PathParam("requestId") String requestId) {
        try {
            matchRequestBean.approveMentorRequest(requestId);
            return JsonApi.ok(new Object() {
                public String status = "approved";
            });
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @PUT
    @Path("/{requestId}/reject")
    public Response rejectRequest(@PathParam("requestId") String requestId) {
        try {
            matchRequestBean.rejectMentorRequest(requestId);
            return JsonApi.ok(new Object() {
                public String status = "rejected";
            });
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }

    @DELETE
    @Path("/{requestId}")
    public Response deleteMatchRequest(@PathParam("requestId") String requestId) {
        try {
            matchRequestBean.deleteMatchRequest(requestId);
            return Response.noContent().build();
        } catch (Exception e) {
            return JsonApi.badRequest(e.getMessage());
        }
    }
}

