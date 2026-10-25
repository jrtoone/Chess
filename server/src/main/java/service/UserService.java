package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException, BadRequestException, AlreadyTakenException {
        if(request == null){
            throw new BadRequestException("Error: bad request");
        }
        if(request.username() == null || request.username().isBlank() ||
                request.password() == null || request.password().isBlank() ||
                        request.email() == null || request.email().isBlank()){
            throw new BadRequestException("Error: bad request");
        }
        if(userDAO.getUser(request.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(user);
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        authDAO.createAuth(auth);

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException, BadRequestException, UnauthorizedException{
        if(request == null){
            throw new BadRequestException("Error: bad request");
        }
        if(request.username() == null || request.username().isBlank() || request.password() == null || request.password().isBlank()){
            throw new BadRequestException("Error: bad request");
        }

        UserData user = userDAO.getUser(request.username());
        if(user == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        if(!user.password().equals(request.password())){
            throw new UnauthorizedException("Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        authDAO.createAuth(auth);

        return new LoginResult(request.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedException {
        AuthData auth = authDAO.getAuth(authToken);
        if(auth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}
