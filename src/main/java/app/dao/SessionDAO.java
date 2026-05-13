package app.dao;

import app.dbconnection.DataSourceHelper;
import app.model.Session;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class SessionDAO extends GenericDAO<Session, String> {

    @Inject
    public SessionDAO(DataSourceHelper dataSourceHelper) {
        super(Session.class, dataSourceHelper);
    }

    // Add session
    public void addSession(Session session) throws SQLException {
        save(session);
    }

    // Find session by id
    public Session getSession(String id) throws SQLException {
        return findById(id);
    }

    // Find all sessions
    public List<Session> getAllSessions() throws SQLException {
        return findAll();
    }

    // Update session
    public void updateSession(String id, Session session) throws SQLException {
        session.setId(id);
        update(session);
    }

    // Delete session
    public void deleteSession(String id) throws SQLException {
        delete(id);
    }

    // Get total number of sessions
    public int getTotalSessions() throws SQLException {
        return count();
    }

    // Get sessions for a mentor
    public List<Session> getSessionsByMentor(String mentorId) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions WHERE mentor_id = ? ORDER BY scheduled_date DESC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mentorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting sessions by mentor", e);
        }
        return sessions;
    }

    // Get sessions for a mentee
    public List<Session> getSessionsByMentee(String menteeId) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions WHERE mentee_id = ? ORDER BY scheduled_date DESC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, menteeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting sessions by mentee", e);
        }
        return sessions;
    }

    // Get upcoming sessions (scheduled_date > now)
    public List<Session> getUpcomingSessions(String userId) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions WHERE (mentor_id = ? OR mentee_id = ?) " +
                    "AND scheduled_date > ? ORDER BY scheduled_date ASC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, userId);
            stmt.setLong(3, System.currentTimeMillis());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting upcoming sessions", e);
        }
        return sessions;
    }

    // Get completed sessions
    public List<Session> getCompletedSessions(String userId) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions WHERE (mentor_id = ? OR mentee_id = ?) " +
                    "AND status = 'COMPLETED' ORDER BY scheduled_date DESC";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSet(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error getting completed sessions", e);
        }
        return sessions;
    }
}
