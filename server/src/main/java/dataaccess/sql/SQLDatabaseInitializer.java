package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

public class SQLDatabaseInitializer {
    public static void initialize() throws DataAccessException {
        DatabaseManager.createDatabase();
        String createUserTable = """
                CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(256) NOT NULL,
                password VARCHAR(256) NOT NULL,
                email VARCHAR(256) NOT NULL,
                PRIMARY KEY (username)
                )
                """;

        String createAuthTable = """
                CREATE TABLE IF NOT EXISTS auth (
                username VARCHAR(256) NOT NULL,
                authToken VARCHAR(256) NOT NULL,
                PRIMARY KEY (authToken)
                )
                """;

        String createGameTable = """
                CREATE TABLE IF NOT EXISTS game (
                gameID INT NOT NULL,
                whiteUsername VARCHAR(256),
                blackUsername VARCHAR(256),
                gameName VARCHAR(256) NOT NULL,
                game TEXT NOT NULL,
                PRIMARY KEY (gameID)
                )
                """;

        try (var conn = DatabaseManager.getConnection();
        var statement = conn.prepareStatement(createUserTable))
        {
            statement.executeUpdate();
            try(var authStatement = conn.prepareStatement(createAuthTable)) {
                authStatement.executeUpdate();
            }
            try(var gameStatement = conn.prepareStatement(createGameTable)) {
                gameStatement.executeUpdate();
            }
        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to create database tables", e);
        }

    }
}
