package app.dao;

import app.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class UserDAO extends GenericDAO<User, Long> {

    // Find user by username
    public User getUserByUsername(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}