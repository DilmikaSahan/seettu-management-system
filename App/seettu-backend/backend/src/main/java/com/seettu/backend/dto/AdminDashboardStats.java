package com.seettu.backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AdminDashboardStats {
    private long totalUsers;
    private long totalProviders;
    private long totalSubscribers;
    private long totalAdmins;
    
    public AdminDashboardStats(long totalUsers, long totalProviders, long totalSubscribers, long totalAdmins) {
        this.totalUsers = totalUsers;
        this.totalProviders = totalProviders;
        this.totalSubscribers = totalSubscribers;
        this.totalAdmins = totalAdmins;
    }
    
    public AdminDashboardStats() {}
}
