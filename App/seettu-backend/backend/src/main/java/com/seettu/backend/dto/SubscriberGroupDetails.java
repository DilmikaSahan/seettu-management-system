package com.seettu.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SubscriberGroupDetails {
    private Long groupId;
    private String groupName;
    private String packageName;
    private Double monthlyAmount;
    private Integer numberOfMonths;
    private LocalDate startDate;
    private Boolean isActive;
    private Boolean isStarted;
    
    // Member-specific details
    private Integer myOrderNumber;
    private LocalDate myPackageReceiveDate;
    private Boolean hasReceivedPackage;
    
    // Payment details
    private List<PaymentStatus> payments;
    private Integer totalPaidMonths;
    private Integer totalOverdueMonths;
    private Double totalPaidAmount;
    private Double totalOwedAmount;
    
    // Progress
    private Integer currentMonth;
    private Double progressPercentage;
}
