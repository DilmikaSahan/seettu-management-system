package com.seettu.backend.service;
import com.seettu.backend.dto.AuthResponse;
import com.seettu.backend.dto.LoginRequest;
import com.seettu.backend.dto.RegisterRequest;
import com.seettu.backend.entity.Role;
import com.seettu.backend.entity.User;
import com.seettu.backend.repository.UserRepository;
import com.seettu.backend.Config.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SmsService smsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));

        User savedUser = userRepository.save(user);

        // Send welcome SMS if phone number is provided
        if (savedUser.getPhoneNumber() != null && !savedUser.getPhoneNumber().isEmpty()) {
            try {
                smsService.sendWelcomeSms(savedUser.getPhoneNumber(), savedUser.getName());
            } catch (Exception e) {
                // Log error but don't fail registration if SMS fails
                System.err.println("Failed to send welcome SMS: " + e.getMessage());
            }
        }

        // Create UserDetails for JWT generation
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        String token = jwtUtils.generateToken(userDetails);
        
        return new AuthResponse(token, user.getRole().name(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create UserDetails for JWT generation
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        String token = jwtUtils.generateToken(userDetails);
        
        return new AuthResponse(token, user.getRole().name(), user.getEmail());
    }
}
