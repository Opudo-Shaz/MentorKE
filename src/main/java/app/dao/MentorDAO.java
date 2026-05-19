package app.dao;

import app.model.Mentor;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Dependent
public class MentorDAO extends GenericDAO<Mentor, Long> {

    public Mentor getMentorByUsername(String username) {
        String jpql = "SELECT m FROM Mentor m WHERE m.username = :username";
        TypedQuery<Mentor> query = entityManager.createQuery(jpql, Mentor.class);
        query.setParameter("username", username);
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