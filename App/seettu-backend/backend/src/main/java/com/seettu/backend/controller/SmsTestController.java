package com.seettu.backend.controller;

import com.seettu.backend.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sms")
@CrossOrigin(origins = "*")
public class SmsTestController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/test")
    public ResponseEntity<?> testSms(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String message = request.get("message");
            
            if (phoneNumber == null || message == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "phoneNumber and message are required"));
            }
            
            smsService.sendSms(phoneNumber, message);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "SMS sent successfully to " + phoneNumber
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to send SMS: " + e.getMessage()));
        }
    }
    
    @PostMapping("/test/welcome")
    public ResponseEntity<?> testWelcomeSms(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String userName = request.get("userName");
            
            if (phoneNumber == null || userName == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "phoneNumber and userName are required"));
            }
            
            smsService.sendWelcomeSms(phoneNumber, userName);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Welcome SMS sent successfully to " + phoneNumber
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to send welcome SMS: " + e.getMessage()));
        }
    }
    
    @PostMapping("/test/group-created")
    public ResponseEntity<?> testGroupCreatedSms(@RequestBody Map<String, Object> request) {
        try {
            String phoneNumber = (String) request.get("phoneNumber");
            String groupName = (String) request.get("groupName");
            String userName = (String) request.get("userName");
            Double monthlyAmount = Double.valueOf(request.get("monthlyAmount").toString());
            
            if (phoneNumber == null || groupName == null || userName == null || monthlyAmount == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "phoneNumber, groupName, userName, and monthlyAmount are required"));
            }
            
            smsService.sendGroupCreatedSms(phoneNumber, groupName, monthlyAmount, userName);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Group created SMS sent successfully to " + phoneNumber
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to send group created SMS: " + e.getMessage()));
        }
    }
}
