package com.vn.vodka_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO phản hồi chứa thông tin cơ bản của Tag phim.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagResponse {
    // ID của tag
    private Long id;

    // Tên của tag
    private String name;

    // Slug dùng cho URL
    private String slug;
}
