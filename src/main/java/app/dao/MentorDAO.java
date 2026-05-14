package app.dao;

import app.model.Mentor;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Dependent
public class MentorDAO extends GenericDAO<Mentor, String> {

    public MentorDAO() {
        super(Mentor.class);
    }

    // Add mentor
    public void addMentor(Mentor mentor) {
        save(mentor);
    }

    // Find mentor by id
    public Mentor getMentor(String id) {
        return findById(id);
    }

    // Find all mentors
    public List<Mentor> getAllMentors() {
        return findAll();
    }

    // Update mentor
    public void updateMentor(String id, Mentor mentor) {
        mentor.setId(id);
        update(mentor);
    }
    
    // Delete mentor
    public void deleteMentor(String id) {
        delete(id);
    }

    // Get total number of mentors
    public int getTotalMentors() {
        return count();
    }

    // Find mentor by user id
    public Mentor getMentorByUserId(String userId) {
        String jpql = "SELECT m FROM Mentor m WHERE m.userId = :userId";
        TypedQuery<Mentor> query = entityManager.createQuery(jpql, Mentor.class);
        query.setParameter("userId", userId);
        List<Mentor> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // Get all active mentors
    public List<Mentor> getActiveMentors() {
        String jpql = "SELECT m FROM Mentor m WHERE m.status = 'Active' ORDER BY m.specialization ASC";
        TypedQuery<Mentor> query = entityManager.createQuery(jpql, Mentor.class);
        return query.getResultList();
    }
}