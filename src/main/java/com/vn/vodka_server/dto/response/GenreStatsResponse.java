package com.vn.vodka_server.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreStatsResponse {

    private long totalGenres;
    private MostPopularGenre mostPopularGenre;
    private long unclassifiedMovies;
    private LatestGenre latestGenre;

    @Data
    @Builder
    public static class MostPopularGenre {
        private String name;
        private long movieCount;
    }

    @Data
    @Builder
    public static class LatestGenre {
        private String name;
        private String createdAt; // "2 ngày trước", "Hôm nay"...
    }
}
