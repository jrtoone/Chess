package dataaccess;

import model.AuthData;
import java.util.Map;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

}
