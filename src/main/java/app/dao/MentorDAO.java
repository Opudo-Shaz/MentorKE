package app.dao;

import app.dbconnection.DataSourceHelper;
import app.model.Mentor;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class MentorDAO extends GenericDAO<Mentor, String> {

    @Inject
    public MentorDAO(DataSourceHelper dataSourceHelper) {
        super(Mentor.class, dataSourceHelper);
    }

    // Add mentor
    public void addMentor(Mentor mentor) throws SQLException {
        save(mentor);
    }

    // Find mentor by id
    public Mentor getMentor(String id) throws SQLException {
        return findById(id);
    }

    // Find all mentors
    public List<Mentor> getAllMentors() throws SQLException {
        return findAll();
    }

    // Update mentor
    public void updateMentor(String id, Mentor mentor) throws SQLException {
        mentor.setId(id);
        update(mentor);
    }
    
    // Delete mentor
    public void deleteMentor(String id) throws SQLException {
        delete(id);
    }

    // Get total number of mentors
    public int getTotalMentors() throws SQLException {
        return count();
    }

    // Keep — custom WHERE clause
    public Mentor getMentorByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM mentors WHERE user_id = ?";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(userId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (Exception e) {
            throw new SQLException("Error finding mentor by user ID", e);
        }
        return null;
    }

    // custom WHERE clause
    public List<Mentor> getActiveMentors() throws SQLException {
        List<Mentor> mentors = new ArrayList<>();
        String sql = "SELECT * FROM mentors WHERE status = 'Active' ORDER BY specialization ASC";
        try (Connection conn = dataSourceHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) mentors.add(mapResultSet(rs));
        } catch (Exception e) {
            throw new SQLException("Error getting active mentors", e);
        }
        return mentors;
    }
}