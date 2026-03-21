package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO phản hồi chứa thông tin cơ bản của Tag phim.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse {
    // ID của tag
    private String id;

    // Tên của tag
    private String name;

    private String slug;
}
