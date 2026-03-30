package com.vn.vodka_server.service;

import org.springframework.data.domain.Page;

import com.vn.vodka_server.dto.response.ActivityResponse;
import com.vn.vodka_server.dto.response.DashBoardStatsResponse;

public interface DashboardService {
    DashBoardStatsResponse getDashboardStats();

    Page<ActivityResponse> getActivities(String entityType, String search, int page, int pageSize);
}
