package edu.uta.cse.microservice.user.response;

/**
 * Login response for user client
 */

public class LoginResponse extends Response {

    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
