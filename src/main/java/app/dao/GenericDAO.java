package app.dao;

import app.dbconnection.DataSourceHelper;
import app.framework.DbColumn;
import app.framework.DbTable;
import app.utility.logging.AppLogger;
import jakarta.enterprise.context.Dependent;
import org.slf4j.Logger;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;



@Dependent
public class GenericDAO<T, ID> {

    private static final Logger logger = AppLogger.getLogger(GenericDAO.class);

    private final Class<T> entityClass;
    private final String tableName;
    private final List<Field> columns = new ArrayList<>();
    private Field idField;

    protected DataSourceHelper dataSourceHelper;


    public GenericDAO(Class<T> entityClass, DataSourceHelper dataSourceHelper) {
        this.entityClass = entityClass;
        this.dataSourceHelper = dataSourceHelper;

        // Check for @DbTable annotation
        if (!entityClass.isAnnotationPresent(DbTable.class)) {
            throw new RuntimeException("Missing @DbTable annotation on " + entityClass.getName());
        }

        // Get table name from annotation
        this.tableName = entityClass.getAnnotation(DbTable.class).name();

        // Scan all fields for @DbColumn annotations
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DbColumn.class)) {
                field.setAccessible(true);
                columns.add(field);

                // Find the primary key field
                if (field.getAnnotation(DbColumn.class).primaryKey()) {
                    idField = field;
                }
            }
        }

        // Validate that a primary key is defined
        if (idField == null) {
            throw new RuntimeException("No primary key defined in " + entityClass.getName());
        }
    }

    /**
     * CREATE - Save a new entity
     */
    public void save(T entity) throws SQLException {
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement ps = buildInsertStatement(conn, entity)) {

            int index = 1;
            for (Field field : columns) {
                DbColumn col = field.getAnnotation(DbColumn.class);
                if (col.autoIncrement()) continue;

                ps.setObject(index++, field.get(entity));
            }

            ps.executeUpdate();
            logger.info("[{}DAO] Entity saved successfully", entityClass.getSimpleName());

        } catch (IllegalAccessException e) {
            throw new SQLException("Error accessing field values", e);
        }
    }

    /**
     * READ - Find entity by ID
     */
    public T findById(ID id) throws SQLException {
        try (Connection conn = dataSourceHelper.getConnection()) {

            String idColumnName = idField.getAnnotation(DbColumn.class).name();
            String sql = "SELECT * FROM " + tableName + " WHERE " + idColumnName + " = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            
            // Convert ID to appropriate type for database query
            Object idValue = convertIdValue(id, idField.getAnnotation(DbColumn.class).type());
            ps.setObject(1, idValue);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (Exception e) {
            throw new SQLException("Error finding entity by ID", e);
        }

        return null;
    }

    /**
     * READ - Find all entities
     */
    public List<T> findAll() throws SQLException {
        List<T> list = new ArrayList<>();

        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tableName);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            throw new SQLException("Error finding all entities", e);
        }

        return list;
    }

    /**
     * UPDATE - Update an existing entity
     */
    public void update(T entity) throws SQLException {
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement ps = buildUpdateStatement(conn, entity)) {

            int index = 1;
            Object idValue = idField.get(entity);

            for (Field field : columns) {
                DbColumn col = field.getAnnotation(DbColumn.class);
                if (col.primaryKey()) continue;

                ps.setObject(index++, field.get(entity));
            }

            // Set the WHERE clause ID - convert to appropriate type
            Object convertedIdValue = convertIdValue((ID) idValue, idField.getAnnotation(DbColumn.class).type());
            ps.setObject(index, convertedIdValue);

            ps.executeUpdate();
            logger.info("[{}DAO] Entity updated successfully", entityClass.getSimpleName());

        } catch (IllegalAccessException e) {
            throw new SQLException("Error accessing field values", e);
        }
    }

    /**
     * DELETE - Delete entity by ID
     */
    public void delete(ID id) throws SQLException {
        try (Connection conn = dataSourceHelper.getConnection()) {

            String idColumnName = idField.getAnnotation(DbColumn.class).name();
            String sql = "DELETE FROM " + tableName + " WHERE " + idColumnName + " = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            
            // Convert ID to appropriate type for database query
            Object idValue = convertIdValue(id, idField.getAnnotation(DbColumn.class).type());
            ps.setObject(1, idValue);

            ps.executeUpdate();
            logger.info("[{}DAO] Entity deleted successfully", entityClass.getSimpleName());

        } catch (Exception e) {
            throw new SQLException("Error deleting entity", e);
        }
    }

    /**
     * COUNT - Get total count of entities
     */
    public int count() throws SQLException {
        try (Connection conn = dataSourceHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS count FROM " + tableName);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (Exception e) {
            throw new SQLException("Error counting entities", e);
        }

        return 0;
    }

    /**
     * Helper method to build INSERT statement
     */
    private PreparedStatement buildInsertStatement(Connection conn, T entity) throws SQLException {
        List<String> colNames = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();

        for (Field field : columns) {
            DbColumn col = field.getAnnotation(DbColumn.class);
            if (col.autoIncrement()) continue;

            colNames.add(col.name());
            placeholders.add("?");
        }

        String sql = "INSERT INTO " + tableName +
                " (" + String.join(", ", colNames) + ") VALUES (" +
                String.join(", ", placeholders) + ")";

        return conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * Helper method to build UPDATE statement
     */
    private PreparedStatement buildUpdateStatement(Connection conn, T entity) throws SQLException {
        List<String> setParts = new ArrayList<>();
        String idColumnName = idField.getAnnotation(DbColumn.class).name();

        for (Field field : columns) {
            DbColumn col = field.getAnnotation(DbColumn.class);
            if (col.primaryKey()) continue;

            setParts.add(col.name() + " = ?");
        }

        String sql = "UPDATE " + tableName +
                " SET " + String.join(", ", setParts) +
                " WHERE " + idColumnName + " = ?";

        return conn.prepareStatement(sql);
    }

    /**
     * Helper method to map ResultSet to entity instance
     */
    protected T mapResultSet(ResultSet rs) throws Exception {
        T instance = entityClass.getDeclaredConstructor().newInstance();

        for (Field field : columns) {
            DbColumn col = field.getAnnotation(DbColumn.class);
            Object value = rs.getObject(col.name());
            
            // Convert value to the correct field type
            if (value != null) {
                Class<?> fieldType = field.getType();
                if (fieldType == String.class && !(value instanceof String)) {
                    // Convert to String
                    value = String.valueOf(value);
                } else if (fieldType == Integer.class && !(value instanceof Integer)) {
                    // Convert to Integer
                    if (value instanceof Number) {
                        value = ((Number) value).intValue();
                    } else {
                        value = Integer.parseInt(String.valueOf(value));
                    }
                } else if (fieldType == int.class && !(value instanceof Integer)) {
                    // Convert to int (primitive)
                    if (value instanceof Number) {
                        value = ((Number) value).intValue();
                    } else {
                        value = Integer.parseInt(String.valueOf(value));
                    }
                } else if (fieldType == Long.class && !(value instanceof Long)) {
                    // Convert to Long
                    if (value instanceof Number) {
                        value = ((Number) value).longValue();
                    } else if (value instanceof java.sql.Timestamp) {
                        value = ((java.sql.Timestamp) value).getTime();
                    } else if (value instanceof java.util.Date) {
                        value = ((java.util.Date) value).getTime();
                    } else {
                        value = Long.parseLong(String.valueOf(value));
                    }
                } else if (fieldType == long.class && !(value instanceof Long)) {
                    // Convert to long (primitive)
                    if (value instanceof Number) {
                        value = ((Number) value).longValue();
                    } else if (value instanceof java.sql.Timestamp) {
                        value = ((java.sql.Timestamp) value).getTime();
                    } else if (value instanceof java.util.Date) {
                        value = ((java.util.Date) value).getTime();
                    } else {
                        value = Long.parseLong(String.valueOf(value));
                    }
                } else if (fieldType == Double.class && !(value instanceof Double)) {
                    // Convert to Double
                    if (value instanceof Number) {
                        value = ((Number) value).doubleValue();
                    } else {
                        value = Double.parseDouble(String.valueOf(value));
                    }
                } else if (fieldType == double.class && !(value instanceof Double)) {
                    // Convert to double (primitive)
                    if (value instanceof Number) {
                        value = ((Number) value).doubleValue();
                    } else {
                        value = Double.parseDouble(String.valueOf(value));
                    }
                } else if (fieldType == Boolean.class && !(value instanceof Boolean)) {
                    // Convert to Boolean
                    if (value instanceof Number) {
                        value = ((Number) value).intValue() != 0;
                    } else {
                        value = Boolean.parseBoolean(String.valueOf(value));
                    }
                } else if (fieldType == boolean.class && !(value instanceof Boolean)) {
                    // Convert to boolean (primitive)
                    if (value instanceof Number) {
                        value = ((Number) value).intValue() != 0;
                    } else {
                        value = Boolean.parseBoolean(String.valueOf(value));
                    }
                }
            }
            
            field.set(instance, value);
        }

        return instance;
    }

    /**
     * Helper method to convert ID value to the appropriate database type
     */
    private Object convertIdValue(ID id, String columnType) {
        if (id == null) {
            return null;
        }

        String idStr = String.valueOf(id);
        
        // Check column type and convert accordingly
        if (columnType.toUpperCase().contains("SERIAL") || columnType.toUpperCase().contains("INTEGER")) {
            try {
                return Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                return id;
            }
        } else if (columnType.toUpperCase().contains("BIGINT") || columnType.toUpperCase().contains("LONG")) {
            try {
                return Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                return id;
            }
        } else if (columnType.toUpperCase().contains("VARCHAR") || columnType.toUpperCase().contains("TEXT")) {
            return idStr;
        }
        
        return id;
    }

    // Getters
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public DataSourceHelper getDataSourceHelper() {
        return dataSourceHelper;
    }
}

