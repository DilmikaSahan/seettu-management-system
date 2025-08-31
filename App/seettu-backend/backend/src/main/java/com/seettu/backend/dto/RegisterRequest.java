package com.seettu.backend.dto;

import lombok.Getter;

@Getter
public class RegisterRequest {

    // Getters and Setters
    private String name;
    private String email;
    private String password;
    private String role; // Only "PROVIDER" or "SUBSCRIBER" allowed

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
        // Security: Only allow PROVIDER or SUBSCRIBER roles in public registration
        if ("PROVIDER".equals(role) || "SUBSCRIBER".equals(role)) {
            this.role = role;
        } else {
            throw new IllegalArgumentException("Invalid role. Only PROVIDER or SUBSCRIBER allowed for public registration.");
        }
    }

    public String getFullName() {
        return name;
    }
    
    // Validation method
    public boolean isValidRole() {
        return "PROVIDER".equals(this.role) || "SUBSCRIBER".equals(this.role);
    }

}
