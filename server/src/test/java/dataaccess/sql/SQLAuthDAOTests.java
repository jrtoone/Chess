package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {

    @BeforeEach
    public void setUp() throws DataAccessException {
        SQLDatabaseInitializer.initialize();
    }

    @Test
    public void createAuthSuccess() throws Exception {
        SQLAuthDAO authDAO = new SQLAuthDAO();

        String token = java.util.UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, "testuser");

        authDAO.createAuth(auth);

        String sql = "SELECT authToken, username FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, token);

            try (var results = statement.executeQuery()) {
                assertTrue(results.next());
                assertEquals(token, results.getString("authToken"));
                assertEquals("testuser", results.getString("username"));
            }
        }
    }

    @Test
    public void createAuthDuplicate() throws DataAccessException {
        SQLAuthDAO authDAO = new SQLAuthDAO();

        String token = java.util.UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, "testuser");

        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        SQLAuthDAO authDAO = new SQLAuthDAO();

        String token = java.util.UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, "testuser");

        authDAO.createAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth(token);

        assertNotNull(retrievedAuth);
        assertEquals(token, retrievedAuth.authToken());
        assertEquals("testuser", retrievedAuth.username());
    }

    @Test
    public void getAuthNotFound() throws DataAccessException {
        SQLAuthDAO authDAO = new SQLAuthDAO();

        String token = java.util.UUID.randomUUID().toString();

        AuthData result = authDAO.getAuth(token);

        assertNull(result);
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        SQLAuthDAO authDAO = new SQLAuthDAO();

        String token = java.util.UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, "testuser");

        authDAO.createAuth(auth);

        assertNotNull(authDAO.getAuth(token));

        authDAO.deleteAuth(token);

        assertNull(authDAO.getAuth(token));
    }

    @Test
    public void deleteAuthPreservesOtherTokens() throws DataAccessException {
        SQLAuthDAO authDAO = new SQLAuthDAO();

        String tokenA = java.util.UUID.randomUUID().toString();
        String tokenB = java.util.UUID.randomUUID().toString();

        authDAO.createAuth(new AuthData(tokenA, "alice"));
        authDAO.createAuth(new AuthData(tokenB, "alice"));

        authDAO.deleteAuth(tokenA);

        assertNull(authDAO.getAuth(tokenA));
        assertNotNull(authDAO.getAuth(tokenB));
    }

    @Test
    public void clearAuthSuccess() throws DataAccessException {
        SQLAuthDAO authDAO = new SQLAuthDAO();

        String tokenA = java.util.UUID.randomUUID().toString();
        String tokenB = java.util.UUID.randomUUID().toString();

        authDAO.createAuth(new AuthData(tokenA, "alice"));
        authDAO.createAuth(new AuthData(tokenB, "bob"));

        assertNotNull(authDAO.getAuth(tokenA));
        assertNotNull(authDAO.getAuth(tokenB));

        authDAO.clear();

        assertNull(authDAO.getAuth(tokenA));
        assertNull(authDAO.getAuth(tokenB));
    }
}