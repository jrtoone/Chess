package server;

import com.google.gson.JsonSyntaxException;
import io.javalin.*;
import dataaccess.*;
import service.*;
import com.google.gson.Gson;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        // Javalin Config & Data Access Objects
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AdminService adminService = new AdminService(userDAO, authDAO, gameDAO);
        ClearHandler clearHandler = new ClearHandler(adminService);
        javalin.delete("/db", clearHandler::clear);

        // Exception Handlers
        javalin.exception(DataAccessException.class, (exception, ctx) -> {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + exception.getMessage())));
        });
        javalin.exception(BadRequestException.class, (exception, ctx) -> {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", exception.getMessage())));
        });
        javalin.exception(AlreadyTakenException.class, (exception, ctx) -> {
            ctx.status(403);
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", exception.getMessage())));
        });
        javalin.exception(JsonSyntaxException.class, (exception,  ctx) -> {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(
                    Map.of("message", "Error: bad request")
            ));
        });
        javalin.exception(UnauthorizedException.class, (exception, ctx) -> {
            ctx.status(401);
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", exception.getMessage())));
        });

        // Services and Register HTTP Routes
        UserService userService = new UserService(userDAO, authDAO);
        RegisterHandler registerHandler = new RegisterHandler(userService);
        javalin.post("/user", registerHandler::register);

        LoginHandler loginHandler = new LoginHandler(userService);
        javalin.post("/session", loginHandler::login);

        LogoutHandler logoutHandler = new LogoutHandler(userService);
        javalin.delete("/session", logoutHandler::logout);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
