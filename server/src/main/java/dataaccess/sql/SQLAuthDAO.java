package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = """
                INSERT INTO auth (authToken, username)
                VALUES (?, ?)
                """;
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());
            statement.executeUpdate();

        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to create authentication token", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = """
                SELECT authToken, username
                FROM auth
                WHERE authToken = ?
                """;
        try (var conn = DatabaseManager.getConnection(); var statement = conn.prepareStatement(sql)) {
            statement.setString(1, authToken);
            try (var results = statement.executeQuery()) {
                if(results.next()) {
                    String storedToken = results.getString("authToken");
                    String storedUsername = results.getString("username");
                    return new AuthData(storedToken, storedUsername);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to retrieve authentication token", e);
        }

        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE  authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, authToken);
            statement.executeUpdate();

        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to delete authentication token", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to clear authentication tokens", e);
        }
    }
}