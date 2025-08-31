package com.seettu.backend.dto;

import lombok.Getter;

@Getter
public class RegisterRequest {

    // Getters and Setters
    private String name;
    private String email;
    private String password;
    private String role; // "PROVIDER" or "SUBSCRIBER"

    public void setName(String name) {   
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
 
    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return name;
    }

}
