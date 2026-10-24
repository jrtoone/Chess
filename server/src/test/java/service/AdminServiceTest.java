package service;

import dataaccess.*;
import model.*;
import chess.ChessGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class AdminServiceTest {

    @Test
    public void clearAppTest() throws DataAccessException {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        UserData user = new UserData("jake", "1234", "jake@gmail.com");
        AuthData auth = new AuthData("abc123", "jake");
        GameData game = new GameData(1234, null, null, "Test Game", new ChessGame());
        userDAO.createUser(user);
        authDAO.createAuth(auth);
        gameDAO.createGame(game);
        AdminService adminService = new AdminService(userDAO, authDAO, gameDAO);
        assertEquals(user, userDAO.getUser("jake"));
        assertEquals(auth, authDAO.getAuth("abc123"));
        assertEquals(game, gameDAO.getGame(1234));
        adminService.clearApp();
        assertNull(userDAO.getUser("jake"));
        assertNull(authDAO.getAuth("abc123"));
        assertNull(gameDAO.getGame(1234));
    }
}
