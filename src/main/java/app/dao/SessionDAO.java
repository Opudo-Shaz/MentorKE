package app.dao;

import app.model.Session;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

@Dependent
public class SessionDAO extends GenericDAO<Session, String> {

    public SessionDAO() {
        super(Session.class);
    }

    // Add session
    public void addSession(Session session) {
        save(session);
    }

    // Find session by id
    public Session getSession(String id) {
        return findById(id);
    }

    // Find all sessions
    public List<Session> getAllSessions() {
        return findAll();
    }

    // Update session
    public void updateSession(String id, Session session) {
        session.setId(id);
        update(session);
    }

    // Delete session
    public void deleteSession(String id) {
        delete(id);
    }

    // Get total number of sessions
    public int getTotalSessions() {
        return count();
    }

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
}
