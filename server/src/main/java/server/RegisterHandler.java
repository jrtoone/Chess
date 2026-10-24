package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.UserService;
import service.RegisterRequest;
import service.RegisterResult;

public class RegisterHandler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) throws Exception {
        Gson gson = new Gson();
        String body = ctx.body();
        RegisterRequest request = gson.fromJson(body, RegisterRequest.class);
        RegisterResult result = userService.register(request);
        String json = gson.toJson(result);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(json);
    }
}
