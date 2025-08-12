package com.seettu.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class GroupDetailsResponse {
    private Long id;
    private String groupName;
    private String packageName;
    private Double monthlyAmount;
    private Integer numberOfMonths;
    private LocalDate startDate;
    private Boolean isActive;
    private Boolean isStarted;
    private List<MemberDetails> members;
    private List<MonthlyPaymentStatus> monthlyPayments;
    
    @Data
    public static class MemberDetails {
        private Long id;
        private String name;
        private String phoneNumber;
        private Integer orderNumber;
        private LocalDate packageReceiveDate;
    }
    
    @Data
    public static class MonthlyPaymentStatus {
        private Integer monthNumber;
        private LocalDate paymentDate;
        private List<MemberPaymentStatus> memberPayments;
    }
    
    @Data
    public static class MemberPaymentStatus {
        private Long memberId;
        private String memberName;
        private Boolean isPaid;
        private Long paymentId;
    }
}