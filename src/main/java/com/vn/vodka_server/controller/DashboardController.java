package com.vn.vodka_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.DashBoardStatsResponse;
import com.vn.vodka_server.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

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

}
