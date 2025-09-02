package com.seettu.backend.service;

import com.seettu.backend.entity.Member;
import com.seettu.backend.entity.Payment;
import com.seettu.backend.entity.SeettuGroup;
import com.seettu.backend.repository.MemberRepository;
import com.seettu.backend.repository.PaymentRepository;
import com.seettu.backend.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentReminderService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private SmsService smsService;

    @Value("${app.reminders.enabled:true}")
    private boolean remindersEnabled;

    @Value("${app.reminders.advance-days:3}")
    private int advanceDays;

    // Run every day at 9:00 AM - Daily Payment Reminders
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyPaymentReminders() {
        if (!remindersEnabled) {
            System.out.println("🔕 Payment reminders are disabled");
            return;
        }

        System.out.println("🔔 Starting daily payment reminder process...");
        
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(advanceDays); // Remind X days before due date
        
        try {
            // Find all payments due in X days that are not paid
            List<Payment> upcomingPayments = paymentRepository.findUpcomingUnpaidPayments(reminderDate);
            
            int successCount = 0;
            for (Payment payment : upcomingPayments) {
                if (sendUpcomingPaymentReminder(payment)) {
                    successCount++;
                }
            }
            
            System.out.println("✅ Daily payment reminders sent successfully: " + successCount + "/" + upcomingPayments.size());
        } catch (Exception e) {
            System.err.println("❌ Error in daily payment reminder process: " + e.getMessage());
        }
    }

    // Run every day at 10:00 AM - Overdue Payment Alerts
    @Scheduled(cron = "0 0 10 * * *")
    public void sendOverduePaymentAlerts() {
        if (!remindersEnabled) {
            System.out.println("🔕 Overdue payment alerts are disabled");
            return;
        }

        System.out.println("⚠️ Starting overdue payment alert process...");
        
        LocalDate today = LocalDate.now();
        
        try {
            // Find all payments that are overdue (past due date and not paid)
            List<Payment> overduePayments = paymentRepository.findOverduePayments(today);
            
            int successCount = 0;
            for (Payment payment : overduePayments) {
                if (sendOverduePaymentAlert(payment)) {
                    successCount++;
                }
            }
            
            System.out.println("⚠️ Overdue payment alerts sent successfully: " + successCount + "/" + overduePayments.size());
        } catch (Exception e) {
            System.err.println("❌ Error in overdue payment alert process: " + e.getMessage());
        }
    }

    // Run every Monday at 8:00 AM - Weekly Group Summary
    @Scheduled(cron = "0 0 8 * * MON")
    public void sendWeeklyGroupSummary() {
        if (!remindersEnabled) {
            System.out.println("🔕 Weekly group summaries are disabled");
            return;
        }

        System.out.println("📊 Starting weekly group summary process...");
        
        try {
            List<SeettuGroup> activeGroups = groupRepository.findByIsActiveTrue();
            
            int successCount = 0;
            for (SeettuGroup group : activeGroups) {
                if (sendWeeklyGroupSummaryToProvider(group)) {
                    successCount++;
                }
            }
            
            System.out.println("📊 Weekly summaries sent successfully to: " + successCount + "/" + activeGroups.size() + " providers");
        } catch (Exception e) {
            System.err.println("❌ Error in weekly group summary process: " + e.getMessage());
        }
    }

    private boolean sendUpcomingPaymentReminder(Payment payment) {
        try {
            Member member = payment.getMember();
            if (member.getUser().getPhoneNumber() != null && !member.getUser().getPhoneNumber().isEmpty()) {
                String message = String.format(
                    "🔔 Payment Reminder\n\n" +
                    "Hi %s,\n\n" +
                    "Your Seettu payment is due in %d days!\n\n" +
                    "💰 Amount: Rs.%.2f\n" +
                    "📅 Due Date: %s\n" +
                    "🏦 Group: %s\n" +
                    "📋 Month: %d\n\n" +
                    "Please ensure timely payment to avoid any inconvenience.\n\n" +
                    "Thank you!\n" +
                    "Seettu Potha Team",
                    member.getUser().getName(),
                    advanceDays,
                    payment.getAmount(),
                    payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    payment.getGroup().getGroupName(),
                    payment.getMonthNumber()
                );
                
                smsService.sendSms(member.getUser().getPhoneNumber(), message);
                System.out.println("📱 Reminder sent to: " + member.getUser().getName());
                return true;
            } else {
                System.out.println("⚠️ No phone number for member: " + member.getUser().getName());
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to send reminder to: " + payment.getMember().getUser().getName() + " - " + e.getMessage());
            return false;
        }
    }

    private boolean sendOverduePaymentAlert(Payment payment) {
        try {
            Member member = payment.getMember();
            if (member.getUser().getPhoneNumber() != null && !member.getUser().getPhoneNumber().isEmpty()) {
                long daysOverdue = LocalDate.now().toEpochDay() - payment.getPaymentDate().toEpochDay();
                
                String message = String.format(
                    "⚠️ OVERDUE PAYMENT ALERT\n\n" +
                    "Hi %s,\n\n" +
                    "Your Seettu payment is OVERDUE!\n\n" +
                    "💰 Amount: Rs.%.2f\n" +
                    "📅 Due Date: %s\n" +
                    "⏰ Days Overdue: %d days\n" +
                    "🏦 Group: %s\n" +
                    "📋 Month: %d\n\n" +
                    "Please make the payment immediately to avoid penalties.\n\n" +
                    "Contact your provider for assistance.\n\n" +
                    "Seettu Potha Team",
                    member.getUser().getName(),
                    payment.getAmount(),
                    payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    daysOverdue,
                    payment.getGroup().getGroupName(),
                    payment.getMonthNumber()
                );
                
                smsService.sendSms(member.getUser().getPhoneNumber(), message);
                System.out.println("⚠️ Overdue alert sent to: " + member.getUser().getName());
                return true;
            } else {
                System.out.println("⚠️ No phone number for member: " + member.getUser().getName());
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to send overdue alert to: " + payment.getMember().getUser().getName() + " - " + e.getMessage());
            return false;
        }
    }

    private boolean sendWeeklyGroupSummaryToProvider(SeettuGroup group) {
        try {
            if (group.getProvider().getPhoneNumber() != null && !group.getProvider().getPhoneNumber().isEmpty()) {
                // Calculate group statistics
                long totalMembers = memberRepository.countByGroupId(group.getId());
                
                // Calculate payments this week (last 7 days)
                LocalDateTime weekStart = LocalDateTime.now().minusWeeks(1);
                long paidThisWeek = paymentRepository.countPaidPaymentsThisWeek(group.getId(), weekStart);
                
                long overduePayments = paymentRepository.countOverduePaymentsByGroup(group.getId(), LocalDate.now());
                Double totalCollected = paymentRepository.sumPaidAmountsByGroup(group.getId());
                if (totalCollected == null) totalCollected = 0.0;
                
                String message = String.format(
                    "📊 Weekly Group Summary\n\n" +
                    "Hi %s,\n\n" +
                    "Here's your weekly summary for '%s':\n\n" +
                    "👥 Total Members: %d\n" +
                    "✅ Payments This Week: %d\n" +
                    "⚠️ Overdue Payments: %d\n" +
                    "💰 Total Collected: Rs.%.2f\n" +
                    "📅 Group Status: %s\n\n" +
                    "Keep up the great work!\n\n" +
                    "Seettu Potha Team",
                    group.getProvider().getName(),
                    group.getGroupName(),
                    totalMembers,
                    paidThisWeek,
                    overduePayments,
                    totalCollected,
                    group.getStatus()
                );
                
                smsService.sendSms(group.getProvider().getPhoneNumber(), message);
                System.out.println("📊 Weekly summary sent to provider: " + group.getProvider().getName());
                return true;
            } else {
                System.out.println("⚠️ No phone number for provider: " + group.getProvider().getName());
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to send weekly summary to provider: " + group.getProvider().getName() + " - " + e.getMessage());
            return false;
        }
    }

    // Manual trigger methods for testing (these don't check the enabled flag)
    public void triggerDailyReminders() {
        System.out.println("🧪 Manual trigger: Daily payment reminders");
        sendDailyPaymentReminders();
    }

    public void triggerOverdueAlerts() {
        System.out.println("🧪 Manual trigger: Overdue payment alerts");
        sendOverduePaymentAlerts();
    }

    public void triggerWeeklySummary() {
        System.out.println("🧪 Manual trigger: Weekly group summary");
        sendWeeklyGroupSummary();
    }

    // Status check method
    public String getReminderStatus() {
        return String.format("Payment Reminders: %s, Advance Days: %d", 
            remindersEnabled ? "ENABLED" : "DISABLED", advanceDays);
    }
}
