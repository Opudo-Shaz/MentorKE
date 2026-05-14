package app.dao;

import app.model.MatchRequest;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Dependent
public class MatchRequestDAO extends GenericDAO<MatchRequest, Long> {

    public MatchRequestDAO() {
        super(MatchRequest.class);
    }

    // Add match request
    public void addMatchRequest(MatchRequest matchRequest) {
        save(matchRequest);
    }

    // Find match request by id
    public MatchRequest getMatchRequest(Long id) {
        return findById(id);
    }

    // Find all match requests
    public List<MatchRequest> getAllMatchRequests() {
        return findAll();
    }

    // Update match request
    public void updateMatchRequest(Long id, MatchRequest matchRequest) {
        matchRequest.setId(id);
        update(matchRequest);
    }

    // Delete match request
    public void deleteMatchRequest(Long id) {
        delete(id);
    }

    // Get total number of match requests
    public int getTotalMatchRequests() {
        return count();
    }

    // Get pending match requests for a mentor
    public List<MatchRequest> getPendingRequestsForMentor(String mentorId) {
        String jpql = "SELECT mr FROM MatchRequest mr WHERE mr.mentorId = :mentorId AND mr.status = 'PENDING' " +
                     "ORDER BY mr.createdAt DESC";
        TypedQuery<MatchRequest> query = entityManager.createQuery(jpql, MatchRequest.class);
        query.setParameter("mentorId", mentorId);
        return query.getResultList();
    }

    // Get match requests by mentee
    public List<MatchRequest> getRequestsByMentee(String menteeId) {
        String jpql = "SELECT mr FROM MatchRequest mr WHERE mr.menteeId = :menteeId ORDER BY mr.createdAt DESC";
        TypedQuery<MatchRequest> query = entityManager.createQuery(jpql, MatchRequest.class);
        query.setParameter("menteeId", menteeId);
        return query.getResultList();
    }

    // Get approved/matched requests for a mentee
    public MatchRequest getApprovedMatchForMentee(String menteeId) {
        String jpql = "SELECT mr FROM MatchRequest mr WHERE mr.menteeId = :menteeId AND mr.status = 'APPROVED'";
        TypedQuery<MatchRequest> query = entityManager.createQuery(jpql, MatchRequest.class);
        query.setParameter("menteeId", menteeId);
        query.setMaxResults(1);
        List<MatchRequest> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // Get pending requests (not yet assigned to a mentor)
    public List<MatchRequest> getPendingUnassignedRequests() {
        String jpql = "SELECT mr FROM MatchRequest mr WHERE mr.status = 'PENDING' AND mr.mentorId IS NULL " +
                     "ORDER BY mr.createdAt ASC";
        TypedQuery<MatchRequest> query = entityManager.createQuery(jpql, MatchRequest.class);
        return query.getResultList();
    }
}
