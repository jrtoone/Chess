package dataaccess;

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
}