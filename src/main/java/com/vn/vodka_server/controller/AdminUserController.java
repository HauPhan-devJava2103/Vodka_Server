package com.vn.vodka_server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.response.AdminUserResponse;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.dto.response.UserStatsResponse;
import com.vn.vodka_server.service.AdminUserService;
import com.vn.vodka_server.util.PaginationUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // Lấy danh sách user bao gồm lấy danh sách, lọc, phân trang, tìm kiếm, sắp xếp
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "newest") String sort) {

        Page<AdminUserResponse> resultPage = adminUserService.getUsers(
                page, pageSize, search, status, provider, gender, sort);

        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, page);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách người dùng thành công")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }

    // Thống kê user
    @GetMapping("/users/stats")
    public ResponseEntity<ApiResponse> getUserStats() {
        UserStatsResponse stats = adminUserService.getUserStats();
        return ResponseEntity.ok(ApiResponse.success("Lấy thống kê người dùng thành công", stats));
    }

    // Lock / Unlock user (toggle)
    @PatchMapping("/users/{id}/lock")
    public ResponseEntity<ApiResponse> toggleLock(@PathVariable Long id) {
        adminUserService.toggleLock(id);
        return ResponseEntity.ok(ApiResponse.success("Đã thay đổi trạng thái", null));
    }

    // Reset mật khẩu và gửi email
    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@PathVariable Long id) {
        adminUserService.resetPassword(id);
        return ResponseEntity.ok(ApiResponse.success("Đã gửi mật khẩu mới qua email", null));
    }
}

