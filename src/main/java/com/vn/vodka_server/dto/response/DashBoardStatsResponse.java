package com.vn.vodka_server.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardStatsResponse {
    private long totalMovies;
    private TrendInfoDB moviesTrend;

    private long totalUsers;
    private TrendInfoDB usersTrend;

    private long totalReviews;
    private TrendInfoDB reviewsTrend;

    private long totalTags;
    private long totalViews;
    private TrendInfoDB viewsTrend;

    private List<TopMovieInfo> topMovies;
    private List<DailyViewInfo> dailyViews;

    @Data
    @Builder
    public static class TrendInfoDB {
        private String value;
        private String direction;
    }

    @Data
    @Builder
    public static class TopMovieInfo {
        private String title;
        private long viewCount;
    }

    @Data
    @Builder
    public static class DailyViewInfo {
        private String date;
        private long count;
    }

}
