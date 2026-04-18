package app;

import app.dbconnection.Connection;
import app.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {

    private java.sql.Connection conn;

    public UserDAO() throws SQLException {
        this.conn = Connection.getInstance().getConnection();
    }


     // Add a new user to the database

    public void addUser(User user) throws SQLException {
        String sql = generateInsertQuery();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getStatus() != null ? user.getStatus() : "Active");

            int result = stmt.executeUpdate();
            if (result > 0) {
                System.out.println("[UserDAO] User '" + user.getUsername() + "' added successfully");
                Connection.getInstance().insertLog(null, "USER_ADDED", "Username: " + user.getUsername());

                // Get generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error adding user: " + e.getMessage());
            throw e;
        }
    }


     //Get user by username
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[UserDAO] User found: " + username);
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error fetching user by username: " + e.getMessage());
        }
        System.out.println("[UserDAO] User not found: " + username);
        return null;
    }


     //Get user by ID

    public User getUser(String id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error fetching user by ID: " + e.getMessage());
            throw e;
        }
        return null;
    }


     //Update user

    public void updateUser(String id, User user) throws SQLException {
        String sql = generateUpdateQuery();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getStatus() != null ? user.getStatus() : "Active");
            stmt.setInt(6, Integer.parseInt(id));

            int result = stmt.executeUpdate();
            if (result > 0) {
                System.out.println("[UserDAO] User ID " + id + " updated successfully");
                Connection.getInstance().insertLog(Integer.parseInt(id), "USER_UPDATED", "User: " + user.getUsername());
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error updating user: " + e.getMessage());
            throw e;
        }
    }


    //Delete user by ID

    public void deleteUser(String id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            int result = stmt.executeUpdate();
            if (result > 0) {
                System.out.println("[UserDAO] User ID " + id + " deleted successfully");
                Connection.getInstance().insertLog(Integer.parseInt(id), "USER_DELETED", "User ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error deleting user: " + e.getMessage());
            throw e;
        }
    }


    //Get all users from database

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id ASC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            System.out.println("[UserDAO] Retrieved " + users.size() + " users from database");
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error fetching all users: " + e.getMessage());
            throw e;
        }

        return users;
    }


     //Get all users

    public int getTotalUsers() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error getting total users: " + e.getMessage());
            throw e;
        }
        return 0;
    }

    /**
     * Generate INSERT query dynamically
     */
    private String generateInsertQuery() {
        return "INSERT INTO users (username, password, role, email, status) VALUES (?, ?, ?, ?, ?)";
    }

    /**
     * Generate UPDATE query dynamically
     */
    private String generateUpdateQuery() {
        return "UPDATE users SET username=?, password=?, role=?, email=?, status=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
    }

    /**
     * Generate SELECT query dynamically
     */
    private String generateSelectQuery(String... columns) {
        StringBuilder sql = new StringBuilder("SELECT ");
        if (columns.length > 0) {
            for (int i = 0; i < columns.length; i++) {
                sql.append(columns[i]);
                if (i < columns.length - 1) {
                    sql.append(", ");
                }
            }
        } else {
            sql.append("*");
        }
        sql.append(" FROM users");
        return sql.toString();
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                String.valueOf(rs.getInt("id")),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getString("email"),
                rs.getString("status")
        );
    }
}