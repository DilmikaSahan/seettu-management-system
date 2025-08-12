package com.seettu.backend.controller;

import com.seettu.backend.dto.*;
import com.seettu.backend.entity.*;
import com.seettu.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seettu")
@CrossOrigin(origins = "*")
public class SeettuController {

    @Autowired
    private SeettuService seettuService;
    
    @Autowired
    private SeettuPackageService packageService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;

    // Package endpoints
    @PostMapping("/packages")
    public ResponseEntity<SeettuPackage> createPackage(@RequestBody CreatePackageRequest request, Authentication auth) {
        User provider = userService.getUserByEmail(auth.getName());
        return ResponseEntity.ok(packageService.createPackage(request, provider));
    }

    @GetMapping("/packages")
    public ResponseEntity<List<SeettuPackage>> getPackages(Authentication auth) {
        User provider = userService.getUserByEmail(auth.getName());
        return ResponseEntity.ok(packageService.getPackagesByProvider(provider));
    }

    @GetMapping("/packages/all")
    public ResponseEntity<List<SeettuPackage>> getAllPackages() {
        return ResponseEntity.ok(packageService.getAllActivePackages());
    }

    @DeleteMapping("/packages/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        packageService.deletePackage(id);
        return ResponseEntity.ok().build();
    }

    // Group endpoints
    @PostMapping("/groups")
    public ResponseEntity<SeettuGroup> createGroup(@RequestBody CreateGroupRequest request, Authentication auth) {
        System.out.println("Creating group with request: " + request);
        System.out.println("Authenticated user: " + auth.getName());
        User provider = userService.getUserByEmail(auth.getName());
        System.out.println("Provider found: " + provider.getName());
        SeettuGroup createdGroup = seettuService.createGroup(request, provider);
        System.out.println("Group created with ID: " + createdGroup.getId());
        return ResponseEntity.ok(createdGroup);
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupDTO>> getGroups(Authentication auth) {
        System.out.println("Getting groups for user: " + auth.getName());
        User provider = userService.getUserByEmail(auth.getName());
        System.out.println("Provider found: " + provider.getName());
        List<GroupDTO> groups = seettuService.getGroupsByProvider(provider);
        System.out.println("Found " + groups.size() + " groups");
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<GroupDetailsResponse> getGroupDetails(@PathVariable Long id) {
        return ResponseEntity.ok(seettuService.getGroupDetails(id));
    }

    @PostMapping("/groups/{id}/start")
    public ResponseEntity<Void> startGroup(@PathVariable Long id, Authentication auth) {
        User provider = userService.getUserByEmail(auth.getName());
        seettuService.startGroup(id, provider);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/groups/{id}/cancel")
    public ResponseEntity<Void> cancelGroup(@PathVariable Long id, Authentication auth) {
        User provider = userService.getUserByEmail(auth.getName());
        seettuService.cancelGroup(id, provider);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups/status/{status}")
    public ResponseEntity<List<GroupDTO>> getGroupsByStatus(@PathVariable String status, Authentication auth) {
        User provider = userService.getUserByEmail(auth.getName());
        GroupStatus groupStatus = null;
        if (!"ALL".equalsIgnoreCase(status)) {
            try {
                groupStatus = GroupStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        List<GroupDTO> groups = seettuService.getGroupsByStatus(provider, groupStatus);
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/payments/{id}/mark-paid")
    public ResponseEntity<Void> markPaymentAsPaid(@PathVariable Long id, Authentication auth) {
        User provider = userService.getUserByEmail(auth.getName());
        seettuService.markPaymentAsPaid(id, provider);
        return ResponseEntity.ok().build();
    }

    // Subscriber management endpoints
    @PostMapping("/subscribers")
    public ResponseEntity<User> addSubscriber(@RequestBody AddSubscriberRequest request) {
        return ResponseEntity.ok(userService.addSubscriber(request));
    }

    @GetMapping("/subscribers/search")
    public ResponseEntity<List<User>> searchSubscribers(@RequestParam String term) {
        return ResponseEntity.ok(userService.searchSubscribers(term));
    }

    @GetMapping("/subscribers")
    public ResponseEntity<List<User>> getAllSubscribers() {
        return ResponseEntity.ok(userService.getAllSubscribers());
    }

    // Subscriber endpoints (for subscriber users)
    @GetMapping("/my-groups")
    public ResponseEntity<List<GroupDTO>> getMyGroups(Authentication auth) {
        User subscriber = userService.getUserByEmail(auth.getName());
        return ResponseEntity.ok(seettuService.getSubscriberGroups(subscriber));
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications(Authentication auth) {
        User user = userService.getUserByEmail(auth.getName());
        List<Notification> notifications = notificationService.getUserNotifications(user);
        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(NotificationDTO::new)
                .toList();
        return ResponseEntity.ok(notificationDTOs);
    }

    @GetMapping("/notifications/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(Authentication auth) {
        User user = userService.getUserByEmail(auth.getName());
        List<Notification> notifications = notificationService.getUnreadNotifications(user);
        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(NotificationDTO::new)
                .toList();
        return ResponseEntity.ok(notificationDTOs);
    }

    @PostMapping("/notifications/{id}/mark-read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(Authentication auth) {
        User user = userService.getUserByEmail(auth.getName());
        return ResponseEntity.ok(notificationService.getUnreadCount(user));
    }

    // Subscriber group details endpoint
    @GetMapping("/subscriber/group/{groupId}/details")
    public ResponseEntity<SubscriberGroupDetails> getSubscriberGroupDetails(@PathVariable Long groupId, Authentication auth) {
        User subscriber = userService.getUserByEmail(auth.getName());
        return ResponseEntity.ok(seettuService.getSubscriberGroupDetails(groupId, subscriber));
    }

    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<String> test(Authentication auth) {
        if (auth != null) {
            return ResponseEntity.ok("Authenticated as: " + auth.getName());
        } else {
            return ResponseEntity.ok("Not authenticated");
        }
    }
}
