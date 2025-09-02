package com.seettu.backend.controller;

import com.seettu.backend.service.PaymentReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/test/reminders")
@CrossOrigin(origins = "http://localhost:4200")
public class ReminderTestController {

    @Autowired
    private PaymentReminderService paymentReminderService;

    // Test endpoint for daily reminders - Open for testing
    @PostMapping("/trigger-daily")
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

    // Test endpoint for overdue alerts - Open for testing
    @PostMapping("/trigger-overdue")
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

    // Test endpoint for weekly summary - Open for testing
    @PostMapping("/trigger-weekly")
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

    // Status endpoint to check reminder system status - Open for testing
    @GetMapping("/status")
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

    // Endpoint to get reminder system information - Open for testing
    @GetMapping("/info")
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
                "Manual trigger support"
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
