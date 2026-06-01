package app.dao;

import app.model.Mentor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
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

    public int countActiveMentors() {
        String jpql = "SELECT COUNT(m) FROM Mentor m WHERE UPPER(COALESCE(m.status, '')) = 'ACTIVE'";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult().intValue();
    }

    public List<Mentor> searchMentors(
            String specialization,
            Integer minimumYearsOfExperience,
            String availability,
            String location,
            Double minimumRating
    ) {
        StringBuilder jpql = new StringBuilder(
                "SELECT m FROM Mentor m WHERE UPPER(COALESCE(m.status, '')) = 'ACTIVE'"
        );
        Map<String, Object> params = new LinkedHashMap<>();

        if (specialization != null && !specialization.isBlank()) {
            jpql.append(" AND LOWER(m.specialization) LIKE :specialization");
            params.put("specialization", "%" + specialization.trim().toLowerCase() + "%");
        }

        if (minimumYearsOfExperience != null) {
            jpql.append(" AND COALESCE(m.yearsOfExperience, 0) >= :minimumYearsOfExperience");
            params.put("minimumYearsOfExperience", minimumYearsOfExperience);
        }

        if (availability != null && !availability.isBlank()) {
            jpql.append(" AND LOWER(COALESCE(m.availability, '')) LIKE :availability");
            params.put("availability", "%" + availability.trim().toLowerCase() + "%");
        }

        if (location != null && !location.isBlank()) {
            jpql.append(" AND LOWER(COALESCE(m.location, '')) LIKE :location");
            params.put("location", "%" + location.trim().toLowerCase() + "%");
        }

        if (minimumRating != null) {
            jpql.append(" AND COALESCE(m.averageRating, 0.0) >= :minimumRating");
            params.put("minimumRating", minimumRating);
        }

        jpql.append(" ORDER BY COALESCE(m.averageRating, 0.0) DESC, COALESCE(m.ratingCount, 0) DESC, COALESCE(m.yearsOfExperience, 0) DESC, m.specialization ASC, m.username ASC");

        TypedQuery<Mentor> query = entityManager.createQuery(jpql.toString(), Mentor.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }
}