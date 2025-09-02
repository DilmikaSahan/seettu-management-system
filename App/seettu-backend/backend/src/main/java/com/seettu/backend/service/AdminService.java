package com.seettu.backend.service;

import com.seettu.backend.dto.AdminDashboardStats;
import com.seettu.backend.dto.CreateAdminRequest;
import com.seettu.backend.dto.UserSummaryDTO;
import com.seettu.backend.entity.Member;
import com.seettu.backend.entity.Role;
import com.seettu.backend.entity.SeettuGroup;
import com.seettu.backend.entity.User;
import com.seettu.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private SeettuPackageRepository seettuPackageRepository;

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
    @Transactional
    public boolean deleteUser(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return false;
            }
            
            User user = userOptional.get();
            
            // Prevent deleting the last admin
            if (user.getRole() == Role.ADMIN) {
                long adminCount = userRepository.countByRole(Role.ADMIN);
                if (adminCount <= 1) {
                    throw new IllegalStateException("Cannot delete the last admin user");
                }
            }

            // Delete related records in proper order to handle foreign key constraints
            
            // 1. Delete all notifications for this user
            notificationRepository.deleteByUserId(userId);
            
            // 2. Get all members for this user and delete their payments first
            List<Member> userMembers = memberRepository.findByUserId(userId);
            for (Member member : userMembers) {
                // Delete all payments for this member
                paymentRepository.deleteByMemberId(member.getId());
            }
            
            // 3. Now delete all members where this user is a member
            memberRepository.deleteByUserId(userId);
            
            // 4. For groups where this user is the provider, we need to handle differently
            // Check if user is a provider of any groups
            List<SeettuGroup> providedGroups = groupRepository.findByProviderId(userId);
            if (!providedGroups.isEmpty()) {
                // For now, we'll prevent deletion if user is a provider
                // In production, you might want to reassign groups or handle differently
                throw new RuntimeException("Cannot delete user who is providing active groups. Please reassign or close groups first.");
            }
            
            // 5. Delete any payments made by this user (if they're a provider)
            paymentRepository.deleteByPaidByProviderId(userId);
            
            // 6. Delete any packages created by this user (if they're a provider)
            seettuPackageRepository.deleteByProviderId(userId);
            
            // 7. Finally delete the user
            userRepository.delete(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Suspend a user by ID
     */
    @Transactional
    public boolean suspendUser(Long userId, String reason) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return false;
            }
            
            User user = userOptional.get();
            
            // Prevent suspending admin users
            if (user.getRole() == Role.ADMIN) {
                throw new IllegalStateException("Cannot suspend admin users");
            }
            
            // Check if already suspended
            if (user.getIsSuspended() != null && user.getIsSuspended()) {
                throw new IllegalStateException("User is already suspended");
            }
            
            // Suspend the user
            user.setIsSuspended(true);
            user.setSuspendedDate(LocalDateTime.now());
            user.setSuspensionReason(reason != null ? reason : "No reason provided");
            
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reactivate a suspended user
     */
    @Transactional
    public boolean reactivateUser(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return false;
            }
            
            User user = userOptional.get();
            
            // Check if user is suspended
            if (user.getIsSuspended() == null || !user.getIsSuspended()) {
                throw new IllegalStateException("User is not suspended");
            }
            
            // Reactivate the user
            user.setIsSuspended(false);
            user.setSuspendedDate(null);
            user.setSuspensionReason(null);
            
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a user by ID (only for admin users)
     */
    @Transactional
    public boolean deleteAdminUser(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return false;
            }
            
            User user = userOptional.get();
            
            // Only allow deletion for admin users
            if (user.getRole() != Role.ADMIN) {
                throw new IllegalStateException("Only admin users can be deleted. Use suspension for other users.");
            }
            
            // Prevent deleting the last admin
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot delete the last admin user");
            }

            // Delete the admin user (no foreign key constraints)
            userRepository.delete(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
