package app.dao;

import app.dbconnection.DataSourceHelper;
import app.model.User;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.List;

@Dependent
public class UserDAO extends GenericDAO<User, String> {

    @Inject
    public UserDAO(DataSourceHelper dataSourceHelper) {
        super(User.class, dataSourceHelper);
    }

    // ✅ Delegates to inherited save()
    public void addUser(User user) throws SQLException {
        save(user);
    }

    // ✅ Delegates to inherited findById()
    public User getUser(String id) throws SQLException {
        return findById(id);
    }

    // ✅ Delegates to inherited update()
    public void updateUser(String id, User user) throws SQLException {
        user.setId(id);
        update(user);
    }

    // ✅ Delegates to inherited delete()
    public void deleteUser(String id) throws SQLException {
        delete(id);
    }

    // ✅ Delegates to inherited findAll()
    public List<User> getAllUsers() throws SQLException {
        return findAll();
    }

    // ✅ Delegates to inherited count()
    public int getTotalUsers() throws SQLException {
        return count();
    }

    // ⚠️ Keep — custom WHERE clause
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (Exception e) {
            throw new SQLException("Error finding user by username", e);
        }
        return null;
    }

    // ⚠️ Keep — custom SELECT 1 existence check
    public boolean exists(String id) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new SQLException("Error checking if user exists", e);
        }
    }
}