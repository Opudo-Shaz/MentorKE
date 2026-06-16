package app.dao;

import app.model.GoalMilestone;
import app.model.GoalProgressLog;
import app.model.MenteeGoal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class GoalDAO {

    @PersistenceContext(unitName = "MentorKEPU")
    private EntityManager em;

    // ── Goals ──────────────────────────────────────────────

    public MenteeGoal save(MenteeGoal goal) {
        if (goal.getId() == null) {
            em.persist(goal);
            return goal;
        }
        return em.merge(goal);
    }

    public MenteeGoal findById(Long id) {
        return em.find(MenteeGoal.class, id);
    }

    public List<MenteeGoal> findByMenteeId(Long menteeId) {
        return em.createQuery(
            "SELECT g FROM MenteeGoal g WHERE g.menteeId = :menteeId ORDER BY g.createdAt DESC",
            MenteeGoal.class)
            .setParameter("menteeId", menteeId)
            .getResultList();
    }

    public void delete(Long id) {
        MenteeGoal goal = findById(id);
        if (goal != null) em.remove(goal);
    }

    public long countByMenteeId(Long menteeId) {
        return em.createQuery(
            "SELECT COUNT(g) FROM MenteeGoal g WHERE g.menteeId = :menteeId", Long.class)
            .setParameter("menteeId", menteeId)
            .getSingleResult();
    }

    public long countByMenteeIdAndStatus(Long menteeId, String status) {
        return em.createQuery(
            "SELECT COUNT(g) FROM MenteeGoal g WHERE g.menteeId = :menteeId AND g.status = :status", Long.class)
            .setParameter("menteeId", menteeId)
            .setParameter("status", status)
            .getSingleResult();
    }

    public int averageProgress(Long menteeId) {
        Double avg = em.createQuery(
            "SELECT AVG(g.overallProgress) FROM MenteeGoal g WHERE g.menteeId = :menteeId", Double.class)
            .setParameter("menteeId", menteeId)
            .getSingleResult();
        return avg != null ? avg.intValue() : 0;
    }

    // ── Milestones ─────────────────────────────────────────

    public GoalMilestone findMilestoneById(Long id) {
        return em.find(GoalMilestone.class, id);
    }

    public GoalMilestone saveMilestone(GoalMilestone milestone) {
        if (milestone.getId() == null) {
            em.persist(milestone);
            return milestone;
        }
        return em.merge(milestone);
    }

    // ── Progress Logs ──────────────────────────────────────

    public void saveLog(GoalProgressLog log) {
        em.persist(log);
    }

    public List<GoalProgressLog> findLogsByGoalId(Long goalId) {
        return em.createQuery(
            "SELECT l FROM GoalProgressLog l WHERE l.goal.id = :goalId ORDER BY l.loggedAt DESC",
            GoalProgressLog.class)
            .setParameter("goalId", goalId)
            .getResultList();
    }
}