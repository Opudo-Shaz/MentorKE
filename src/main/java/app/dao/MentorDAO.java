package app.dao;

import app.dbconnection.DataSourceHelper;
import app.model.Mentor;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MentorDAO - Data Access Object for Mentor entities
 *
 * Extends GenericDAO to inherit basic CRUD operations
 * Adds custom queries specific to Mentor entity
 *
 * Uses @Dependent scope:
 * - DAO is stateless (no shared state)
 * - All operations are method-level
 * - Each injection point gets a new instance (lightweight)
 */
@Dependent
public class MentorDAO extends GenericDAO<Mentor, String> {

    private final DataSourceHelper dataSourceHelper;

    @Inject
    public MentorDAO(DataSourceHelper dataSourceHelper) {
        super(Mentor.class, dataSourceHelper);
        this.dataSourceHelper = dataSourceHelper;
    }

    /**
     * CREATE - Add a new mentor
     */
    public void addMentor(Mentor mentor) throws SQLException {
        String sql = """
            INSERT INTO mentors
            (user_id, specialization, expertise, years_of_experience, bio,
             qualifications, phone_number, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, Integer.parseInt(mentor.getUserId()));
            stmt.setString(2, mentor.getSpecialization());
            stmt.setString(3, mentor.getExpertise());
            stmt.setInt(4, mentor.getYearsOfExperience() != null ? mentor.getYearsOfExperience() : 0);
            stmt.setString(5, mentor.getBio());
            stmt.setString(6, mentor.getQualifications());
            stmt.setString(7, mentor.getPhoneNumber());
            stmt.setString(8, mentor.getStatus() != null ? mentor.getStatus() : "Active");

            int result = stmt.executeUpdate();

            if (result > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        mentor.setId(String.valueOf(keys.getInt(1)));
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error adding mentor", e);
        }
    }

    /**
     * READ - Get mentor by ID
     */
    public Mentor getMentor(String id) throws SQLException {
        return findById(id);
    }

    /**
     * READ - Get mentor by user ID
     */
    public Mentor getMentorByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM mentors WHERE user_id = ?";

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(userId));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error finding mentor by user ID", e);
        }

        return null;
    }

    /**
     * READ - Get all mentors
     */
    public List<Mentor> getAllMentors() throws SQLException {
        return findAll();
    }

    /**
     * READ - Get active mentors
     */
    public List<Mentor> getActiveMentors() throws SQLException {
        List<Mentor> mentors = new ArrayList<>();
        String sql = "SELECT * FROM mentors WHERE status = 'Active' ORDER BY specialization ASC";

        try (Connection conn = dataSourceHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                mentors.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            throw new SQLException("Error getting active mentors", e);
        }

        return mentors;
    }

    /**
     * UPDATE - Update mentor
     */
    public void updateMentor(String id, Mentor mentor) throws SQLException {
        String sql = """
            UPDATE mentors
            SET specialization=?,
                expertise=?,
                years_of_experience=?,
                bio=?,
                qualifications=?,
                phone_number=?,
                status=?,
                updated_at=CURRENT_TIMESTAMP
            WHERE id=?
        """;

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mentor.getSpecialization());
            stmt.setString(2, mentor.getExpertise());
            stmt.setInt(3, mentor.getYearsOfExperience() != null ? mentor.getYearsOfExperience() : 0);
            stmt.setString(4, mentor.getBio());
            stmt.setString(5, mentor.getQualifications());
            stmt.setString(6, mentor.getPhoneNumber());
            stmt.setString(7, mentor.getStatus() != null ? mentor.getStatus() : "Active");
            stmt.setInt(8, Integer.parseInt(id));

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("Error updating mentor", e);
        }
    }

    /**
     * DELETE - Delete mentor
     */
    public void deleteMentor(String id) throws SQLException {
        delete(id);
    }

    /**
     * COUNT - Total mentors
     */
    public int getTotalMentors() throws SQLException {
        return count();
    }
}