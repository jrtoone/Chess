package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

public class ServerFacade {

    private final String serverUrl;
    private final HttpClient httpClient;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
        httpClient = HttpClient.newHttpClient();
    }

    public RegisterResult register(String username, String password, String email) {
        var requestbody = new RegisterRequest(username, password, email);
        String json = new Gson().toJson(requestbody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            if(response.statusCode() == 400) {
                throw new RuntimeException("Please provide a valid username, password, and email");
            }
            if(response.statusCode() == 403) {
                throw new RuntimeException("That username is already taken");
            }
            if(response.statusCode() != 200) {
                throw new RuntimeException("Registration failed. Please try again.");
            }
            return new Gson().fromJson(response.body(), RegisterResult.class);
        } catch (java.io.IOException | InterruptedException e) {
            throw new RuntimeException("Unable to communicate with the server" , e);
        }
    }
}