package com.vn.vodka_server.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO phản hồi dành riêng cho API Filter Movies
// id của genre/tag trả về slug, duration trả về Integer.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterMovieResponse {
    // ID của phim
    private String id;

    // Tên phim
    private String title;

    // URL của poster phim
    private String posterUrl;

    // Năm phát hành
    private int releaseYear;

    // Đánh giá
    private double rating;

    // Thời lượng phim (lấy từ tập phim đầu tiên)
    private Integer duration;

    // Danh sách thể loại
    private List<GenreInfo> genre;

    // Danh sách tag
    private List<TagInfo> tags;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GenreInfo {
        private String id;
        private String name;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TagInfo {
        private String id;
        private String name;
    }
}
