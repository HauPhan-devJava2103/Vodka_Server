package com.vn.vodka_server.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponse {

    private Long id;
    private String userName;
    private String avatarUrl;
    private Double rating;
    private String content;
    private String createdAt;

    // Danh sách reply của review này
    private List<ReplyInfo> replied;

    // Movie
    private FeaturedMovieResponse movie;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyInfo {
        private Long id;
        private String userName;
        private String avatarUrl;
        private String content;
        private String createdAt;
    }
}
