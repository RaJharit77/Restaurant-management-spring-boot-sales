package com.rajharit.rajharitsprings.config;

import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DataBaseSource {
    private final static int defaultPort = 5432;
    private final String host;
    private final String user;
    private final String password;
    private final String database;

    public DataBaseSource() {
        this.host = System.getenv("DB_HOST");
        this.user = System.getenv("DB_USER");
        this.password = System.getenv("DB_PASSWORD");
        this.database = System.getenv("DB_NAME");

        if (host == null || user == null || password == null || database == null) {
            throw new IllegalStateException("Database configuration is missing");
        }
    }

    public Connection getConnection() throws SQLException {
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + defaultPort + "/" + database;
        return DriverManager.getConnection(jdbcUrl, user, password);
    }
}