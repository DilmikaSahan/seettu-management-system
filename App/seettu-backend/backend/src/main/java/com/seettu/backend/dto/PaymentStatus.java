package com.seettu.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentStatus {
    private Long paymentId;
    private Integer monthNumber;
    private Double amount;
    private LocalDate paymentDate;
    private Boolean isPaid;
    private LocalDateTime paidAt;
    private Boolean isOverdue;
    private String status; // "PAID", "PENDING", "OVERDUE"
    
    public PaymentStatus() {}
    
    public PaymentStatus(Long paymentId, Integer monthNumber, Double amount, 
                        LocalDate paymentDate, Boolean isPaid, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.monthNumber = monthNumber;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.isPaid = isPaid;
        this.paidAt = paidAt;
        
        // Calculate status
        this.isOverdue = !isPaid && paymentDate.isBefore(LocalDate.now());
        if (isPaid) {
            this.status = "PAID";
        } else if (isOverdue) {
            this.status = "OVERDUE";
        } else {
            this.status = "PENDING";
        }
    }
}
