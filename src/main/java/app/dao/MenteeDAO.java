package app.dao;

import app.dbconnection.DataSourceHelper;
import app.model.Mentee;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class MenteeDAO extends GenericDAO<Mentee, String> {

    @Inject
    public MenteeDAO(DataSourceHelper dataSourceHelper) {
        super(Mentee.class, dataSourceHelper);
    }

    // ✅ Use inherited save() instead of custom addMentee()
    public void addMentee(Mentee mentee) throws SQLException {
        save(mentee);
    }

    // ✅ Use inherited findById()
    public Mentee getMentee(String id) throws SQLException {
        return findById(id);
    }

    // ✅ Use inherited findAll()
    public List<Mentee> getAllMentees() throws SQLException {
        return findAll();
    }

    // ✅ Use inherited update()
    public void updateMentee(String id, Mentee mentee) throws SQLException {
        mentee.setId(id);
        update(mentee);
    }

    // ✅ Use inherited delete()
    public void deleteMentee(String id) throws SQLException {
        delete(id);
    }

    // ✅ Use inherited count()
    public int getTotalMentees() throws SQLException {
        return count();
    }

    // ⚠️ Keep these — they use custom WHERE clauses GenericDAO can't handle
    public Mentee getMenteeByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM mentees WHERE user_id = ?";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(userId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (Exception e) {
            throw new SQLException("Error finding mentee by user ID", e);
        }
        return null;
    }

    public List<Mentee> getActiveMentees() throws SQLException {
        List<Mentee> list = new ArrayList<>();
        String sql = "SELECT * FROM mentees WHERE status = 'Active' ORDER BY id ASC";
        try (Connection conn = dataSourceHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (Exception e) {
            throw new SQLException("Error getting active mentees", e);
        }
        return list;
    }

    public List<Mentee> getMenteesWithoutMentor() throws SQLException {
        List<Mentee> list = new ArrayList<>();
        String sql = """
            SELECT * FROM mentees
            WHERE mentor_id IS NULL AND status = 'Active'
            ORDER BY id ASC
        """;
        try (Connection conn = dataSourceHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (Exception e) {
            throw new SQLException("Error getting mentees without mentor", e);
        }
        return list;
    }

    public List<Mentee> getMenteesByMentorId(String mentorId) throws SQLException {
        List<Mentee> mentees = new ArrayList<>();
        String sql = "SELECT * FROM mentees WHERE mentor_id = ?";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mentorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) mentees.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            throw new SQLException("Error getting mentees by mentor ID", e);
        }
        return mentees;
    }
}