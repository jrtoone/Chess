package dataaccess.sql;

import chess.*;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {

    @BeforeEach
    public void setUp() throws DataAccessException {
        SQLDatabaseInitializer.initialize();
    }

    private int generateGameID() {
        return java.util.UUID.randomUUID().hashCode() & Integer.MAX_VALUE;
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();

        int gameID = generateGameID();

        GameData game = new GameData(
                gameID,
                null,
                null,
                "Test Chess Game",
                new ChessGame()
        );

        gameDAO.createGame(game);

        String sql = """
        SELECT gameID, whiteUsername, blackUsername, gameName, game
        FROM game
        WHERE gameID = ?
        """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setInt(1, gameID);

            try (var results = statement.executeQuery()) {
                assertTrue(results.next());
                assertEquals(gameID, results.getInt("gameID"));
                assertNull(results.getString("whiteUsername"));
                assertNull(results.getString("blackUsername"));
                assertEquals("Test Chess Game", results.getString("gameName"));

                String storedJson = results.getString("game");
                assertNotNull(storedJson);
                assertFalse(storedJson.isEmpty());
            }

        } catch (java.sql.SQLException e) {
            fail("Failed to verify saved game: " + e.getMessage());
        }
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();

        int gameID = generateGameID();

        GameData original = new GameData(
                gameID,
                null,
                null,
                "Retrieve Test",
                new ChessGame()
        );

        gameDAO.createGame(original);

        GameData retrieved = gameDAO.getGame(gameID);

        assertNotNull(retrieved);
        assertEquals(gameID, retrieved.gameID());
        assertEquals("Retrieve Test", retrieved.gameName());
        assertNull(retrieved.whiteUsername());
        assertNull(retrieved.blackUsername());
        assertNotNull(retrieved.game());
        assertNotNull(retrieved.game().getBoard());
        ChessPiece whiteKing = retrieved.game().getBoard().getPiece(new ChessPosition(1,5));
        assertNotNull(whiteKing);
        assertEquals(ChessPiece.PieceType.KING, whiteKing.getPieceType());
        assertEquals(ChessGame.TeamColor.WHITE, whiteKing.getTeamColor());
    }

    @Test
    public void getGameNotFound() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();

        int gameID = generateGameID();

        GameData retrieved = gameDAO.getGame(gameID);

        assertNull(retrieved);
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();

        int id1 = generateGameID();
        int id2 = generateGameID();

        GameData game1 = new GameData(
                id1, null, null, "First Game", new ChessGame()
        );

        GameData game2 = new GameData(
                id2, null, null, "Second Game", new ChessGame()
        );

        gameDAO.createGame(game1);
        gameDAO.createGame(game2);

        var games = gameDAO.listGames();

        assertNotNull(games);
        assertTrue(games.stream().anyMatch(game ->
                game.gameID() == id1 && game.gameName().equals("First Game")));

        assertTrue(games.stream().anyMatch(game ->
                game.gameID() == id2 && game.gameName().equals("Second Game")));
    }

    @Test
    public void listGamesEmpty() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();

        String sql = "DELETE FROM game";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (java.sql.SQLException e) {
            fail("Failed to clear games for test: " + e.getMessage());
        }

        var games = gameDAO.listGames();

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        int gameID = generateGameID();

        GameData original = new GameData(
                gameID, null, null, "Original Game", new ChessGame()
        );

        gameDAO.createGame(original);

        GameData updated = new GameData(
                gameID, "alice", null, "Updated Game", new ChessGame()
        );

        gameDAO.updateGame(updated);

        GameData retrieved = gameDAO.getGame(gameID);

        assertNotNull(retrieved);
        assertEquals("alice", retrieved.whiteUsername());
        assertNull(retrieved.blackUsername());
        assertEquals("Updated Game", retrieved.gameName());
    }

    @Test
    public void updateGameBoardSuccess() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();
        int gameID = generateGameID();

        ChessGame chessGame = new ChessGame();

        GameData original = new GameData(
                gameID, null, null, "Board Update Test", chessGame
        );

        gameDAO.createGame(original);

        ChessMove move = new ChessMove(
                new ChessPosition(2, 5),
                new ChessPosition(4, 5),
                null
        );

        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {
            fail("The test move should be legal: " + e.getMessage());
        }
        GameData updated = new GameData(
                gameID, null, null, "Board Update Test", chessGame
        );

        gameDAO.updateGame(updated);
        GameData retrieved = gameDAO.getGame(gameID);

        assertNotNull(retrieved);
        assertNotNull(retrieved.game());

        ChessPiece originalSquare = retrieved.game().getBoard()
                .getPiece(new ChessPosition(2, 5));

        ChessPiece destinationSquare = retrieved.game().getBoard()
                .getPiece(new ChessPosition(4, 5));

        assertNull(originalSquare);

        assertNotNull(destinationSquare);
        assertEquals(ChessPiece.PieceType.PAWN, destinationSquare.getPieceType());
        assertEquals(ChessGame.TeamColor.WHITE, destinationSquare.getTeamColor());
    }

    @Test
    public void clearGamesSuccess() throws DataAccessException {
        SQLGameDAO gameDAO = new SQLGameDAO();

        int id1 = generateGameID();
        int id2 = generateGameID();

        gameDAO.createGame(new GameData(
                id1, null, null, "First Game", new ChessGame()
        ));

        gameDAO.createGame(new GameData(
                id2, null, null, "Second Game", new ChessGame()
        ));

        assertNotNull(gameDAO.getGame(id1));
        assertNotNull(gameDAO.getGame(id2));

        gameDAO.clear();

        assertNull(gameDAO.getGame(id1));
        assertNull(gameDAO.getGame(id2));
        assertTrue(gameDAO.listGames().isEmpty());
    }

}