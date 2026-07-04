package app.restapi;

import app.bean.GoalBean;
import app.model.GoalProgressLog;
import app.model.MenteeGoal;
import app.security.jwt.JwtUtil;
import app.utility.logging.AppLogger;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/goals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GoalApi {

    private static final Logger logger = AppLogger.getLogger(GoalApi.class);

    @Inject
    private GoalBean goalBean;

    @Inject
    private JwtUtil jwtUtil;

    // POST /api/goals
    @POST
    public Response createGoal(Map<String, Object> body, @Context HttpHeaders headers) {
        try {
            Long menteeId = extractMenteeId(headers);
            if (menteeId == null) {
                logger.warn("createGoal: unauthorized access attempt");
                return unauthorized();
            }

            String title       = (String) body.get("title");
            String description = (String) body.get("description");
            String targetDate  = (String) body.get("targetDate");

            logger.debug("createGoal: title={}, description={}, targetDate={}, menteeId={}", title, description, targetDate, menteeId);

            @SuppressWarnings("unchecked")
            List<String> milestones = (List<String>) body.get("milestones");

            MenteeGoal goal = goalBean.createGoal(
                menteeId, title, description,
                targetDate != null ? LocalDate.parse(targetDate) : null,
                milestones
            );
            logger.info("createGoal: goal created successfully with id={}", goal.getId());
            return Response.status(Response.Status.CREATED).entity(goal).build();
        } catch (Exception e) {
            logger.error("createGoal: failed to create goal", e);
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Failed to create goal: " + e.getMessage()))
                           .build();
        }
    }

    // GET /api/goals
    @GET
    public Response myGoals(@Context HttpHeaders headers) {
        try {
            Long menteeId = extractMenteeId(headers);
            if (menteeId == null) {
                logger.warn("myGoals: unauthorized access");
                return unauthorized();
            }
            List<MenteeGoal> goals = goalBean.getGoalsForMentee(menteeId);
            logger.debug("myGoals: retrieved {} goals for menteeId={}", goals.size(), menteeId);
            return Response.ok(goals).build();
        } catch (Exception e) {
            logger.error("myGoals: failed to retrieve goals", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of("error", "Failed to retrieve goals: " + e.getMessage()))
                           .build();
        }
    }

    // GET /api/goals/{id}
    @GET
    @Path("/{id}")
    public Response getGoal(@PathParam("id") Long id) {
        try {
            MenteeGoal goal = goalBean.getGoalById(id);
            if (goal == null) {
                logger.warn("getGoal: goal not found id={}", id);
                return Response.status(Response.Status.NOT_FOUND).entity(Map.of("error", "Goal not found")).build();
            }
            logger.debug("getGoal: retrieved goal id={}", id);
            return Response.ok(goal).build();
        } catch (Exception e) {
            logger.error("getGoal: failed to retrieve goal id={}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of("error", "Failed to retrieve goal: " + e.getMessage()))
                           .build();
        }
    }

    // PATCH /api/goals/{id}/progress
    @PATCH
    @Path("/{id}/progress")
    public Response updateProgress(@PathParam("id") Long id, Map<String, Object> body) {
        try {
            int    progress = ((Number) body.get("progress")).intValue();
            String note     = (String) body.getOrDefault("note", "");
            logger.debug("updateProgress: id={}, progress={}, note={}", id, progress, note);
            MenteeGoal goal = goalBean.updateProgress(id, progress, note);
            logger.info("updateProgress: goal updated id={}", id);
            return Response.ok(goal).build();
        } catch (Exception e) {
            logger.error("updateProgress: failed to update progress id={}", id, e);
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Failed to update progress: " + e.getMessage()))
                           .build();
        }
    }

    // PATCH /api/goals/{id}/milestones/{mid}/complete
    @PATCH
    @Path("/{id}/milestones/{mid}/complete")
    public Response completeMilestone(@PathParam("id") Long goalId,
                                      @PathParam("mid") Long milestoneId) {
        try {
            logger.debug("completeMilestone: goalId={}, milestoneId={}", goalId, milestoneId);
            MenteeGoal goal = goalBean.completeMilestone(goalId, milestoneId);
            logger.info("completeMilestone: milestone completed goalId={}", goalId);
            return Response.ok(goal).build();
        } catch (Exception e) {
            logger.error("completeMilestone: failed goalId={} milestoneId={}", goalId, milestoneId, e);
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Failed to complete milestone: " + e.getMessage()))
                           .build();
        }
    }

    // PATCH /api/goals/{id}/milestones/{mid}/uncomplete
    @PATCH
    @Path("/{id}/milestones/{mid}/uncomplete")
    public Response uncompleteMilestone(@PathParam("id") Long goalId,
                                        @PathParam("mid") Long milestoneId) {
        try {
            logger.debug("uncompleteMilestone: goalId={}, milestoneId={}", goalId, milestoneId);
            MenteeGoal goal = goalBean.uncompleteMilestone(goalId, milestoneId);
            logger.info("uncompleteMilestone: milestone uncompleted goalId={}", goalId);
            return Response.ok(goal).build();
        } catch (Exception e) {
            logger.error("uncompleteMilestone: failed goalId={} milestoneId={}", goalId, milestoneId, e);
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Failed to uncomplete milestone: " + e.getMessage()))
                           .build();
        }
    }

    // GET /api/goals/{id}/logs
    @GET
    @Path("/{id}/logs")
    public Response progressLogs(@PathParam("id") Long id) {
        try {
            List<GoalProgressLog> logs = goalBean.getProgressLogs(id);
            logger.debug("progressLogs: retrieved {} logs for goalId={}", logs.size(), id);
            return Response.ok(logs).build();
        } catch (Exception e) {
            logger.error("progressLogs: failed to retrieve logs id={}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of("error", "Failed to retrieve logs: " + e.getMessage()))
                           .build();
        }
    }

    // DELETE /api/goals/{id}
    @DELETE
    @Path("/{id}")
    public Response deleteGoal(@PathParam("id") Long id) {
        try {
            logger.debug("deleteGoal: deleting goalId={}", id);
            goalBean.deleteGoal(id);
            logger.info("deleteGoal: goal deleted id={}", id);
            return Response.ok(Map.of("message", "Goal deleted")).build();
        } catch (Exception e) {
            logger.error("deleteGoal: failed to delete id={}", id, e);
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Failed to delete goal: " + e.getMessage()))
                           .build();
        }
    }

    // ── helpers ────────────────────────────────────────────

    private Long extractMenteeId(HttpHeaders headers) {
        return jwtUtil.extractUserIdFromHeader(
            headers.getHeaderString("Authorization")
        );
    }

    private Response unauthorized() {
        return Response.status(Response.Status.UNAUTHORIZED)
                       .entity(Map.of("error", "Invalid or missing token"))
                       .build();
    }
}