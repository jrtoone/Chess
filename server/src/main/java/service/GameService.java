package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import java.util.ArrayList;

import java.util.Collection;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException, UnauthorizedException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        Collection<GameData> games = gameDAO.listGames();
        Collection<GameSummary> summaries = new ArrayList<>();
        for(GameData game : games) {
            GameSummary summary = new GameSummary(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            );
            summaries.add(summary);
        }
        return new ListGamesResult(summaries);
    }
}
