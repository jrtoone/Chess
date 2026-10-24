package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
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
}
