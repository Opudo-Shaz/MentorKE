package app.dao;

import app.dbconnection.DataSourceHelper;
import app.model.Mentee;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MenteeDAO - Data Access Object for Mentee entities
 *
 * Extends GenericDAO to inherit basic CRUD operations
 * Adds custom queries specific to Mentee entity
 *
 * Uses @Dependent scope for stateless operations
 */
@Dependent
public class MenteeDAO extends GenericDAO<Mentee, String> {

    private final DataSourceHelper dataSourceHelper;

    @Inject
    public MenteeDAO(DataSourceHelper dataSourceHelper) {
        super(Mentee.class, dataSourceHelper);
        this.dataSourceHelper = dataSourceHelper;
    }

    /**
     * CREATE - Add a new mentee
     */
    public void addMentee(Mentee mentee) throws SQLException {
        String sql = """
            INSERT INTO mentees
            (user_id, education_level, field_of_study, learning_goals, phone_number, mentor_id, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, Integer.parseInt(mentee.getUserId()));
            stmt.setString(2, mentee.getEducationLevel());
            stmt.setString(3, mentee.getFieldOfStudy());
            stmt.setString(4, mentee.getLearningGoals());
            stmt.setString(5, mentee.getPhoneNumber());

            if (mentee.getMentorId() != null) {
                stmt.setInt(6, Integer.parseInt(mentee.getMentorId()));
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, mentee.getStatus() != null ? mentee.getStatus() : "Active");

            int result = stmt.executeUpdate();

            if (result > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        mentee.setId(String.valueOf(keys.getInt(1)));
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error adding mentee", e);
        }
    }

    /**
     * READ - Get mentee by ID
     */
    public Mentee getMentee(String id) throws SQLException {
        return findById(id);
    }

    /**
     * READ - Get mentee by user ID
     */
    public Mentee getMenteeByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM mentees WHERE user_id = ?";

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(userId));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error finding mentee by user ID", e);
        }

        return null;
    }

    /**
     * READ - Get all mentees
     */
    public List<Mentee> getAllMentees() throws SQLException {
        return findAll();
    }

    /**
     * READ - Get active mentees
     */
    public List<Mentee> getActiveMentees() throws SQLException {
        List<Mentee> list = new ArrayList<>();
        String sql = "SELECT * FROM mentees WHERE status = 'Active' ORDER BY id ASC";

        try (Connection conn = dataSourceHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            throw new SQLException("Error getting active mentees", e);
        }

        return list;
    }

    /**
     * READ - Get mentees without mentor
     */
    public List<Mentee> getMenteesWithoutMentor() throws SQLException {
        List<Mentee> list = new ArrayList<>();
        String sql = """
            SELECT * FROM mentees
            WHERE mentor_id IS NULL
            AND status = 'Active'
            ORDER BY id ASC
        """;

        try (Connection conn = dataSourceHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            throw new SQLException("Error getting mentees without mentor", e);
        }

        return list;
    }

    /**
     * UPDATE - Update mentee
     */
    public void updateMentee(String id, Mentee mentee) throws SQLException {
        String sql = """
            UPDATE mentees
            SET education_level = ?,
                field_of_study = ?,
                learning_goals = ?,
                phone_number = ?,
                mentor_id = ?,
                status = ?
            WHERE id = ?
        """;

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mentee.getEducationLevel());
            stmt.setString(2, mentee.getFieldOfStudy());
            stmt.setString(3, mentee.getLearningGoals());
            stmt.setString(4, mentee.getPhoneNumber());

            if (mentee.getMentorId() != null) {
                stmt.setInt(5, Integer.parseInt(mentee.getMentorId()));
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, mentee.getStatus());
            stmt.setInt(7, Integer.parseInt(id));

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("Error updating mentee", e);
        }
    }

    /**
     * DELETE - Delete mentee
     */
    public void deleteMentee(String id) throws SQLException {
        delete(id);
    }

    /**
     * COUNT - Total mentees
     */
    public int getTotalMentees() throws SQLException {
        return count();
    }
}