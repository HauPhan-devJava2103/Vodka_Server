package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteMovieResponse {

    // ID bản ghi yêu thích
    private String id;

    // ID phim
    private String movieId;

    // Tiêu đề phim
    private String title;

    // Đường dẫn ảnh poster
    private String posterUrl;

    // Điểm đánh giá của phim
    private Double rating;

    // Thời gian người dùng thêm vào yêu thích (ISO 8601)
    private String addedAt;
}
