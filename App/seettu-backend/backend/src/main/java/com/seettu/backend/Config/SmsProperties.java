package com.seettu.backend.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {
    
    private String provider = "twilio";
    
    private Twilio twilio = new Twilio();
    
    @Data
    public static class Twilio {
        private String accountSid;
        private String authToken;
        private String fromNumber;
        private String messagingServiceSid;
    }
    
    // Template configurations
    private Templates templates = new Templates();
    
    @Data
    public static class Templates {
        private String welcome = " Welcome to Seettu, {userName}! Your account has been created successfully.";
        private String groupCreated = " Hi {userName}! Your Seettu group '{groupName}' has been created successfully!";
        private String paymentReceived = " Pyment Confirmed! Hi {userName}, your payment of Rs.{amount} has been received.";
        private String paymentReminder = " Payment Reminder: Hi {userName}, your payment is due on {dueDate}.";
        private String groupStarted = " Hi {userName}! Your Seettu group '{groupName}' has started!";
        private String payoutNotification = " Congratulations {userName}! You've received your payout of Rs.{amount}!";
    }
}
