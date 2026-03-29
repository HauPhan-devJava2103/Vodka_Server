package com.vn.vodka_server.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewStatsResponse {

    private long totalReviews;
    private double averageRating;
    private long moviesWithReviews;
    private long totalReplies;
    private TrendInfo trends;

    @Data
    @Builder
    public static class TrendInfo {
        private double reviewsTrendPercent;
        private double repliesTrendPercent;
    }
}
