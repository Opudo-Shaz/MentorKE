package app.bean;

import app.dao.AuditTrailDAO;
import app.model.AuditTrail;
import jakarta.annotation.Resource;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import app.utility.logging.AppLogger;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;
import java.sql.SQLException;

@Singleton
@Remote
public class AuditTrailBean {

    private static final Logger logger = AppLogger.getLogger(AuditTrailBean.class);

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/jms/queue/MentorKEQueue")
    private Queue auditQueue;

    @Inject
    private AuditTrailDAO auditTrailDAO;

    /**
     * Observes AuditTrail events and saves them to the database and JMS queue
     */
    public void save(@Observes AuditTrail auditTrail) {
        try {
            logger.info("=== Audit Trail Event Observed ===");

            // Add timestamp to details
            String detailedInfo = new Date() + ": " + auditTrail.getDetails();
            auditTrail.setDetails(detailedInfo);

            // Save to database
            auditTrailDAO.addAuditTrail(auditTrail);
            logger.info("Audit recorded to database. ID: {}", auditTrail.getId());

            // Send to JMS queue as producer
            context.createProducer().send(auditQueue, detailedInfo);
            logger.info("Audit message sent to JMS queue");

        } catch (SQLException e) {
            logger.error("Error saving audit trail: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all audit trails
     */
    public List<AuditTrail> list(AuditTrail filter) {
        try {
            return auditTrailDAO.getAllAuditTrails();
        } catch (SQLException e) {
            logger.error("Error retrieving audit trails: {}", e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}