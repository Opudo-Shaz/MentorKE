package app.dao;

import app.model.Session;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class SessionDAO extends GenericDAO<Session, Long> {

    // Get sessions for a mentor
    public List<Session> getSessionsByMentor(String mentorId) {
        String jpql = "SELECT s FROM Session s WHERE s.mentorId = :mentorId ORDER BY s.scheduledDate DESC";
        TypedQuery<Session> query = entityManager.createQuery(jpql, Session.class);
        query.setParameter("mentorId", mentorId);
        return query.getResultList();
    }

    // Get sessions for a mentee
    public List<Session> getSessionsByMentee(String menteeId) {
        String jpql = "SELECT s FROM Session s WHERE s.menteeId = :menteeId ORDER BY s.scheduledDate DESC";
        TypedQuery<Session> query = entityManager.createQuery(jpql, Session.class);
        query.setParameter("menteeId", menteeId);
        return query.getResultList();
    }

    // Get upcoming sessions (scheduled_date > now)
    public List<Session> getUpcomingSessions(String userId) {
        String jpql = "SELECT s FROM Session s WHERE (s.mentorId = :userId OR s.menteeId = :userId) " +
                "AND s.scheduledDate > :now ORDER BY s.scheduledDate ASC";
        TypedQuery<Session> query = entityManager.createQuery(jpql, Session.class);
        query.setParameter("userId", userId);
        query.setParameter("now", LocalDateTime.now());
        return query.getResultList();
    }

    // Get completed sessions
    public List<Session> getCompletedSessions(String userId) {
        String jpql = "SELECT s FROM Session s WHERE (s.mentorId = :userId OR s.menteeId = :userId) " +
                "AND s.status = 'COMPLETED' ORDER BY s.scheduledDate DESC";
        TypedQuery<Session> query = entityManager.createQuery(jpql, Session.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public int countSessionsInMonth(LocalDateTime monthStartInclusive, LocalDateTime nextMonthStartExclusive) {
        String jpql = "SELECT COUNT(s) FROM Session s WHERE s.scheduledDate >= :monthStart AND s.scheduledDate < :nextMonthStart";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("monthStart", monthStartInclusive);
        query.setParameter("nextMonthStart", nextMonthStartExclusive);
        return query.getSingleResult().intValue();
    }

    // Count completed sessions for a mentor
    public int countCompletedSessionsByMentor(String mentorId) {
        return entityManager.createQuery(
                "SELECT COUNT(s) FROM Session s WHERE s.mentorId = :mentorId AND s.status = 'COMPLETED'", Long.class)
                .setParameter("mentorId", mentorId)
                .getSingleResult().intValue();
    }

    // Count completed sessions for a mentee
    public int countCompletedSessionsByMentee(String menteeId) {
        return entityManager.createQuery(
                "SELECT COUNT(s) FROM Session s WHERE s.menteeId = :menteeId AND s.status = 'COMPLETED'", Long.class)
                .setParameter("menteeId", menteeId)
                .getSingleResult().intValue();
    }

    // Count cancelled sessions for a mentor
    public int countCancelledSessionsByMentor(String mentorId) {
        return entityManager.createQuery(
                "SELECT COUNT(s) FROM Session s WHERE s.mentorId = :mentorId AND s.status = 'CANCELLED'", Long.class)
                .setParameter("mentorId", mentorId)
                .getSingleResult().intValue();
    }

    // Count pending sessions for a mentor
    public int countPendingSessionsByMentor(String mentorId) {
        return entityManager.createQuery(
                "SELECT COUNT(s) FROM Session s WHERE s.mentorId = :mentorId AND s.status = 'PENDING'", Long.class)
                .setParameter("mentorId", mentorId)
                .getSingleResult().intValue();
    }

    // Total mentorship hours for a mentor (sum of duration of completed sessions)
    public int totalMentorshipHoursByMentor(String mentorId) {
        Long mins = entityManager.createQuery(
                "SELECT SUM(s.durationMinutes) FROM Session s WHERE s.mentorId = :mentorId AND s.status = 'COMPLETED'",
                Long.class)
                .setParameter("mentorId", mentorId)
                .getSingleResult();
        return mins != null ? (int) (mins / 60) : 0;
    }

    // Total mentorship hours for a mentee
    public int totalMentorshipHoursByMentee(String menteeId) {
        Long mins = entityManager.createQuery(
                "SELECT SUM(s.durationMinutes) FROM Session s WHERE s.menteeId = :menteeId AND s.status = 'COMPLETED'",
                Long.class)
                .setParameter("menteeId", menteeId)
                .getSingleResult();
        return mins != null ? (int) (mins / 60) : 0;
    }

    // Monthly session counts for last 6 months (mentor)
    public Map<String, Integer> monthlySessionCountByMentor(String mentorId) {
        Map<String, Integer> result = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM");
        for (int i = 5; i >= 0; i--) {
            LocalDateTime start = now.minusMonths(i).withDayOfMonth(1).toLocalDate().atStartOfDay();
            LocalDateTime end = start.plusMonths(1);
            String label = start.format(fmt);
            int count = countSessionsInMonth(mentorId, start, end);
            result.put(label, count);
        }
        return result;
    }

    // Monthly session counts for last 6 months (mentee)
    public Map<String, Integer> monthlySessionCountByMentee(String menteeId) {
        Map<String, Integer> result = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM");
        for (int i = 5; i >= 0; i--) {
            LocalDateTime start = now.minusMonths(i).withDayOfMonth(1).toLocalDate().atStartOfDay();
            LocalDateTime end = start.plusMonths(1);
            String label = start.format(fmt);
            Long count = entityManager.createQuery(
                    "SELECT COUNT(s) FROM Session s WHERE s.menteeId = :menteeId " +
                            "AND s.scheduledDate >= :start AND s.scheduledDate < :end",
                    Long.class)
                    .setParameter("menteeId", menteeId)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getSingleResult();
            result.put(label, count.intValue());
        }
        return result;
    }

    // Average rating for a mentor
    public double averageRatingByMentor(String mentorId) {
        Double avg = entityManager.createQuery(
                "SELECT AVG(s.mentorRating) FROM Session s WHERE s.mentorId = :mentorId AND s.mentorRating IS NOT NULL",
                Double.class)
                .setParameter("mentorId", mentorId)
                .getSingleResult();
        return avg != null ? avg : 0.0;
    }

    // helper used by monthlySessionCountByMentor
    private int countSessionsInMonth(String mentorId, LocalDateTime start, LocalDateTime end) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(s) FROM Session s WHERE s.mentorId = :mentorId " +
                        "AND s.scheduledDate >= :start AND s.scheduledDate < :end",
                Long.class)
                .setParameter("mentorId", mentorId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
        return count.intValue();
    }
}
