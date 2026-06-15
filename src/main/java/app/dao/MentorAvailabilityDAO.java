package app.dao;

import app.model.MentorAvailability;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class MentorAvailabilityDAO {

    @PersistenceContext(unitName = "MentorKEPU")
    private EntityManager em;

    public List<MentorAvailability> findByMentorId(Long mentorId) {
        return em.createQuery(
            "SELECT a FROM MentorAvailability a WHERE a.mentorId = :mentorId ORDER BY a.dayOfWeek",
            MentorAvailability.class)
            .setParameter("mentorId", mentorId)
            .getResultList();
    }

    public void save(MentorAvailability availability) {
        if (availability.getId() == null) {
            em.persist(availability);
        } else {
            em.merge(availability);
        }
    }

    public void deleteByMentorId(Long mentorId) {
        em.createQuery("DELETE FROM MentorAvailability a WHERE a.mentorId = :mentorId")
          .setParameter("mentorId", mentorId)
          .executeUpdate();
    }
}