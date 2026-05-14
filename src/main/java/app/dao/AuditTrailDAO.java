package app.dao;

import app.model.AuditTrail;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Dependent
public class AuditTrailDAO extends GenericDAO<AuditTrail, Long> {

    public AuditTrailDAO() {
        super(AuditTrail.class);
    }

    // Add audit trail
    public void addAuditTrail(AuditTrail audit) {
        save(audit);
    }

    // Get by id
    public AuditTrail getAuditTrail(String id) {
        return findById(Long.parseLong(id));
    }

    // Return audit trail count
    public int getTotalAuditTrails() {
        return count();
    }

    // Get all audit trails ordered by timestamp
    public List<AuditTrail> getAllAuditTrails() {
        String jpql = "SELECT a FROM AuditTrail a ORDER BY a.timestamp DESC";
        TypedQuery<AuditTrail> query = entityManager.createQuery(jpql, AuditTrail.class);
        return query.getResultList();
    }

    // Get audit trails by entity type
    public List<AuditTrail> getAuditTrailsByEntityType(String entityType) {
        String jpql = "SELECT a FROM AuditTrail a WHERE a.entityType = :entityType ORDER BY a.timestamp DESC";
        TypedQuery<AuditTrail> query = entityManager.createQuery(jpql, AuditTrail.class);
        query.setParameter("entityType", entityType);
        return query.getResultList();
    }

    // Get audit trails by entity id
    public List<AuditTrail> getAuditTrailsByEntityId(String entityId) {
        String jpql = "SELECT a FROM AuditTrail a WHERE a.entityId = :entityId ORDER BY a.timestamp DESC";
        TypedQuery<AuditTrail> query = entityManager.createQuery(jpql, AuditTrail.class);
        query.setParameter("entityId", entityId);
        return query.getResultList();
    }
}