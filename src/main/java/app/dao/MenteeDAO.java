package app.dao;

import app.model.Mentee;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Dependent
public class MenteeDAO extends GenericDAO<Mentee, Long> {

    public MenteeDAO() {
        super(Mentee.class);
    }

    // Add mentee
    public void addMentee(Mentee mentee) {
        save(mentee);
    }

    // Find mentee by id
    public Mentee getMentee(String id) {
        return findById(Long.parseLong(id));
    }

    // Find all mentees
    public List<Mentee> getAllMentees() {
        return findAll();
    }

    public Mentee getMenteeByUsername(String username) {
        String jpql = "SELECT m FROM Mentee m WHERE m.username = :username";
        TypedQuery<Mentee> query = entityManager.createQuery(jpql, Mentee.class);
        query.setParameter("username", username);
        List<Mentee> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // update mentee
    public void updateMentee(String id, Mentee mentee) {
        mentee.setId(Long.parseLong(id));
        update(mentee);
    }

    // delete mentee
    public void deleteMentee(String id) {
        delete(Long.parseLong(id));
    }

    // Get total number of mentees
    public int getTotalMentees() {
        return count();
    }

    // Find mentee by user id
    public Mentee getMenteeByUserId(String userId) {
        return getMentee(userId);
    }

    // Get all active mentees
    public List<Mentee> getActiveMentees() {
        String jpql = "SELECT m FROM Mentee m WHERE m.status = 'Active' ORDER BY m.id ASC";
        TypedQuery<Mentee> query = entityManager.createQuery(jpql, Mentee.class);
        return query.getResultList();
    }

    // Get mentees without a mentor
    public List<Mentee> getMenteesWithoutMentor() {
        String jpql = "SELECT m FROM Mentee m WHERE m.mentor IS NULL AND m.status = 'Active' ORDER BY m.id ASC";
        TypedQuery<Mentee> query = entityManager.createQuery(jpql, Mentee.class);
        return query.getResultList();
    }

    // Get mentees assigned to a specific mentor
    public List<Mentee> getMenteesByMentorId(String mentorId) {
        String jpql = "SELECT m FROM Mentee m WHERE m.mentor.id = :mentorId";
        TypedQuery<Mentee> query = entityManager.createQuery(jpql, Mentee.class);
        query.setParameter("mentorId", Long.parseLong(mentorId));
        return query.getResultList();
    }
}