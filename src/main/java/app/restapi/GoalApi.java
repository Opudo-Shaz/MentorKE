package app.restapi;

import app.bean.GoalBean;
import app.model.MenteeGoal;
import app.model.GoalProgressLog;
import app.security.jwt.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/goals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GoalApi {

    @Inject
    private GoalBean goalBean;

    // POST /api/goals — create a goal
    @POST
    public Response createGoal(Map<String, Object> body, @Context HttpHeaders headers) {
        Long menteeId = extractMenteeId(headers);
        String title       = (String) body.get("title");
        String description = (String) body.get("description");
        String targetDate  = (String) body.get("targetDate");

        @SuppressWarnings("unchecked")
        List<String> milestones = (List<String>) body.get("milestones");

        MenteeGoal goal = goalBean.createGoal(
            menteeId, title, description,
            targetDate != null ? LocalDate.parse(targetDate) : null,
            milestones
        );
        return Response.status(Response.Status.CREATED).entity(goal).build();
    }

    // GET /api/goals — my goals
    @GET
    public Response myGoals(@Context HttpHeaders headers) {
        Long menteeId = extractMenteeId(headers);
        return Response.ok(goalBean.getGoalsForMentee(menteeId)).build();
    }

    // GET /api/goals/{id} — single goal
    @GET
    @Path("/{id}")
    public Response getGoal(@PathParam("id") Long id) {
        MenteeGoal goal = goalBean.getGoalById(id);
        if (goal == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(goal).build();
    }

    // PATCH /api/goals/{id}/progress — manual progress update
    @PATCH
    @Path("/{id}/progress")
    public Response updateProgress(@PathParam("id") Long id, Map<String, Object> body) {
        int progress = (int) body.get("progress");
        String note  = (String) body.getOrDefault("note", "");
        return Response.ok(goalBean.updateProgress(id, progress, note)).build();
    }

    // PATCH /api/goals/{id}/milestones/{mid}/complete
    @PATCH
    @Path("/{id}/milestones/{mid}/complete")
    public Response completeMilestone(@PathParam("id") Long goalId, @PathParam("mid") Long milestoneId) {
        return Response.ok(goalBean.completeMilestone(goalId, milestoneId)).build();
    }

    // PATCH /api/goals/{id}/milestones/{mid}/uncomplete
    @PATCH
    @Path("/{id}/milestones/{mid}/uncomplete")
    public Response uncompleteMilestone(@PathParam("id") Long goalId, @PathParam("mid") Long milestoneId) {
        return Response.ok(goalBean.uncompleteMilestone(goalId, milestoneId)).build();
    }

    // GET /api/goals/{id}/logs — progress history
    @GET
    @Path("/{id}/logs")
    public Response progressLogs(@PathParam("id") Long id) {
        return Response.ok(goalBean.getProgressLogs(id)).build();
    }

    // DELETE /api/goals/{id}
    @DELETE
    @Path("/{id}")
    public Response deleteGoal(@PathParam("id") Long id) {
        goalBean.deleteGoal(id);
        return Response.ok(Map.of("message", "Goal deleted")).build();
    }

    private Long extractMenteeId(HttpHeaders headers) {
        String token = headers.getHeaderString("Authorization");
        return JwtUtil.extractUserId(token);
    }
}