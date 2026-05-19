package app.dao;

import app.utility.logging.AppLogger;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;


@Dependent
public class GenericDAO<T, I> {

    private static final Logger logger = AppLogger.getLogger(GenericDAO.class);

    @PersistenceContext(unitName = "MentorKEPU")
    protected EntityManager entityManager;

    /**
     * CREATE - Save a new entity
     */
    public void save(T entity) {
        entityManager.persist(entity);
        logger.info("[{}DAO] Entity saved successfully", getType().getSimpleName());
    }

    /**
     * READ - Find entity by ID
     */
    public T findById(I id) {
        return entityManager.find(getType(), id);
    }

    /**
     * READ - Find all entities
     */
    public List<T> findAll() {
        String jpql = "SELECT e FROM " + getType().getSimpleName() + " e";
        TypedQuery<T> query = entityManager.createQuery(jpql, getType());
        return query.getResultList();
    }

    /**
     * UPDATE - Update an existing entity
     */
    public void update(T entity) {
        entityManager.merge(entity);
        logger.info("[{}DAO] Entity updated successfully", getType().getSimpleName());
    }

    /**
     * DELETE - Delete entity by ID
     */
    public void delete(I id) {
        T entity = findById(id);
        if (entity != null) {
            entityManager.remove(entity);
            logger.info("[{}DAO] Entity deleted successfully", getType().getSimpleName());
        }
    }

    /**
     * COUNT - Get total count of entities
     */
    public int count() {
        String jpql = "SELECT COUNT(e) FROM " + getType().getSimpleName() + " e";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult().intValue();
    }

    // Getters
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        Class<?> currentClass = getClass();

        while (currentClass != null && currentClass != Object.class) {
            Type genericSuperclass = currentClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType parameterizedType) {
                return (Class<T>) parameterizedType.getActualTypeArguments()[0];
            }

            currentClass = currentClass.getSuperclass();
        }

        throw new IllegalStateException("Unable to determine entity type for " + getClass().getName());
    }
}

