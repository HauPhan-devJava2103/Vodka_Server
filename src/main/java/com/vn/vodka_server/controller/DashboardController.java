package com.vn.vodka_server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.response.ActivityResponse;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.DashBoardStatsResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.service.DashboardService;
import com.vn.vodka_server.util.PaginationUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    // Thống kê Dashboard
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        try {
            DashBoardStatsResponse rs = dashboardService.getDashboardStats();
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Lấy thống kê Dashboard thành công.")
                    .data(rs)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Activities Dashboard
    @GetMapping("/activities")
    public ResponseEntity<ApiResponse> getActivities(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<ActivityResponse> activities = dashboardService.getActivities(entityType, search, page, pageSize);
            PaginationMeta pagination = PaginationUtils.buildPaginationMeta(activities, page);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Lấy danh sách hoạt động thành công.")
                    .data(activities.getContent())
                    .pagination(pagination)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

}
