package app.bean;

import app.dao.GoalDAO;
import app.model.GoalMilestone;
import app.model.GoalProgressLog;
import app.model.MenteeGoal;
import app.utility.logging.AppLogger;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
@Named("goalBean")
public class GoalBean {

    private static final Logger logger = AppLogger.getLogger(GoalBean.class);

    @Inject
    private GoalDAO goalDAO;

    // Create goal with optional milestones
    public MenteeGoal createGoal(Long menteeId, String title, String description,
                                  LocalDate targetDate, List<String> milestoneTitles) {
        logger.info("Creating goal for mentee {}: {}", menteeId, title);

        MenteeGoal goal = new MenteeGoal();
        goal.setMenteeId(menteeId);
        goal.setTitle(title);
        goal.setDescription(description);
        goal.setTargetDate(targetDate);

        if (milestoneTitles != null) {
            for (int i = 0; i < milestoneTitles.size(); i++) {
                String t = milestoneTitles.get(i).trim();
                if (!t.isEmpty()) {
                    GoalMilestone m = new GoalMilestone();
                    m.setTitle(t);
                    m.setOrderIndex(i);
                    m.setGoal(goal);
                    goal.getMilestones().add(m);
                }
            }
        }

        return goalDAO.save(goal);
    }

    // Manually set progress + log it
    public MenteeGoal updateProgress(Long goalId, int newProgress, String note) {
        MenteeGoal goal = goalDAO.findById(goalId);
        if (goal == null) throw new IllegalArgumentException("Goal not found: " + goalId);

        goal.updateProgress(newProgress);
        goalDAO.save(goal);

        GoalProgressLog log = new GoalProgressLog();
        log.setGoal(goal);
        log.setProgressSnapshot(newProgress);
        log.setNote(note);
        goalDAO.saveLog(log);

        logger.info("Progress updated for goal {} to {}%", goalId, newProgress);
        return goal;
    }

    // Tick a milestone → auto-recalculate progress
    public MenteeGoal completeMilestone(Long goalId, Long milestoneId) {
        MenteeGoal goal = goalDAO.findById(goalId);
        if (goal == null) throw new IllegalArgumentException("Goal not found: " + goalId);

        GoalMilestone milestone = goalDAO.findMilestoneById(milestoneId);
        if (milestone == null) throw new IllegalArgumentException("Milestone not found: " + milestoneId);

        milestone.setCompleted(true);
        milestone.setCompletedAt(LocalDateTime.now());
        goalDAO.saveMilestone(milestone);

        goal.recalculateProgressFromMilestones();

        GoalProgressLog log = new GoalProgressLog();
        log.setGoal(goal);
        log.setProgressSnapshot(goal.getOverallProgress());
        log.setNote("Milestone completed: " + milestone.getTitle());
        goalDAO.saveLog(log);

        return goalDAO.save(goal);
    }

    // Uncheck a milestone → recalculate
    public MenteeGoal uncompleteMilestone(Long goalId, Long milestoneId) {
        MenteeGoal goal = goalDAO.findById(goalId);
        GoalMilestone milestone = goalDAO.findMilestoneById(milestoneId);
        if (goal == null || milestone == null) throw new IllegalArgumentException("Goal or milestone not found");

        milestone.setCompleted(false);
        milestone.setCompletedAt(null);
        goalDAO.saveMilestone(milestone);
        goal.recalculateProgressFromMilestones();

        return goalDAO.save(goal);
    }

    public MenteeGoal getGoalById(Long goalId) {
        return goalDAO.findById(goalId);
    }

    public List<MenteeGoal> getGoalsForMentee(Long menteeId) {
        return goalDAO.findByMenteeId(menteeId);
    }

    public List<GoalProgressLog> getProgressLogs(Long goalId) {
        return goalDAO.findLogsByGoalId(goalId);
    }

    public void deleteGoal(Long goalId) {
        goalDAO.delete(goalId);
        logger.info("Goal {} deleted", goalId);
    }
}
    

