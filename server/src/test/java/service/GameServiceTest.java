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

    @Test
    public void createGameSuccessTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);
        AuthData auth = new AuthData("testToken", "jake");
        authDAO.createAuth(auth);
        CreateGameRequest gameRequest = new CreateGameRequest("Practice");
        CreateGameResult result = service.createGame(auth.authToken(), gameRequest);
        assertEquals(1, result.gameID());
        GameData savedGame = gameDAO.getGame(result.gameID());
        assertEquals("Practice", savedGame.gameName());
        assertNull(savedGame.whiteUsername());
        assertNull(savedGame.blackUsername());
    }

    @Test
    public void createGameUnauthorizedTest() {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);
        CreateGameRequest request = new CreateGameRequest("Practice");
        assertThrows(UnauthorizedException.class, () -> service.createGame("invalidToken", request));
    }

    @Test
    public void createGameBadRequestTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);
        AuthData auth = new AuthData("testToken", "jake");
        authDAO.createAuth(auth);
        CreateGameRequest request = new CreateGameRequest(null);
        assertThrows(BadRequestException.class, () -> service.createGame("testToken", request));
        assertEquals(0, gameDAO.listGames().size());
        assertThrows(BadRequestException.class, () -> service.createGame("testToken", new CreateGameRequest("")));

        assertThrows(BadRequestException.class, () -> service.createGame("testToken", new CreateGameRequest("   ")));
    }

    @Test
    public void createGameUniqueIDsTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService service = new GameService(authDAO, gameDAO);
        AuthData auth = new AuthData("testToken", "jake");
        authDAO.createAuth(auth);
        CreateGameResult game1 = service.createGame("testToken", new CreateGameRequest("Game One"));
        CreateGameResult game2 = service.createGame("testToken", new CreateGameRequest("Game Two"));
        assertNotEquals(game1.gameID(), game2.gameID());
        assertEquals(2, gameDAO.listGames().size());
    }

    @Test
    public void joinGameWhiteSuccessTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);
        AuthData auth = new AuthData("testToken", "jake");
        authDAO.createAuth(auth);
        GameData game = new GameData(1, null, null, "Practice", new ChessGame());
        gameDAO.createGame(game);
        JoinGameRequest request = new JoinGameRequest("WHITE", 1);
        service.joinGame("testToken", request);
        GameData updatedGame = gameDAO.getGame(1);
        assertEquals("jake", updatedGame.whiteUsername());
        assertNull(updatedGame.blackUsername());
    }

    @Test
    public void joinGameBlackSuccessTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);
        AuthData auth = new AuthData("testToken", "jake");
        authDAO.createAuth(auth);
        GameData game = new GameData(1, null, null, "Practice", new ChessGame());
        gameDAO.createGame(game);
        JoinGameRequest request = new JoinGameRequest("BLACK", 1);
        service.joinGame("testToken", request);
        GameData updatedGame = gameDAO.getGame(1);
        assertEquals("jake", updatedGame.blackUsername());
        assertNull(updatedGame.whiteUsername());
    }

    @Test
    public void joinGameAlreadyTakenTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);
        AuthData jake = new AuthData("testToken1", "jake");
        AuthData hailey = new AuthData("testToken2", "hailey");
        authDAO.createAuth(jake);
        authDAO.createAuth(hailey);
        GameData game = new GameData(1, null, null, "Practice", new ChessGame());
        gameDAO.createGame(game);
        JoinGameRequest request = new JoinGameRequest("BLACK", 1);
        service.joinGame("testToken1", request);
        JoinGameRequest request2 = new JoinGameRequest("BLACK", 1);
        assertThrows(AlreadyTakenException.class, () -> service.joinGame("testToken2", request2));
        GameData finalGame = gameDAO.getGame(1);
        assertEquals("jake", finalGame.blackUsername());
    }

    @Test
    public void joinGameUnauthorizedTest() {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);
        JoinGameRequest request = new JoinGameRequest("WHITE", 1);

        assertThrows(UnauthorizedException.class, () -> service.joinGame("invalidToken", request));
    }

    @Test
    public void joinGameInvalidIDTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);

        AuthData auth = new AuthData("testToken", "jake");
        authDAO.createAuth(auth);
        JoinGameRequest request = new JoinGameRequest("WHITE", 999);
        assertThrows(BadRequestException.class, () ->service.joinGame("testToken", request));
    }

    @Test
    public void joinGameInvalidColorTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService service = new GameService(authDAO, gameDAO);
        AuthData jake = new AuthData("testToken1", "jake");
        authDAO.createAuth(jake);
        JoinGameRequest request = new JoinGameRequest("GREEN", 1);
        assertThrows(BadRequestException.class, () -> service.joinGame("testToken1", request));
    }


}
