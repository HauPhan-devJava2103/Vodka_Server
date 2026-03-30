package com.vn.vodka_server.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.response.DashBoardStatsResponse;
import com.vn.vodka_server.dto.response.DashBoardStatsResponse.DailyViewInfo;
import com.vn.vodka_server.dto.response.DashBoardStatsResponse.TopMovieInfo;
import com.vn.vodka_server.dto.response.DashBoardStatsResponse.TrendInfoDB;
import com.vn.vodka_server.model.Movie;
import com.vn.vodka_server.repository.MovieRepository;
import com.vn.vodka_server.repository.ReviewRepository;
import com.vn.vodka_server.repository.TagRepository;
import com.vn.vodka_server.repository.UserRepository;
import com.vn.vodka_server.repository.WatchHistoryRepository;
import com.vn.vodka_server.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final TagRepository tagRepository;
    private final WatchHistoryRepository watchHistoryRepository;

    @Override
    public DashBoardStatsResponse getDashboardStats() {
        // Time
        LocalDateTime endOfThisMonth = LocalDateTime.now();
        LocalDateTime startOfThisMonth = endOfThisMonth.toLocalDate().atStartOfDay().withDayOfMonth(1);
        LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfThisMonth;

        long totalMovies = movieRepository.count();
        long totalUsers = userRepository.count();
        long totalReviews = reviewRepository.count();
        long totalTags = tagRepository.count();
        long totalViews = movieRepository.sumViewCount();

        // Trend
        long moviesThisMonth = movieRepository.countByCreatedAtBetween(startOfThisMonth, endOfThisMonth);
        long moviesLastMonth = movieRepository.countByCreatedAtBetween(startOfLastMonth, endOfLastMonth);
        long usersThisMonth = userRepository.countByCreatedAtBetween(startOfThisMonth, endOfThisMonth);
        long usersLastMonth = userRepository.countByCreatedAtBetween(startOfLastMonth, endOfLastMonth);
        long reviewsThisMonth = reviewRepository.countByCreatedAtBetween(startOfThisMonth, endOfThisMonth);
        long reviewsLastMonth = reviewRepository.countByCreatedAtBetween(startOfLastMonth, endOfLastMonth);
        long viewsThisMonth = watchHistoryRepository.countByWatchedAtBetween(startOfThisMonth, endOfThisMonth);
        long viewsLastMonth = watchHistoryRepository.countByWatchedAtBetween(startOfLastMonth, endOfLastMonth);

        TrendInfoDB moviesTrend = buildTrend(moviesThisMonth, moviesLastMonth, false);
        TrendInfoDB usersTrend = buildTrend(usersThisMonth, usersLastMonth, false);
        TrendInfoDB reviewsTrend = buildTrend(reviewsThisMonth, reviewsLastMonth, false);
        TrendInfoDB viewsTrend = buildTrend(viewsThisMonth, viewsLastMonth, true);

        // Top Movies
        List<Movie> trendingMovies = movieRepository.findTrendingMovies(Limit.of(5));
        List<TopMovieInfo> topMovies = trendingMovies.stream()
                .map(m -> TopMovieInfo.builder()
                        .title(m.getTitle())
                        .viewCount(m.getViewCount())
                        .build())
                .toList();

        // 6. Daily views (7 ngày gần nhất)
        LocalDateTime from7DaysAgo = LocalDateTime.now()
                .minusDays(6)
                .truncatedTo(ChronoUnit.DAYS);

        List<Object[]> rawDailyViews = watchHistoryRepository.countDailyViewsSince(from7DaysAgo);

        List<DailyViewInfo> dailyViews = rawDailyViews.stream()
                .map(row -> DailyViewInfo.builder()
                        .date(row[0].toString()) // DATE() trả về String dạng "yyyy-MM-dd"
                        .count(((Number) row[1]).longValue())
                        .build())
                .toList();

        return DashBoardStatsResponse.builder()
                .totalMovies(totalMovies)
                .moviesTrend(moviesTrend)
                .totalUsers(totalUsers)
                .usersTrend(usersTrend)
                .totalReviews(totalReviews)
                .reviewsTrend(reviewsTrend)
                .totalTags(totalTags)
                .totalViews(totalViews)
                .viewsTrend(viewsTrend)
                .topMovies(topMovies)
                .dailyViews(dailyViews)
                .build();

    }

    // HELPER
    private TrendInfoDB buildTrend(long thisMonth, long lastMonth, boolean usePercent) {
        long diff = thisMonth - lastMonth;
        String value;
        if (usePercent) {
            if (lastMonth == 0) {
                value = diff >= 0 ? "+100%" : "0%";
            } else {
                long percent = Math.round((double) diff / lastMonth * 100);
                value = formatTrend(percent) + "%";
            }
        } else {
            value = formatTrend(diff);
        }
        return TrendInfoDB.builder()
                .value(value)
                .direction(diff >= 0 ? "up" : "down")
                .build();
    }

    private String formatTrend(long diff) {
        return diff >= 0 ? "+" + diff : String.valueOf(diff);
    }

}
