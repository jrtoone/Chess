package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{

    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if(!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game does not exist");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
