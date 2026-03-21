package com.vn.vodka_server.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private Long id;
    private String userName;
    private String avatarUrl;
    private Double rating;
    private String content;
    private String createdAt;

    // Danh sách phản hồi của review này
    private List<ReviewReplyResponse> replies;

}
