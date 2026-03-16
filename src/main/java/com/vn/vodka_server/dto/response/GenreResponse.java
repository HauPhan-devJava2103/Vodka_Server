package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO phản hồi chứa thông tin cơ bản của thể loại phim.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponse {
    // ID của thể loại
    private String id;

    // Tên của thể loại
    private String name;
}
