package dataaccess;

import dataaccess.sql.SQLDatabaseInitializer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    @Test
    public void testDatabaseConnection() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        } catch (java.sql.SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    @Test
    public void testUserTableCreation() throws DataAccessException {
        SQLDatabaseInitializer.initialize();

        try(var conn = DatabaseManager.getConnection();
        var statement = conn.prepareStatement("SHOW TABLES LIKE 'user'");
        var results = statement.executeQuery()) {
            assertTrue(results.next(), "The user table should exist");
        } catch (java.sql.SQLException e) {
            fail("Failed to verify user table: " + e.getMessage());
        }
    }

    @Test
    public void testAuthTableCreation() throws DataAccessException {
        SQLDatabaseInitializer.initialize();

        try(var conn = DatabaseManager.getConnection();
            var statement = conn.prepareStatement("SHOW TABLES LIKE 'auth'");
            var results = statement.executeQuery()) {
            assertTrue(results.next(), "The auth table should exist");
        } catch (java.sql.SQLException e) {
            fail("Failed to verify auth table: " + e.getMessage());
        }
    }

    @Test
    public void testGameTableCreation() throws DataAccessException {
        SQLDatabaseInitializer.initialize();

        try(var conn = DatabaseManager.getConnection();
            var statement = conn.prepareStatement("SHOW TABLES LIKE 'game'");
            var results = statement.executeQuery()) {
            assertTrue(results.next(), "The game table should exist");
        } catch (java.sql.SQLException e) {
            fail("Failed to verify game table: " + e.getMessage());
        }
    }
}