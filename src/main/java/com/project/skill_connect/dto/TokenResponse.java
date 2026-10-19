package com.project.skill_connect.dto;

public class TokenResponse {
    private String token;
    private String tokenType;

    public TokenResponse(String token) {
        this.token = token;
        this.tokenType = "Bearer";
    }

    public String getToken() {
        return token;
    }

    // public void setToken(String token) {
    //     this.token = token;
    // }

    public String getTokenType() {
        return tokenType;
    }

    // public void setTokenType(String tokenType) {
    //     this.tokenType = tokenType;
    // }
}
