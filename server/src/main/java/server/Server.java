package server;

import io.javalin.*;
import dataaccess.*;
import service.AdminService;
import com.google.gson.Gson;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AdminService adminService = new AdminService(userDAO, authDAO, gameDAO);
        ClearHandler clearHandler = new ClearHandler(adminService);
        javalin.delete("/db", clearHandler::clear);
        javalin.exception(DataAccessException.class, (exception, ctx) -> {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + exception.getMessage())));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
