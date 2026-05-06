package app.dao;

import app.dbconnection.DataSourceHelper;
import app.model.User;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.sql.*;


@Dependent
public class UserDAO extends GenericDAO<User, String> {

    private final DataSourceHelper dataSourceHelper;

    @Inject
    public UserDAO(DataSourceHelper dataSourceHelper) {
        super(User.class, dataSourceHelper);
        this.dataSourceHelper = dataSourceHelper;
    }

    /**
     * Custom Query - Find user by username
     */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error finding user by username", e);
        }

        return null;
    }

    /**
     * Create User - with ID generation
     */
    public void addUser(User user) throws SQLException {
        String sql = """
            INSERT INTO users (username, password, role, email, status)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getStatus() != null ? user.getStatus() : "Active");

            int result = stmt.executeUpdate();

            if (result > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(String.valueOf(keys.getInt(1)));
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error adding user", e);
        }
    }

    /**
     * Read User by ID
     */
    public User getUser(String id) throws SQLException {
        return findById(id);
    }

    /**
     * Update User
     */
    public void updateUser(String id, User user) throws SQLException {
        String sql = """
            UPDATE users
            SET username=?, password=?, role=?, email=?, status=?, updated_at=CURRENT_TIMESTAMP
            WHERE id=?
        """;

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getStatus() != null ? user.getStatus() : "Active");
            stmt.setInt(6, Integer.parseInt(id));

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("Error updating user", e);
        }
    }

    /**
     * Delete User
     */
    public void deleteUser(String id) throws SQLException {
        delete(id);
    }

    /**
     * Read All Users
     */
    public List<User> getAllUsers() throws SQLException {
        return findAll();
    }

     /**
      * Count Total Users
      */
     public int getTotalUsers() throws SQLException {
         return count();
     }

     /**
      * Check if user exists by ID
      */
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