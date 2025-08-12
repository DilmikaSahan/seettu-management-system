package com.seettu.backend.dto;

import com.seettu.backend.entity.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Long groupId;
    private String groupName;
    private Long paymentId;
    
    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.type = notification.getType().name();
        this.isRead = notification.getIsRead();
        this.createdAt = notification.getCreatedAt();
        
        if (notification.getGroup() != null) {
            this.groupId = notification.getGroup().getId();
            this.groupName = notification.getGroup().getGroupName();
        }
        
        if (notification.getPayment() != null) {
            this.paymentId = notification.getPayment().getId();
        }
    }
}
