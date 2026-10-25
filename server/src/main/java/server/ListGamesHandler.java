package server;

import service.GameService;
import io.javalin.http.Context;
import service.ListGamesResult;
import com.google.gson.Gson;

public class ListGamesHandler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService){
        this.gameService = gameService;
    }

    public void listGames(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        ListGamesResult result = gameService.listGames(authToken);
        String json = new Gson().toJson(result);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(json);
    }
}
