package service;

import chess.ChessGame;
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
    private int nextGameID = 1;

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

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException, UnauthorizedException, BadRequestException {
        AuthData auth = authDAO.getAuth(authToken);
        if(auth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        if(request == null || request.gameName() == null || request.gameName().isBlank()){
            throw new BadRequestException("Error: bad request");
        }
        int gameID = nextGameID++;
        GameData game = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(game);
        return new CreateGameResult(gameID);
    }

}
