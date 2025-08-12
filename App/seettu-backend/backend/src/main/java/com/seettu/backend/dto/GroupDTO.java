package com.seettu.backend.dto;

import com.seettu.backend.entity.GroupStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class GroupDTO {
    private Long id;
    private String groupName;
    private String providerName;
    private String providerEmail;
    private PackageDTO seettuPackage;
    private Double monthlyAmount;
    private Integer numberOfMonths;
    private LocalDate startDate;
    private Boolean isActive;
    private Boolean isStarted;
    private GroupStatus status;
    private Integer memberCount;

    @Data
    public static class PackageDTO {
        private Long id;
        private String packageName;
        private String description;
        private Double packageValue;
        private Boolean isActive;
    }
}