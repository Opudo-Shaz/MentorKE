package app.dao;

import app.model.AuditTrail;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class AuditTrailDAO extends GenericDAO<AuditTrail, Long> {

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