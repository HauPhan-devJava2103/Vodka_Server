package com.vn.vodka_server.service;

import org.springframework.data.domain.Page;

import com.vn.vodka_server.dto.response.AdminUserResponse;
import com.vn.vodka_server.dto.response.UserStatsResponse;

public interface AdminUserService {

    // Lấy danh sách user bao gồm lấy danh sách, lọc, phân trang, tìm kiếm, sắp xếp
    Page<AdminUserResponse> getUsers(int page, int pageSize, String search, String status, String provider,
            String gender, String sort);

    // Thống kê users
    UserStatsResponse getUserStats();

    // Lock / Unlock user (toggle)
    void toggleLock(Long id);

    // Reset mật khẩu và gửi email
    void resetPassword(Long id);
}
