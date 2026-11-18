package client;

import org.junit.jupiter.api.*;
import server.Server;
import dataaccess.DataAccessException;
import dataaccess.sql.SQLUserDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;



public class ServerFacadeTests {

    private static ServerFacade facade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clearDatabase() throws DataAccessException {
        new SQLAuthDAO().clear();
        new SQLGameDAO().clear();
        new SQLUserDAO().clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerSuccess() {
        RegisterResult result = facade.register(
                "player1",
                "password123",
                "player1@email.com"
        );
        Assertions.assertNotNull(result);
        Assertions.assertEquals("player1", result.username());
        Assertions.assertNotNull(result.authToken());
        Assertions.assertFalse(result.authToken().isBlank());
    }

    @Test
    public void registerDuplicateusername(){
        facade.register(
                "player1",
                "password123",
                "player1@email.com"
        );
        Assertions.assertThrows(RuntimeException.class, () -> {
            facade.register(
                    "player1",
                    "differentPassword",
                    "another@email.com"
            );
        });
    }

}
