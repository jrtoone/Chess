package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MemoryAuthDAOTest {
    @Test
    public void createAuthTest() throws DataAccessException{
        MemoryAuthDAO dao = new MemoryAuthDAO();
        AuthData authData = new AuthData("1234", "jake");
        dao.createAuth(authData);
        AuthData retrievedAuthData = dao.getAuth("1234");
        assertEquals(authData, retrievedAuthData);
    }

    @Test
    public void getNonexistentAuthTest() throws DataAccessException{
        MemoryAuthDAO dao = new MemoryAuthDAO();
        AuthData retrievedAuthData = dao.getAuth("1234");
        assertNull(retrievedAuthData);
    }

    @Test
    public void deleteAuthTest() throws DataAccessException{
        MemoryAuthDAO dao = new MemoryAuthDAO();
        AuthData authData = new AuthData("1234", "jake");
        dao.createAuth(authData);
        assertEquals(authData, dao.getAuth("1234"));
        dao.deleteAuth("1234");
        AuthData retrievedAuthData = dao.getAuth("1234");
        assertNull(retrievedAuthData);
    }

    @Test
    public void clearAuthTest() throws DataAccessException{
        MemoryAuthDAO dao = new MemoryAuthDAO();
        AuthData authData1 = new AuthData("1234", "jake");
        AuthData authData2 = new AuthData("5678", "hailey");
        dao.createAuth(authData1);
        dao.createAuth(authData2);
        assertEquals(authData1, dao.getAuth("1234"));
        assertEquals(authData2, dao.getAuth("5678"));
        dao.clear();
        assertNull(dao.getAuth("1234"));
        assertNull(dao.getAuth("5678"));
    }
}
