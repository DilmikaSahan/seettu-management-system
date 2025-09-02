package com.seettu.backend.service;

import com.seettu.backend.dto.AddSubscriberRequest;
import com.seettu.backend.entity.Role;
import com.seettu.backend.entity.User;
import com.seettu.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private SmsService smsService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }

    public User addSubscriber(AddSubscriberRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User subscriber = new User();
        subscriber.setName(request.getName());
        subscriber.setEmail(request.getEmail());
        subscriber.setPhoneNumber(request.getPhoneNumber());
        subscriber.setPassword(passwordEncoder.encode(request.getPassword()));
        subscriber.setRole(Role.SUBSCRIBER);
        
        // Auto-generate unique userId
        subscriber.setUserId(generateUniqueUserId());

        User savedSubscriber = userRepository.save(subscriber);
        
        // Send welcome SMS to new subscriber
        if (savedSubscriber.getPhoneNumber() != null && !savedSubscriber.getPhoneNumber().isEmpty()) {
            try {
                smsService.sendWelcomeSms(savedSubscriber.getPhoneNumber(), savedSubscriber.getName());
            } catch (Exception e) {
                // Log error but don't fail subscriber creation if SMS fails
                System.err.println("Failed to send welcome SMS to new subscriber: " + e.getMessage());
            }
        }

        return savedSubscriber;
    }
    
    private String generateUniqueUserId() {
        String userId;
        do {
            // Generate userId in format: SUB + timestamp + random 3-digit number
            long timestamp = System.currentTimeMillis() % 1000000; // Last 6 digits of timestamp
            int random = (int) (Math.random() * 900) + 100; // Random 3-digit number (100-999)
            userId = "SUB" + timestamp + random;
        } while (userRepository.existsByUserId(userId)); // Ensure uniqueness
        
        return userId;
    }

    public List<User> searchSubscribers(String searchTerm) {
        return userRepository.searchUsersByRoleAndTerm(Role.SUBSCRIBER, searchTerm);
    }

    public List<User> getAllSubscribers() {
        return userRepository.findByRole(Role.SUBSCRIBER);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
