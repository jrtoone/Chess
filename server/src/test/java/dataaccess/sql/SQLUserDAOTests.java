package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.UserData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {
    @BeforeEach
    public void setUp() throws DataAccessException {
        SQLDatabaseInitializer.initialize();
    }

    @Test
    public void createUserSuccess() throws Exception {
        SQLUserDAO userDAO = new SQLUserDAO();

        String username = "testuser_" + java.util.UUID.randomUUID();

        UserData user = new UserData(
                username,
                "testPassword123",
                "test@example.com"
        );

        userDAO.createUser(user);
        String sql = "SELECT username, password, email FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
        var statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            try (var results = statement.executeQuery()) {
                assertTrue(results.next());
                assertEquals(username, results.getString("username"));
                assertEquals("test@example.com", results.getString("email"));
                String storedPassword = results.getString("password");
                assertNotEquals("testPassword123", storedPassword);
                assertTrue(BCrypt.checkpw("testPassword123", storedPassword));
            }
        }
    }

    @Test
    public void createUserDuplicate() throws DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();

        String username = "duplicate_" + java.util.UUID.randomUUID();

        UserData user = new UserData(
                username,
                "password123",
                "test@example.com"
        );

        userDAO.createUser(user);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();

        String username = "getuser_" + java.util.UUID.randomUUID();

        UserData user = new UserData(
                username,
                "password123",
                "test@example.com"
        );

        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser(username);

        assertNotNull(retrievedUser);
        assertEquals(username, retrievedUser.username());
        assertEquals(user.email(), retrievedUser.email());
        assertTrue(BCrypt.checkpw(user.password(), retrievedUser.password()));
    }

    @Test
    public void getUserNotFound() throws DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();

        String username = "nonexistent_" + java.util.UUID.randomUUID();

        UserData result = userDAO.getUser(username);

        assertNull(result);
    }

    @Test
    public void clearUsersSuccess() throws DataAccessException {
        SQLUserDAO userDAO = new SQLUserDAO();

        String username = "clearuser_" + java.util.UUID.randomUUID();

        UserData user = new UserData(
                username,
                "password123",
                "test@example.com"
        );

        userDAO.createUser(user);

        assertNotNull(userDAO.getUser(username));

        userDAO.clear();

        assertNull(userDAO.getUser(username));
    }

}