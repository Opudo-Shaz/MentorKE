package app.dao;

import app.utility.logging.AppLogger;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;

import java.util.List;


@Dependent
public class GenericDAO<T, ID> {

    private static final Logger logger = AppLogger.getLogger(GenericDAO.class);

    @PersistenceContext(unitName = "MentorKEPU")
    protected EntityManager entityManager;

    private final Class<T> entityClass;


    public GenericDAO() {
        this.entityClass = null;
    }

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * CREATE - Save a new entity
     */
    public void save(T entity) {
        entityManager.persist(entity);
        assert entityClass != null;
        logger.info("[{}DAO] Entity saved successfully", entityClass.getSimpleName());
    }

    /**
     * READ - Find entity by ID
     */
    public T findById(ID id) {
        return entityManager.find(entityClass, id);
    }

    /**
     * READ - Find all entities
     */
    public List findAll() {
        assert entityClass != null;
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        Query query = entityManager.createQuery(jpql);
        return query.getResultList();
    }

    /**
     * UPDATE - Update an existing entity
     */
    public void update(T entity) {
        entityManager.merge(entity);
        assert entityClass != null;
        logger.info("[{}DAO] Entity updated successfully", entityClass.getSimpleName());
    }

    /**
     * DELETE - Delete entity by ID
     */
    public void delete(ID id) {
        T entity = findById(id);
        if (entity != null) {
            entityManager.remove(entity);
            assert entityClass != null;
            logger.info("[{}DAO] Entity deleted successfully", entityClass.getSimpleName());
        }
    }

    /**
     * COUNT - Get total count of entities
     */
    public int count() {
        assert entityClass != null;
        String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
        Query query = entityManager.createQuery(jpql);
        return ((Number) query.getSingleResult()).intValue();
    }

    // Getters
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}

