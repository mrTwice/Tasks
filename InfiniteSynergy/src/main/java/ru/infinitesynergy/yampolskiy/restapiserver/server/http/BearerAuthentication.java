package ru.infinitesynergy.yampolskiy.restapiserver.server.http;

public class BearerAuthentication {
    private String jwtToken;

    public BearerAuthentication(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return "Bearer " + jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}