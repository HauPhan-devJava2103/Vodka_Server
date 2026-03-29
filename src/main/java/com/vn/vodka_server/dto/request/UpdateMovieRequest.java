package com.vn.vodka_server.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMovieRequest {

    @NotBlank(message = "Tên phim không được để trống")
    private String title;

    private String description;

    private Integer releaseYear;

    private String posterUrl;

    private String bannerUrl;

    @NotBlank(message = "Loại phim không được để trống")
    private String movieType; // "SERIES" hoặc "SINGLE"

    private List<GenreInput> genres;

    private List<TagInput> tags;

    @NotEmpty(message = "Phim phải có ít nhất 1 season")
    @NotNull(message = "Danh sách season không được null")
    private List<SeasonRequest> seasons;

    // Fe gửi cả {id, name, slug} nhưng server chỉ dùng "id"
    @Data
    public static class GenreInput {
        private Long id;
    }

    // Fe gửi cả {id, name, slug} nhưng server chỉ dùng "id"
    @Data
    public static class TagInput {
        private Long id;
    }

    // Season có thêm field id so với CreateMovieRequest
    // id = null -> insert season mới
    // id != null -> update season đã tồn tại
    @Data
    public static class SeasonRequest {
        private Long id;
        private Integer seasonNumber;
        private String title;
        private List<EpisodeRequest> episodes;
    }

    // Episode có thêm field id so với CreateMovieRequest
    // id = null -> insert episode mới
    // id != null -> update episode đã tồn tại
    @Data
    public static class EpisodeRequest {
        private Long id;
        private Integer episodeNumber;
        private String title;
        private String description;
        private String videoUrl;
        private Double duration;
    }
}
