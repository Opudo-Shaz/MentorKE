package app.dao;

import app.model.User;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Dependent
public class UserDAO extends GenericDAO<User, Long> {

    public UserDAO() {
        super(User.class);
    }

    // Add user
    public void addUser(User user) {
        save(user);
    }

    // Find user by id
    public User getUser(String id) {
        return findById(Long.parseLong(id));
    }

    // Update user
    public void updateUser(String id, User user) {
        user.setId(Long.parseLong(id));
        update(user);
    }

    // Delete user
    public void deleteUser(String id) {
        delete(Long.parseLong(id));
    }

    // Find all users
    public List<User> getAllUsers() {
        return findAll();
    }

    // Get total number of users
    public int getTotalUsers() {
        return count();
    }

    // Find user by username
    public User getUserByUsername(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // Check if user exists
    public boolean exists(String id) {
        User user = findById(Long.parseLong(id));
        return user != null;
    }
}