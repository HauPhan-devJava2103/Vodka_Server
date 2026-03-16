package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private PaginationMeta pagination; // Thêm trường mới để làm API có phân trang

    // Constructor không có pagination
    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }

    // Thêm hàm success mới để dùng cho API phân trang
    public static ApiResponse success(String message, Object data, PaginationMeta pagination) {
        return new ApiResponse(true, message, data, pagination);
    }
}