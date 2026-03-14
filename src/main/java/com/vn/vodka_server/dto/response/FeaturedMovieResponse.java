package com.vn.vodka_server.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO phản hồi chứa thông tin cơ bản của phim nổi bật.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedMovieResponse {
    // ID của phim
    private String id;

    // Tên phim
    private String title;

    // URL của poster phim
    private String posterUrl;

    // URL của banner phim
    private String bannerUrl;

    // Năm phát hành
    private int releaseYear;

    // Danh sách thể loại
    private List<GenreResponse> genre;

    // Đánh giá
    private double rating;

    // Danh sách tag
    private List<TagResponse> tags;

    // Mô tả
    private String description;

}
