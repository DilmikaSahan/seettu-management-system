package com.seettu.backend.service;

import com.seettu.backend.Config.SmsProperties;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {
    
    private final SmsProperties smsProperties;
    
    @PostConstruct
    public void initializeTwilio() {
        if ("twilio".equals(smsProperties.getProvider())) {
            try {
                Twilio.init(
                    smsProperties.getTwilio().getAccountSid(),
                    smsProperties.getTwilio().getAuthToken()
                );
                log.info("Twilio SMS service initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize Twilio: {}", e.getMessage());
            }
        }
    }
    
    public void sendSms(String phoneNumber, String message) {
        if (!"twilio".equals(smsProperties.getProvider())) {
            log.info("SMS sent to {}: {}", phoneNumber, message);
            return;
        }
        
        try {
            // Ensure phone number is in international format
            String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
            
            MessageCreator messageCreator;
            
            // Use messaging service if available, otherwise use from number
            String messagingServiceSid = smsProperties.getTwilio().getMessagingServiceSid();
            if (messagingServiceSid != null && !messagingServiceSid.isEmpty()) {
                messageCreator = Message.creator(
                    new PhoneNumber(formattedPhoneNumber),
                    messagingServiceSid,
                    message
                );
            } else {
                messageCreator = Message.creator(
                    new PhoneNumber(formattedPhoneNumber),
                    new PhoneNumber(smsProperties.getTwilio().getFromNumber()),
                    message
                );
            }
            
            Message sentMessage = messageCreator.create();
            log.info("SMS sent successfully to {}, SID: {}", phoneNumber, sentMessage.getSid());
            
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            throw new RuntimeException("Failed to send SMS", e);
        }
    }
    
    public void sendWelcomeSms(String phoneNumber, String userName) {
        String message = String.format(
            "🎉 Welcome to Seettu, %s! Your account has been created successfully. " +
            "Start building your financial future with our trusted savings circles. " +
            "For support, contact us anytime!",
            userName
        );
        sendSms(phoneNumber, message);
    }
    
    public void sendGroupCreatedSms(String phoneNumber, String groupName, double monthlyAmount, String userName) {
        String message = String.format(
            "🏦 Hi %s! Your Seettu group '%s' has been created successfully! " +
            "Monthly contribution: Rs.%.2f. Members will be notified once the group starts. " +
            "Good luck with your savings journey!",
            userName, groupName, monthlyAmount
        );
        sendSms(phoneNumber, message);
    }
    
    public void sendGroupStartNotificationSms(String phoneNumber, String groupName, double monthlyAmount, String userName) {
        String message = String.format(
            "🚀 Hi %s! Your Seettu group '%s' has started! " +
            "Monthly contribution: Rs.%.2f. First payment is now due. " +
            "Thank you for joining our savings circle!",
            userName, groupName, monthlyAmount
        );
        sendSms(phoneNumber, message);
    }
    
    public void sendPaymentReceivedSms(String phoneNumber, String groupName, double amount, String month, String userName) {
        String message = String.format(
            "✅ Payment Confirmed! Hi %s, your payment of Rs.%.2f for group '%s' (%s) " +
            "has been received successfully. Thank you for staying on track with your savings!",
            userName, amount, groupName, month
        );
        sendSms(phoneNumber, message);
    }
    
    public void sendPaymentReminderSms(String phoneNumber, String groupName, double amount, String dueDate, String userName) {
        String message = String.format(
            "💰 Payment Reminder: Hi %s, your Seettu payment of Rs.%.2f for group '%s' " +
            "is due on %s. Please make your payment to stay active in the group!",
            userName, amount, groupName, dueDate
        );
        sendSms(phoneNumber, message);
    }
    
    public void sendPayoutNotificationSms(String phoneNumber, String groupName, double amount, String userName) {
        String message = String.format(
            "🎊 Congratulations %s! You've received your payout of Rs.%.2f from group '%s'! " +
            "Your consistent savings have paid off. Keep growing your wealth with Seettu!",
            userName, amount, groupName
        );
        sendSms(phoneNumber, message);
    }
    
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("Phone number cannot be null");
        }
        
        // Remove any spaces, dashes, or other non-numeric characters except '+'
        String cleaned = phoneNumber.replaceAll("[^+\\d]", "");
        
        // If it starts with '0', replace with Sri Lanka country code
        if (cleaned.startsWith("0")) {
            cleaned = "+94" + cleaned.substring(1);
        }
        // If it doesn't start with '+', assume it's a Sri Lankan number
        else if (!cleaned.startsWith("+")) {
            cleaned = "+94" + cleaned;
        }
        
        return cleaned;
    }
}