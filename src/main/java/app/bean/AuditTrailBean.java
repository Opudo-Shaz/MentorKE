package app.bean;

import app.bean.event.CRUDEvent;
import app.dao.AuditTrailDAO;
import app.model.AuditTrail;

import jakarta.ejb.Singleton;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.sql.SQLException;

@Singleton
@Lock(LockType.WRITE)
@Named("auditTrailBean")
public class AuditTrailBean {

    private AuditTrailDAO auditTrailDAO;

    public AuditTrailBean() {
        // Required no-arg constructor for EJB
    }

    @Inject
    public void setAuditTrailDAO(AuditTrailDAO auditTrailDAO) {
        this.auditTrailDAO = auditTrailDAO;
    }

    /**
     * Observes CRUD events fired from other beans
     */
    public void observeCRUDEvent(@Observes CRUDEvent event) {
        System.out.println("[AuditTrailBean] === CRUD Event Observed ===");
        System.out.println("[AuditTrailBean] Entity: " + event.getEntityType() +
                ", Operation: " + event.getOperation() +
                ", Entity ID: " + event.getEntityId());

        try {
            AuditTrail audit = new AuditTrail(
                    event.getEntityType(),
                    event.getEntityId(),
                    event.getOperation(),
                    event.getUserId(),
                    event.getDetails()
            );

            auditTrailDAO.addAuditTrail(audit);

            System.out.println("[AuditTrailBean] Audit recorded. ID: " + audit.getId());

        } catch (SQLException e) {
            System.err.println("[AuditTrailBean] Error saving audit trail: " + e.getMessage());
            e.printStackTrace();
        }
    }
}