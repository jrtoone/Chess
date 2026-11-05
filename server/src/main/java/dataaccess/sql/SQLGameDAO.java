package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import java.util.ArrayList;

import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = """
                SELECT gameID, whiteUsername, blackUsername, gameName, game
                FROM game
                WHERE gameID = ?
                """;
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setInt(1, gameID);

            try (var results = statement.executeQuery()) {
                if(results.next()){
                    int storedID = results.getInt("gameID");
                    String whiteUsername = results.getString("whiteUsername");
                    String blackUsername = results.getString("blackUsername");
                    String gameName = results.getString("gameName");
                    String gameJson = results.getString("game");
                    Gson gson = new Gson();
                    ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);
                    return new GameData(storedID, whiteUsername, blackUsername, gameName, chessGame);
                }
            }

        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to retrieve game", e);
        }
        return null;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        String sql = """
                INSERT INTO game (
                gameID, whiteUsername, blackUsername, gameName, game)
                VALUES (?, ?, ?, ?, ?)
                """;
        Gson gson = new Gson();
        String gameJson = gson.toJson(game.game());
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)){
            statement.setInt(1, game.gameID());
            statement.setString(2, game.whiteUsername());
            statement.setString(3, game.blackUsername());
            statement.setString(4, game.gameName());
            statement.setString(5, gameJson);
            statement.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to create game", e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String sql = """
                SELECT gameID, whiteUsername, blackUsername, gameName, game
                FROM game
                """;
        Collection<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql);
             var results = statement.executeQuery()) {
            while (results.next()) {
                int gameID = results.getInt("gameID");
                String whiteUsername = results.getString("whiteUsername");
                String blackUsername = results.getString("blackUsername");
                String gameName = results.getString("gameName");
                String gameJson = results.getString("game");
                ChessGame chessGame = new Gson().fromJson(gameJson, ChessGame.class);
                GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                games.add(game);
            }

        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to list games", e);
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
                UPDATE game
                SET whiteUsername = ?,
                blackUsername = ?,
                gameName = ?,
                game = ?
                WHERE gameID = ?
                """;
        Gson gson = new Gson();
        String gameJson = gson.toJson(game.game());
        try (var conn = DatabaseManager.getConnection();
        var statement = conn.prepareStatement(sql)) {
            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gameJson);
            statement.setInt(5, game.gameID());
            statement.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to update game", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM game";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to clear games", e);
        }
    }
}