package server;

import service.GameService;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.JoinGameRequest;

public class JoinGameHandler {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void joinGame(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        String body = ctx.body();
        JoinGameRequest request = new Gson().fromJson(body, JoinGameRequest.class);
        gameService.joinGame(authToken, request);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result("{}");
    }


}
