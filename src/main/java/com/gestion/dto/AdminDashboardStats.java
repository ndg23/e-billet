package com.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// @AllArgsConstructor
public class AdminDashboardStats {
    private long totalEvents;
    private long activeTickets;
    private long totalUsers;

    public AdminDashboardStats(long totalEvents, long activeTickets, long totalUsers) {
        this.totalEvents = totalEvents;
        this.activeTickets = activeTickets;
        this.totalUsers = totalUsers;
    }
} 