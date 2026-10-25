package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    @Test
    public void listGamesSuccessTest() throws Exception{
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);

        AuthData auth = new AuthData("testToken", "jake");
        authDAO.createAuth(auth);
        GameData game = new GameData(1,"jake",null,"Practice", new ChessGame());
        gameDAO.createGame(game);
        ListGamesResult result = service.listGames("testToken");
        assertEquals(1,result.games().size());
        GameSummary expected = new GameSummary (1, "jake", null, "Practice");
        assertTrue(result.games().contains(expected));
    }

    @Test
    public void listGamesUnauthorizedTest() {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);
        assertThrows(UnauthorizedException.class, () -> service.listGames("invalidToken"));
    }

    @Test
    public void listGamesEmptyTest() throws Exception{
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);
        AuthData auth = new AuthData("testToken", "jake");
        authDAO.createAuth(auth);
        ListGamesResult result = service.listGames("testToken");
        assertEquals(0,result.games().size());
    }

}
