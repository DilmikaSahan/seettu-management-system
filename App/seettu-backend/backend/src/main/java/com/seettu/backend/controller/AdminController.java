package com.seettu.backend.controller;

import com.seettu.backend.dto.AdminDashboardStats;
import com.seettu.backend.dto.CreateAdminRequest;
import com.seettu.backend.dto.UserSummaryDTO;
import com.seettu.backend.entity.Role;
import com.seettu.backend.service.AdminService;
import com.seettu.backend.service.PaymentReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Only admins can access these endpoints
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private PaymentReminderService paymentReminderService;

    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStats> getDashboardStats() {
        try {
            AdminDashboardStats stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers() {
        try {
            List<UserSummaryDTO> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search users
     */
    @GetMapping("/users/search")
    public ResponseEntity<List<UserSummaryDTO>> searchUsers(@RequestParam String term) {
        try {
            List<UserSummaryDTO> users = adminService.searchUsers(term);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get users by role
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserSummaryDTO>> getUsersByRole(@PathVariable String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            List<UserSummaryDTO> users = adminService.getUsersByRole(userRole);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new admin user
     */
    @PostMapping("/users/admin")
    public ResponseEntity<?> createAdmin(@RequestBody CreateAdminRequest request) {
        try {
            UserSummaryDTO newAdmin = adminService.createAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAdmin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create admin user"));
        }
    }

    /**
     * Get user details by ID
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserSummaryDTO> getUserById(@PathVariable Long userId) {
        try {
            UserSummaryDTO user = adminService.getUserById(userId);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update user role
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> roleRequest) {
        try {
            String roleString = roleRequest.get("role");
            if (roleString == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Role is required"));
            }
            
            Role newRole = Role.valueOf(roleString.toUpperCase());
            UserSummaryDTO updatedUser = adminService.updateUserRole(userId, newRole);
            
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user role"));
        }
    }

    /**
     * Delete a user (only for admin users)
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            boolean deleted = adminService.deleteAdminUser(userId);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Admin user deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user"));
        }
    }

    /**
     * Suspend a user
     */
    @PutMapping("/users/{userId}/suspend")
    public ResponseEntity<?> suspendUser(@PathVariable Long userId, @RequestBody Map<String, String> suspensionRequest) {
        try {
            String reason = suspensionRequest.get("reason");
            boolean suspended = adminService.suspendUser(userId, reason);
            if (suspended) {
                return ResponseEntity.ok(Map.of("message", "User suspended successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to suspend user"));
        }
    }

    /**
     * Reactivate a suspended user
     */
    @PutMapping("/users/{userId}/reactivate")
    public ResponseEntity<?> reactivateUser(@PathVariable Long userId) {
        try {
            boolean reactivated = adminService.reactivateUser(userId);
            if (reactivated) {
                return ResponseEntity.ok(Map.of("message", "User reactivated successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reactivate user"));
        }
    }

    // ============== PAYMENT REMINDER SYSTEM ENDPOINTS ==============

    /**
     * Trigger daily payment reminders manually
     */
    @PostMapping("/reminders/trigger-daily")
    public ResponseEntity<Map<String, String>> triggerDailyReminders() {
        try {
            paymentReminderService.triggerDailyReminders();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Daily payment reminders triggered successfully");
            response.put("status", "success");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "failed");
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Trigger overdue payment alerts manually
     */
    @PostMapping("/reminders/trigger-overdue")
    public ResponseEntity<Map<String, String>> triggerOverdueAlerts() {
        try {
            paymentReminderService.triggerOverdueAlerts();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Overdue payment alerts triggered successfully");
            response.put("status", "success");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "failed");
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Trigger weekly group summary manually
     */
    @PostMapping("/reminders/trigger-weekly")
    public ResponseEntity<Map<String, String>> triggerWeeklySummary() {
        try {
            paymentReminderService.triggerWeeklySummary();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Weekly group summary triggered successfully");
            response.put("status", "success");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "failed");
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get payment reminder system status
     */
    @GetMapping("/reminders/status")
    public ResponseEntity<Map<String, String>> getReminderStatus() {
        try {
            Map<String, String> response = new HashMap<>();
            response.put("status", paymentReminderService.getReminderStatus());
            response.put("message", "Reminder system is operational");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "failed");
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get payment reminder system information
     */
    @GetMapping("/reminders/info")
    public ResponseEntity<Map<String, Object>> getReminderInfo() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("dailyReminderTime", "09:00 AM (every day)");
            response.put("overdueAlertTime", "10:00 AM (every day)");
            response.put("weeklyReportTime", "08:00 AM (every Monday)");
            response.put("reminderAdvanceDays", 3);
            response.put("status", "ACTIVE");
            response.put("features", new String[]{
                "Daily payment reminders",
                "Overdue payment alerts", 
                "Weekly group summaries",
                "SMS notifications",
                "Automated scheduling with Quartz"
            });
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "failed");
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
