package com.vn.vodka_server.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.response.ActivityResponse;
import com.vn.vodka_server.dto.response.DashBoardStatsResponse;
import com.vn.vodka_server.dto.response.DashBoardStatsResponse.DailyViewInfo;
import com.vn.vodka_server.dto.response.DashBoardStatsResponse.TopMovieInfo;
import com.vn.vodka_server.dto.response.DashBoardStatsResponse.TrendInfoDB;
import com.vn.vodka_server.model.Genre;
import com.vn.vodka_server.model.Movie;
import com.vn.vodka_server.model.Review;
import com.vn.vodka_server.model.Tag;
import com.vn.vodka_server.repository.GenreRepository;
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
        private final GenreRepository genreRepository;

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

        @Override
        public Page<ActivityResponse> getActivities(String entityType, String search, int page, int pageSize) {
                Limit limit = Limit.of(50);

                List<Review> reviews = reviewRepository.findByOrderByUpdatedAtDesc(limit);
                List<Movie> movies = movieRepository.findTopByOrderByUpdatedAtDesc(limit);
                List<Genre> genres = genreRepository.findTopByOrderByUpdatedAtDesc(limit);
                List<Tag> tags = tagRepository.findTopByOrderByUpdatedAtDesc(limit);

                Stream<ActivityResponse> reviewStream = reviews.stream().map(this::mapReviewToActivity);
                Stream<ActivityResponse> movieStream = movies.stream()
                                .map(m -> buildAdminActivity(m.getId(), "Movie", m.getTitle(), m.getCreatedAt(),
                                                m.getUpdatedAt()));
                Stream<ActivityResponse> genreStream = genres.stream()
                                .map(g -> buildAdminActivity(g.getId(), "Genre", g.getName(), g.getCreatedAt(),
                                                g.getUpdatedAt()));
                Stream<ActivityResponse> tagStream = tags.stream()
                                .map(t -> buildAdminActivity(t.getId(), "Tag", t.getName(), t.getCreatedAt(),
                                                t.getUpdatedAt()));

                // Nối Stream
                Stream<ActivityResponse> stream = Stream.of(reviewStream, movieStream, genreStream, tagStream)
                                .flatMap(Function.identity());
                // Filter and Search
                if (entityType != null && !entityType.isBlank()) {
                        stream = stream.filter(str -> str.getEntityType().equalsIgnoreCase(entityType));
                }
                if (search != null && !search.isBlank()) {
                        String s = search.toLowerCase();
                        stream = stream.filter(str -> str.getTargetName().toLowerCase().contains(s)
                                        || str.getActorName().toLowerCase().contains(s));
                }

                List<ActivityResponse> all = stream
                                .sorted(Comparator.comparing(ActivityResponse::getUpdatedAt).reversed()).toList();

                int safePage = Math.max(page - 1, 0);
                Pageable pageable = PageRequest.of(safePage, pageSize);
                int start = (int) pageable.getOffset();
                if (start >= all.size()) {
                        return new PageImpl<>(List.of(), pageable, all.size());
                }
                int end = Math.min(start + pageSize, all.size());
                List<ActivityResponse> pageData = all.subList(start, end);

                return new PageImpl<>(pageData, pageable, all.size());

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

        private ActivityResponse mapReviewToActivity(Review r) {
                return ActivityResponse.builder()
                                .id(r.getId())
                                .actorName(r.getUser().getFullName())
                                .actorAvatar(r.getUser().getAvatarUrl())
                                .entityType("Review")
                                .targetName(r.getMovie().getTitle())
                                .createdAt(r.getCreatedAt().toString())
                                .updatedAt(r.getUpdatedAt().toString())
                                .build();
        }

        // Dùng chung cho Movie, Genre, Tag
        private ActivityResponse buildAdminActivity(Long id, String entityType, String targetName, Object createdAt,
                        Object updatedAt) {
                return ActivityResponse.builder()
                                .id(id)
                                .actorName("Admin")
                                .actorAvatar(null)
                                .entityType(entityType)
                                .targetName(targetName)
                                .createdAt(createdAt.toString())
                                .updatedAt(updatedAt.toString())
                                .build();
        }

}
