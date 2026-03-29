package com.vn.vodka_server.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagStatsResponse {

    private long totalTags;
    private MostPopularTag mostPopularTag;
    private long unclassifiedMovies;
    private LatestTag latestTag;

    @Data
    @Builder
    public static class MostPopularTag {
        private String name;
        private long movieCount;
    }

    @Data
    @Builder
    public static class LatestTag {
        private String name;
        private String createdAt; // "2 ngày trước", "Hôm nay"...
    }
}
