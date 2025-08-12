package com.seettu.backend.controller;

import com.seettu.backend.dto.RegisterRequest;
import com.seettu.backend.dto.LoginRequest;
import com.seettu.backend.dto.AuthResponse;
import com.seettu.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
