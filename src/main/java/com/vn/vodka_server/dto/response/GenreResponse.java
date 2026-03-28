package com.vn.vodka_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO phản hồi chứa thông tin cơ bản của thể loại phim.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Các trường ko được xét(nhận giá trị null) sẽ không được trả về trong Json
public class GenreResponse {
    // ID của thể loại
    private Long id;

    // Tên của thể loại
    private String name;

    // Slug dùng cho URL
    private String slug;

    // Dùng cho api trả về danh sách genres của admin
    private Long movieCount;
    private Long viewCount;
    private String createdAt; // format dd/MM/yyyy
    private String updatedAt; // format dd/MM/yyyy
}
