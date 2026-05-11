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

    @Inject
    public AuditTrailDAO(DataSourceHelper dataSourceHelper) {
        super(AuditTrail.class, dataSourceHelper);
    }

    // ✅ Delegates to inherited save()
    public void addAuditTrail(AuditTrail audit) throws SQLException {
        save(audit);
    }

    // ✅ Delegates to inherited findById()
    public AuditTrail getAuditTrail(String id) throws SQLException {
        return findById(id);
    }

    // ✅ Delegates to inherited count()
    public int getTotalAuditTrails() throws SQLException {
        return count();
    }

    //  Keep — custom ORDER BY, GenericDAO.findAll() has no ordering
    public List<AuditTrail> getAllAuditTrails() throws SQLException {
        List<AuditTrail> audits = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail ORDER BY timestamp DESC";
        try (Connection conn = dataSourceHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) audits.add(mapResultSet(rs));
        } catch (Exception e) {
            throw new SQLException("Error getting all audit trails", e);
        }
        return audits;
    }

    // Keep — custom WHERE clause
    public List<AuditTrail> getAuditTrailsByEntityType(String entityType) throws SQLException {
        List<AuditTrail> audits = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail WHERE entity_type = ? ORDER BY timestamp DESC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) audits.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            throw new SQLException("Error getting audit trails by entity type", e);
        }
        return audits;
    }

    //  Keep — custom WHERE clause
    public List<AuditTrail> getAuditTrailsByEntityId(String entityId) throws SQLException {
        List<AuditTrail> audits = new ArrayList<>();
        String sql = "SELECT * FROM audit_trail WHERE entity_id = ? ORDER BY timestamp DESC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) audits.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            throw new SQLException("Error getting audit trails by entity ID", e);
        }
        return audits;
    }
}