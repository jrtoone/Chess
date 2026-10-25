package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    @Test
    public void registerSuccessTest() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        RegisterRequest request = new RegisterRequest("jake", "1234", "jake@gmail.com");
        RegisterResult result = service.register(request);
        assertEquals("jake", result.username());
        assertNotNull(result.authToken());
        UserData storedUser = userDAO.getUser("jake");
        UserData expectedUser = new UserData("jake", "1234", "jake@gmail.com");
        assertEquals(expectedUser, storedUser);
        AuthData storedAuth = authDAO.getAuth(result.authToken());
        assertEquals("jake", storedAuth.username());
    }

    @Test
    public void registerDuplicateTest() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);

        RegisterRequest request = new RegisterRequest("jake", "1234", "jake@gmail.com");
        service.register(request);
        assertThrows(AlreadyTakenException.class, () -> service.register(request));
    }

    @Test
    public void registerNullRequestTest() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        assertThrows(BadRequestException.class, () -> service.register(null));
    }

    @Test
    public void registerMissingUsernameTest() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        RegisterRequest request = new RegisterRequest(
                null, "1234", "jake@gmail.com"
        );
        assertThrows(BadRequestException.class, () -> service.register(request));
    }

    @Test
    public void registerBlankUsername() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        RegisterRequest request = new RegisterRequest(
                " ", "1234", "jake@gmail.com"
        );
        assertThrows(BadRequestException.class, () -> service.register(request));
    }

    @Test
    public void registerBlankPassword() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        RegisterRequest request = new RegisterRequest(
                "jake", " ", "jake@gmail.com"
        );
        assertThrows(BadRequestException.class, () -> service.register(request));
        RegisterRequest nullPassword = new RegisterRequest(
                "jake", null, "jake@gmail.com"
        );
        assertThrows(BadRequestException.class, () -> service.register(nullPassword));
    }

    @Test
    public void registerBlankEmail() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        RegisterRequest request = new RegisterRequest(
                "jake", "1234", " "
        );
        assertThrows(BadRequestException.class, () -> service.register(request));
        RegisterRequest nullEmail = new RegisterRequest(
                "jake", "1234", null
        );
        assertThrows(BadRequestException.class, () -> service.register(nullEmail));
    }

    @Test
    public void loginSuccessTest() throws Exception{
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("jake", "1234","jake@gmail.com");
        service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("jake", "1234");
        LoginResult result = service.login(loginRequest);
        assertEquals("jake", result.username());
        assertNotNull(result.authToken());
        AuthData storedAuth = authDAO.getAuth(result.authToken());
        assertNotNull(storedAuth);
        assertEquals("jake", storedAuth.username());
    }

    @Test
    public void loginFailTest() throws Exception{
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("jake", "1234","jake@gmail.com");
        service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("jake", "wrongPassword");
        assertThrows(UnauthorizedException.class, () -> service.login(loginRequest));
    }

    @Test
    public void loginNonexistentUserTest() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        LoginRequest loginRequest = new LoginRequest("hailey", "1234");
        assertThrows(UnauthorizedException.class, () -> service.login(loginRequest));
    }

    @Test
    public void loginNullRequestTest() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        assertThrows(BadRequestException.class, () -> service.login(null));
    }

    @Test
    public void loginMissingUsernameTest() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        LoginRequest loginRequest = new LoginRequest(null, "1234");
        assertThrows(BadRequestException.class, () -> service.login(loginRequest));
    }

    @Test
    public void loginBlankUsernameTest() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        LoginRequest loginRequest = new LoginRequest(" ", "1234");
        assertThrows(BadRequestException.class, () -> service.login(loginRequest));
    }

    @Test
    public void loginMissingPasswordTest() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        LoginRequest loginRequest = new LoginRequest("jake", null);
        assertThrows(BadRequestException.class, () -> service.login(loginRequest));
    }

    @Test
    public void loginBlankPasswordTest() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        LoginRequest loginRequest = new LoginRequest("jake", " ");
        assertThrows(BadRequestException.class, () -> service.login(loginRequest));
    }

    @Test
    public void logoutSuccessTest() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("jake", "1234","jake@gmail.com");
        service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("jake", "1234");
        LoginResult result = service.login(loginRequest);
        AuthData storedAuth = authDAO.getAuth(result.authToken());
        assertNotNull(storedAuth);
        service.logout(storedAuth.authToken());
        AuthData deleteAuth = authDAO.getAuth(result.authToken());
        assertNull(deleteAuth);
    }

    @Test
    public void logoutInvalidTokenTest() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        UserService service = new UserService(userDAO, authDAO);
        assertThrows(UnauthorizedException.class, () -> service.logout("invalidToken"));
    }

}
