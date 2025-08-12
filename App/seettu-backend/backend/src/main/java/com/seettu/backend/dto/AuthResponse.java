package com.seettu.backend.dto;

public class AuthResponse {

    private String token;
    private String role;
    private String email;

    public AuthResponse() {}

    public AuthResponse(String token) {
        this.token = token;
    }

    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    public AuthResponse(String token, String role, String email) {
        this.token = token;
        this.role = role;
        this.email = email;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
