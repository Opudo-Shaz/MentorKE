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
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.sql.SQLException;

@Singleton
@Lock(LockType.WRITE)
@Named("auditTrailBean")
public class AuditTrailBean {

    private static final Logger logger = AppLogger.getLogger(AuditTrailBean.class);

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
        logger.info("=== CRUD Event Observed ===");
        logger.info("Entity: {}, Operation: {}, Entity ID: {}", event.getEntityType(), event.getOperation(), event.getEntityId());

        try {
            AuditTrail audit = new AuditTrail(
                    event.getEntityType(),
                    event.getEntityId(),
                    event.getOperation(),
                    event.getUserId(),
                    event.getDetails()
            );

            auditTrailDAO.addAuditTrail(audit);

            logger.info("Audit recorded. ID: {}", audit.getId());

        } catch (SQLException e) {
            logger.error("Error saving audit trail: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}