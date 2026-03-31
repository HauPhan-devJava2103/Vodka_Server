package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {

    private Long id;

    private String fullName;

    private String dateOfBirth; // format "dd/MM/yyyy"

    private String gender; // "Nam", "Nữ", "Khác"

    private String phone;

    private String email;

    private String avatarUrl;

    private String provider; // "GOOGLE", "LOCAL"

    private String status; // "ACTIVE", "INACTIVE"

    private long movieWatched; // Số phim đã xem

    private long reviewCount; // Số lượng đánh giá

    private String createdAt; // format "yyyy-MM-dd"
}
