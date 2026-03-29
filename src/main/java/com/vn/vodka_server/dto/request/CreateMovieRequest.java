package com.vn.vodka_server.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMovieRequest {

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

    // Đại diện cho 1 thể loại mà frontend gửi lên
    // Fe gửi cả {id, name, slug} nhưng server chỉ dùng "id"
    @Data
    public static class GenreInput {
        private Long id;
    }

    // Đại diện cho 1 tag mà frontend gửi lên
    // Fe gửi cả {id, name, slug} nhưng server chỉ dùng "id"
    @Data
    public static class TagInput {
        private Long id;
    }

    // Đại diện cho 1 mùa phim (season) trong payload
    @Data
    public static class SeasonRequest {
        private Integer seasonNumber;
        private String title;
        private List<EpisodeRequest> episodes;
    }

    // Đại diện cho 1 tập phim (episode) trong payload
    @Data
    public static class EpisodeRequest {
        private Integer episodeNumber;
        private String title;
        private String description;
        private String videoUrl;
        private Double duration;
    }
}
