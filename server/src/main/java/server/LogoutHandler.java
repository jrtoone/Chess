package server;

import io.javalin.http.Context;
import service.UserService;

public class LogoutHandler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public void logout(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result("{}");
    }
}
