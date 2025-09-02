package com.seettu.backend.dto;

import com.seettu.backend.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class UserSummaryDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdDate;
    private Boolean active;
    private Boolean isSuspended;
    private LocalDateTime suspendedDate;
    private String suspensionReason;
    
    // Constructor from User entity
    public UserSummaryDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.createdDate = user.getCreatedDate();
        this.isSuspended = user.getIsSuspended() != null ? user.getIsSuspended() : false;
        this.active = !this.isSuspended; // Active if not suspended
        this.suspendedDate = user.getSuspendedDate();
        this.suspensionReason = user.getSuspensionReason();
    }
    
    // Default constructor
    public UserSummaryDTO() {}
}
