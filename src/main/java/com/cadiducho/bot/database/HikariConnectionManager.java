package com.cadiducho.bot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.flywaydb.core.Flyway;

import java.sql.Connection;

@Log
@RequiredArgsConstructor
public class HikariConnectionManager {

    private HikariDataSource dataSource = null;

    private final String hostname;
    private final String port;
    private final String database;
    private final String username;
    private final String password;

    private final String args = "?useLegacyDatetimeCode=false&serverTimezone=Europe/Madrid";

    /**
     * Configurar las conexiones a la base de datos
     */
    public void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mariadb://" + hostname + ":" + port + "/" + database + args);
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setLeakDetectionThreshold(60 * 1000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("passwordCharacterEncoding", "utf8");
        config.addDataSourceProperty("useUnicode", "true");

        dataSource = new HikariDataSource(config);

        final Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.migrate();
    }

    /**
     * Cerrar las conexiones a la base de datos
     */
    public void shutdownConnPool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    /**
     * Obtener una conexión segura de Hikari
     * @return Connection
     */
    Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (Exception e) {
            log.severe("Problema obteniendo una conexión desde Hikari");
            log.severe(e.getMessage());
        }
        return conn;
    }

    /**
     * Release the connection
     * @param conn The connection
     */
    public void closeConnection(Connection conn) {
        if (conn != null)
            dataSource.evictConnection(conn);
    }
}