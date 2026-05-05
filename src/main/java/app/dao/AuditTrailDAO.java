package app.dao;

import app.dbconnection.DataSourceHelper;
import app.model.AuditTrail;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Dependent
public class AuditTrailDAO extends GenericDAO<AuditTrail, String> {

    private final DataSourceHelper dataSourceHelper;

    @Inject
    public AuditTrailDAO(DataSourceHelper dataSourceHelper) {
        super(AuditTrail.class, dataSourceHelper);
        this.dataSourceHelper = dataSourceHelper;
    }

    /**
     * CREATE - Add audit trail record
     */
    public void addAuditTrail(AuditTrail audit) throws SQLException {
        String sql = """
            INSERT INTO audit_trail (entity_type, entity_id, operation, user_id, details, timestamp)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, audit.getEntityType());
            stmt.setString(2, audit.getEntityId());
            stmt.setString(3, audit.getOperation());
            stmt.setString(4, audit.getUserId());
            stmt.setString(5, audit.getDetails());
            stmt.setLong(6, audit.getTimestamp());

            int result = stmt.executeUpdate();

            if (result > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        audit.setId(String.valueOf(keys.getInt(1)));
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error adding audit trail", e);
        }
    }

    /**
     * READ - Get audit trail by ID
     */
    public AuditTrail getAuditTrail(String id) throws SQLException {
        return findById(id);
    }

    /**
     * READ - Get all audit trails
     */
    public List<AuditTrail> getAllAuditTrails() throws SQLException {
        List<AuditTrail> audits = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail ORDER BY timestamp DESC";

        try (Connection conn = dataSourceHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                audits.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            throw new SQLException("Error getting all audit trails", e);
        }

        return audits;
    }

    /**
     * READ - Get audit trails by entity type
     */
    public List<AuditTrail> getAuditTrailsByEntityType(String entityType) throws SQLException {
        List<AuditTrail> audits = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail WHERE entity_type = ? ORDER BY timestamp DESC";

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entityType);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting audit trails by entity type", e);
        }

        return audits;
    }

    /**
     * READ - Get audit trails by entity ID
     */
    public List<AuditTrail> getAuditTrailsByEntityId(String entityId) throws SQLException {
        List<AuditTrail> audits = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail WHERE entity_id = ? ORDER BY timestamp DESC";

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entityId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting audit trails by entity ID", e);
        }

        return audits;
    }

    /**
     * COUNT - Total audit trails
     */
    public int getTotalAuditTrails() throws SQLException {
        return count();
    }
}

