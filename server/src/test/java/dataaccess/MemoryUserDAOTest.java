package dataaccess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.UserData;

public class MemoryUserDAOTest {
    @Test
    public void createUserTest() throws DataAccessException {
        MemoryUserDAO dao = new MemoryUserDAO();
        UserData user = new UserData("jake", "1234", "jake@gmail.com");
        dao.createUser(user);
        UserData retrievedUser = dao.getUser("jake");
        assertEquals(user, retrievedUser);
    }

    @Test
    public void getNonexistentuserTest() throws DataAccessException {
        MemoryUserDAO dao = new MemoryUserDAO();
        UserData retrieveUser = dao.getUser("jake");
        assertNull(retrieveUser);
    }

    @Test
    public void clearUsersTest() throws DataAccessException {
        MemoryUserDAO dao = new MemoryUserDAO();
        UserData user = new UserData("jake", "1234", "jake@gmail.com");
        dao.createUser(user);
        dao.clear();
        UserData retrievedUser = dao.getUser("jake");
        assertNull(retrievedUser);
    }

}
