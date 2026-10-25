package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.LoginRequest;
import service.LoginResult;
import service.UserService;

public class LoginHandler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public void login(Context ctx) throws Exception {
        Gson gson = new Gson();
        String body = ctx.body();
        LoginRequest request = gson.fromJson(body, LoginRequest.class);
        LoginResult result = userService.login(request);
        String json = gson.toJson(result);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(json);
    }
}
