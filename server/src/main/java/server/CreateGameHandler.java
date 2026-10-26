package server;

import com.google.gson.Gson;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.GameService;
import io.javalin.http.Context;

public class CreateGameHandler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void createGame(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        String body = ctx.body();
        CreateGameRequest request = new Gson().fromJson(body, CreateGameRequest.class);
        CreateGameResult result = gameService.createGame(authToken, request);
        String json = new Gson().toJson(result);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(json);
    }
}
