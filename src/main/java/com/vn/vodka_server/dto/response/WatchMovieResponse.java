package com.vn.vodka_server.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Add API11: DTO phản hồi cho trang xem phim
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WatchMovieResponse {

    private MovieInfo movie;
    private CurrentEpisodeInfo currentEpisode;
    private List<SeasonResponse> seasons;
    private List<ReviewResponse> reviews;
    private List<RelatedMovieInfo> relatedMovies;
    private MovieStatsInfo stats;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MovieInfo {
        private String id;
        private String title;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurrentEpisodeInfo {
        private String id;
        private String title;
        private Double duration;
        private String videoUrl;
        private String description;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelatedMovieInfo {
        private String id;
        private String title;
        private Integer releaseYear;
        private Double rating;
        private String posterUrl;
        private List<GenreResponse> genre;
        private List<TagResponse> tags;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MovieStatsInfo {
        private Long totalViews;
        private Long totalReviews;
    }
}
