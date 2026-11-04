package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class SQLUserDAO implements UserDAO{
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM user";
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to clear users", e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = """
                INSERT INTO user (username, password, email)
                VALUES (?, ?, ?)
                """;
        try (var conn = DatabaseManager.getConnection();
        var statement = conn.prepareStatement(sql)) {
            statement.setString(1, user.username());
            statement.setString(3,user.email());
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            statement.setString(2, hashedPassword);
            statement.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to create user", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = """
                SELECT username, password, email
                FROM user
                WHERE username = ?
                """;
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, username);
            try (var results = statement.executeQuery()) {
                if (results.next()) {
                    String storedUsername = results.getString("username");
                    String storedPassword = results.getString("password");
                    String storedEmail = results.getString("email");
                    return new UserData(storedUsername, storedPassword, storedEmail);
                }
            }

        } catch (java.sql.SQLException e) {
            throw new DataAccessException("Failed to retrieve user", e);
        }
        return null;
    }
}
