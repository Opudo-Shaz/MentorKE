package app.model;

import app.framework.DbTable;
import app.framework.DbColumn;
import java.io.Serial;
import java.io.Serializable;


@DbTable(name = "users")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @DbColumn(name = "id", type = "SERIAL", primaryKey = true, autoIncrement = true)
    private String id;

    @DbColumn(name = "username", type = "VARCHAR(100)", notNull = true, unique = true)
    private String username;

    @DbColumn(name = "password", type = "VARCHAR(255)", notNull = true)
    private String password;

    @DbColumn(name = "role", type = "VARCHAR(50)", notNull = true)
    private String role;

    @DbColumn(name = "email", type = "VARCHAR(150)", notNull = true, unique = true)
    private String email;

    @DbColumn(name = "status", type = "VARCHAR(50)", defaultValue = "'Active'")
    private String status;

    @DbColumn(name = "created_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long createdAt;

    @DbColumn(name = "updated_at", type = "TIMESTAMP", defaultValue = "CURRENT_TIMESTAMP")
    private long updatedAt;

    public User() {
    }

    public User(String id, String username, String password, String role, String email, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

