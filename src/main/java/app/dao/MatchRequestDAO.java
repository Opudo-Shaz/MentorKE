package app.dao;

import app.dbconnection.DataSourceHelper;
import app.model.MatchRequest;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class MatchRequestDAO extends GenericDAO<MatchRequest, String> {

    @Inject
    public MatchRequestDAO(DataSourceHelper dataSourceHelper) {
        super(MatchRequest.class, dataSourceHelper);
    }

    // Add match request
    public void addMatchRequest(MatchRequest matchRequest) throws SQLException {
        save(matchRequest);
    }

    // Find match request by id
    public MatchRequest getMatchRequest(String id) throws SQLException {
        return findById(id);
    }

    // Find all match requests
    public List<MatchRequest> getAllMatchRequests() throws SQLException {
        return findAll();
    }

    // Update match request
    public void updateMatchRequest(String id, MatchRequest matchRequest) throws SQLException {
        matchRequest.setId(id);
        update(matchRequest);
    }

    // Delete match request
    public void deleteMatchRequest(String id) throws SQLException {
        delete(id);
    }

    // Get total number of match requests
    public int getTotalMatchRequests() throws SQLException {
        return count();
    }

    // Get pending match requests for a mentor
    public List<MatchRequest> getPendingRequestsForMentor(String mentorId) throws SQLException {
        List<MatchRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM match_requests WHERE mentor_id = ? AND status = 'PENDING' " +
                    "ORDER BY created_at DESC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mentorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting pending requests for mentor", e);
        }
        return requests;
    }

    // Get match requests by mentee
    public List<MatchRequest> getRequestsByMentee(String menteeId) throws SQLException {
        List<MatchRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM match_requests WHERE mentee_id = ? ORDER BY created_at DESC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, menteeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting requests by mentee", e);
        }
        return requests;
    }

    // Get approved/matched requests for a mentee
    public MatchRequest getApprovedMatchForMentee(String menteeId) throws SQLException {
        String sql = "SELECT * FROM match_requests WHERE mentee_id = ? AND status = 'APPROVED' " +
                    "LIMIT 1";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, menteeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting approved match for mentee", e);
        }
        return null;
    }

    // Get pending requests (not yet assigned to a mentor)
    public List<MatchRequest> getPendingUnassignedRequests() throws SQLException {
        List<MatchRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM match_requests WHERE status = 'PENDING' AND mentor_id IS NULL " +
                    "ORDER BY created_at ASC";
        try (Connection conn = dataSourceHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                requests.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            throw new SQLException("Error getting pending unassigned requests", e);
        }
        return requests;
    }
}
