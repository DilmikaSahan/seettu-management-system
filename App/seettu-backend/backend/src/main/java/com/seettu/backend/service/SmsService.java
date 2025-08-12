package com.seettu.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {
    
    public void sendSms(String phoneNumber, String message) {
        // TODO: Integrate with actual SMS service provider (Twilio, AWS SNS, etc.)
        // For now, just log the SMS
        log.info("SMS sent to {}: {}", phoneNumber, message);
        
        // Example integration with Twilio:
        /*
        try {
            Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber("YOUR_TWILIO_PHONE_NUMBER"),
                message
            ).create();
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
        }
        */
    }
}