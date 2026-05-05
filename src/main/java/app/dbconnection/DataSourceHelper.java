package app.dbconnection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@ApplicationScoped
public class DataSourceHelper {

    private HikariDataSource dataSource;

    private static final String HOST = "localhost";
    private static final int PORT = 5432;
    private static final String DB_NAME = "mentorke_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "12345@NOT";

    public DataSourceHelper() {
        initializeDataSource();
        System.out.println("[DataSourceHelper] CDI-managed datasource initialized");
    }

    private void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);

        this.dataSource = new HikariDataSource(config);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public String getPoolStatus() {
        return "Active Connections: " + dataSource.getHikariPoolMXBean().getActiveConnections() +
                ", Idle Connections: " + dataSource.getHikariPoolMXBean().getIdleConnections() +
                ", Total Connections: " + dataSource.getHikariPoolMXBean().getTotalConnections();
    }

    public String getDbName() {
        return DB_NAME;
    }

    public String getHost() {
        return HOST;
    }

    public int getPort() {
        return PORT;
    }

    public String getUser() {
        return USER;
    }

    public String getPassword() {
        return PASSWORD;
    }

    @PreDestroy
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[DataSourceHelper] DataSource closed");
        }
    }
}