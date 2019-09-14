package com.cadiducho.bot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

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
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + args);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setLeakDetectionThreshold(60 * 1000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
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