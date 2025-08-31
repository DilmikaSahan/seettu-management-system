package com.seettu.backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreateAdminRequest {
    private String name;
    private String email;
    private String password;
    
    // Validation methods
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               email != null && email.contains("@") &&
               password != null && password.length() >= 6;
    }
}
