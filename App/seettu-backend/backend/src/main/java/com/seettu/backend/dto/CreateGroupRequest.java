package com.seettu.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateGroupRequest {
    private String groupName;
    private Long packageId;
    private Double monthlyAmount;
    private Integer numberOfMonths;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    private List<MemberRequest> members;
    
    @Data
    public static class MemberRequest {
        private Long userId;
        private Integer orderNumber;
    }
}