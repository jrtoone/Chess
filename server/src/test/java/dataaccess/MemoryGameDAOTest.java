package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class MemoryGameDAOTest {
    @Test
    public void createGameTest() throws DataAccessException {
        MemoryGameDAO dao = new MemoryGameDAO();
        GameData game = new GameData(1234, null, null, "Test Game", new ChessGame());
        dao.createGame(game);
        assertEquals(game, dao.getGame(1234));
    }

    @Test
    public void getNonexistentGameTest() throws DataAccessException {
        MemoryGameDAO dao = new MemoryGameDAO();
        assertNull(dao.getGame(9999));
    }

    @Test
    public void listGamesTest() throws DataAccessException {
        MemoryGameDAO dao = new MemoryGameDAO();
        GameData game1 = new GameData(1234, null, null, "Game 1", new ChessGame());
        GameData game2 = new GameData(5678, null, null, "Game 2", new ChessGame());
        dao.createGame(game1);
        dao.createGame(game2);
        Collection<GameData> games = dao.listGames();
        assertEquals(2, games.size());
        assertTrue(games.contains(game1));
        assertTrue(games.contains(game2));
    }

    @Test
    public void updateGameTest() throws DataAccessException {
        MemoryGameDAO dao = new MemoryGameDAO();
        GameData original = new GameData(1234, null, null, "Test Game", new ChessGame());
        dao.createGame(original);
        GameData updated = new GameData(1234, "jake", null, "Test Game", original.game());
        dao.updateGame(updated);
        assertEquals(updated, dao.getGame(1234));
    }

    @Test
    public void clearGamesTest() throws DataAccessException {
        MemoryGameDAO dao = new MemoryGameDAO();
        GameData game = new GameData(1234, null, null, "Test Game", new ChessGame());
        dao.createGame(game);
        assertEquals(game, dao.getGame(1234));
        dao.clear();
        assertTrue(dao.listGames().isEmpty());
        assertNull(dao.getGame(1234));
    }

    @Test
    public void updateNonexistentGameTest() {
        MemoryGameDAO dao = new MemoryGameDAO();
        GameData game = new GameData(9999, null, null, "Missing Game", new ChessGame());
        assertThrows(DataAccessException.class, () -> dao.updateGame(game));
    }
}
