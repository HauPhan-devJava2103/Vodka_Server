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
@JsonInclude(JsonInclude.Include.NON_NULL) // Các trường ko được xét(nhận giá trị null) sẽ ko được trả về trong Json
public class TagResponse {
    // ID của tag
    private Long id;

    // Tên của tag
    private String name;

    // Slug dùng cho URL
    private String slug;

    // Dùng cho api trả về danh sách tags của admin
    private Long movieCount;
    private Long viewCount;
    private String createdAt; // format dd/MM/yyyy
    private String updatedAt; // format dd/MM/yyyy
}
