package com.seettu.backend.service;

import com.seettu.backend.dto.AdminDashboardStats;
import com.seettu.backend.dto.CreateAdminRequest;
import com.seettu.backend.dto.UserSummaryDTO;
import com.seettu.backend.entity.Role;
import com.seettu.backend.entity.User;
import com.seettu.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get dashboard statistics for admin
     */
    public AdminDashboardStats getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalProviders = userRepository.countByRole(Role.PROVIDER);
        long totalSubscribers = userRepository.countByRole(Role.SUBSCRIBER);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);
        
        return new AdminDashboardStats(totalUsers, totalProviders, totalSubscribers, totalAdmins);
    }

    /**
     * Get all users with summary information
     */
    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAllOrderByCreatedDateDesc()
                .stream()
                .map(UserSummaryDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Search users by term
     */
    public List<UserSummaryDTO> searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllUsers();
        }
        
        return userRepository.searchAllUsers(searchTerm.trim())
                .stream()
                .map(UserSummaryDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get users by role
     */
    public List<UserSummaryDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(UserSummaryDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Create a new admin user (only accessible by existing admins)
     */
    public UserSummaryDTO createAdmin(CreateAdminRequest request) {
        // Validate request
        if (!request.isValid()) {
            throw new IllegalArgumentException("Invalid admin creation request");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new admin user
        User newAdmin = new User();
        newAdmin.setName(request.getName());
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        newAdmin.setRole(Role.ADMIN);
        newAdmin.setCreatedDate(LocalDateTime.now());

        User savedAdmin = userRepository.save(newAdmin);
        return new UserSummaryDTO(savedAdmin);
    }

    /**
     * Delete a user by ID
     */
    public boolean deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Prevent deleting the last admin
            if (user.getRole() == Role.ADMIN) {
                long adminCount = userRepository.countByRole(Role.ADMIN);
                if (adminCount <= 1) {
                    throw new IllegalStateException("Cannot delete the last admin user");
                }
            }
            
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    /**
     * Get user details by ID
     */
    public UserSummaryDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserSummaryDTO::new)
                .orElse(null);
    }

    /**
     * Update user role (admin only operation)
     */
    public UserSummaryDTO updateUserRole(Long userId, Role newRole) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Prevent changing the last admin's role
            if (user.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
                long adminCount = userRepository.countByRole(Role.ADMIN);
                if (adminCount <= 1) {
                    throw new IllegalStateException("Cannot change role of the last admin user");
                }
            }
            
            user.setRole(newRole);
            User updatedUser = userRepository.save(user);
            return new UserSummaryDTO(updatedUser);
        }
        return null;
    }
}
