package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewReplyResponse {

    // ID của phản hồi
    private String id;

    // ID người viết phản hồi
    private String userId;

    // Tên người viết phản hồi
    private String userName;

    // Ảnh đại diện người viết
    private String avatarUrl;

    // Nội dung phản hồi
    private String content;

    // Thời gian tạo (ISO 8601)
    private String createdAt;
}
