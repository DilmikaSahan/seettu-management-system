package com.seettu.backend.service;

import com.seettu.backend.entity.Member;
import com.seettu.backend.entity.Notification;
import com.seettu.backend.entity.Payment;
import com.seettu.backend.entity.SeettuGroup;
import com.seettu.backend.entity.User;
import com.seettu.backend.repository.MemberRepository;
import com.seettu.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private MemberRepository memberRepository;
    
    public void createPaymentReceivedNotification(Payment payment) {
        Notification notification = new Notification();
        notification.setUser(payment.getMember().getUser());
        notification.setTitle("Payment Received");
        notification.setMessage(String.format("Your payment for month %d of group '%s' has been received by the provider.", 
                payment.getMonthNumber(), payment.getGroup().getGroupName()));
        notification.setType(Notification.NotificationType.PAYMENT_RECEIVED);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setGroup(payment.getGroup());
        notification.setPayment(payment);
        
        notificationRepository.save(notification);
        
        // Send SMS
        if (payment.getMember().getUser().getPhoneNumber() != null) {
            smsService.sendSms(payment.getMember().getUser().getPhoneNumber(), notification.getMessage());
        }
    }
    
    public void createGroupStartedNotification(SeettuGroup group) {
        // Get members from repository to avoid lazy loading issues
        List<Member> members = memberRepository.findByGroup(group);
        members.forEach(member -> {
            Notification notification = new Notification();
            notification.setUser(member.getUser());
            notification.setTitle("Seettu Group Started");
            notification.setMessage(String.format("The seettu group '%s' has been started. You will receive your package in month %d.", 
                    group.getGroupName(), member.getOrderNumber()));
            notification.setType(Notification.NotificationType.GROUP_STARTED);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setGroup(group);
            
            notificationRepository.save(notification);
        });
    }
    
    public void createGroupCompletedNotification(SeettuGroup group) {
        List<Member> members = memberRepository.findByGroup(group);
        
        // Notify all members
        members.forEach(member -> {
            Notification notification = new Notification();
            notification.setUser(member.getUser());
            notification.setTitle("Seettu Group Completed");
            notification.setMessage(String.format("Congratulations! The seettu group '%s' has been successfully completed. Thank you for your participation.", 
                    group.getGroupName()));
            notification.setType(Notification.NotificationType.GROUP_COMPLETED);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setGroup(group);
            
            notificationRepository.save(notification);
            
            // Send SMS
            if (member.getUser().getPhoneNumber() != null) {
                smsService.sendSms(member.getUser().getPhoneNumber(), notification.getMessage());
            }
        });
        
        // Notify provider
        Notification providerNotification = new Notification();
        providerNotification.setUser(group.getProvider());
        providerNotification.setTitle("Seettu Group Completed");
        providerNotification.setMessage(String.format("Your seettu group '%s' has been successfully completed. All members have fulfilled their commitments.", 
                group.getGroupName()));
        providerNotification.setType(Notification.NotificationType.GROUP_COMPLETED);
        providerNotification.setCreatedAt(LocalDateTime.now());
        providerNotification.setGroup(group);
        
        notificationRepository.save(providerNotification);
    }
    
    public void createGroupCancelledNotification(SeettuGroup group) {
        List<Member> members = memberRepository.findByGroup(group);
        
        // Notify all members
        members.forEach(member -> {
            Notification notification = new Notification();
            notification.setUser(member.getUser());
            notification.setTitle("Seettu Group Cancelled");
            notification.setMessage(String.format("The seettu group '%s' has been cancelled by the provider. Please contact the provider for more details.", 
                    group.getGroupName()));
            notification.setType(Notification.NotificationType.GROUP_CANCELLED);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setGroup(group);
            
            notificationRepository.save(notification);
            
            // Send SMS
            if (member.getUser().getPhoneNumber() != null) {
                smsService.sendSms(member.getUser().getPhoneNumber(), notification.getMessage());
            }
        });
    }
    
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }
    
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
}