package com.ecommerce.db;

import java.util.logging.Logger;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());

    private DatabaseConnection() {
        logger.info("Database Connection Created!");
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public void query(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL query cannot be null or empty");
        }
        logger.info("Executing query: " + sql);
    }
}
