package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminMovieStatsResponse {
    private KpiItem movie;
    private KpiItem rating;
    private KpiItem views;
    private KpiItem favorites;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KpiItem {
        private double value;
        private double trend;
    }
}
