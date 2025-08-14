package com.blog.web.api.dto;

import java.util.Set;

public class LoginResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private UserDto user;
    
    public LoginResponse() {}
    
    public LoginResponse(String accessToken, String refreshToken, UserDto user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public UserDto getUser() {
        return user;
    }
    
    public void setUser(UserDto user) {
        this.user = user;
    }
}