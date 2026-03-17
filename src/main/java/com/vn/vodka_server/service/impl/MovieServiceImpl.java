package com.vn.vodka_server.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.EpisodeResponse;
import com.vn.vodka_server.dto.response.FeaturedMovieResponse;
import com.vn.vodka_server.dto.response.GenreResponse;
import com.vn.vodka_server.dto.response.MovieDetailResponse;
import com.vn.vodka_server.dto.response.MovieStatsResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.dto.response.ReviewResponse;
import com.vn.vodka_server.dto.response.SeasonResponse;
import com.vn.vodka_server.dto.response.TagResponse;
import com.vn.vodka_server.dto.response.TrendingMovieResponse;
import com.vn.vodka_server.model.Movie;
import com.vn.vodka_server.model.Review;
import com.vn.vodka_server.model.Season;
import com.vn.vodka_server.repository.MovieRepository;
import com.vn.vodka_server.repository.ReviewRepository;
import com.vn.vodka_server.repository.SeasonRepository;
import com.vn.vodka_server.service.MovieService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

        private final MovieRepository movieRepository;
        private final SeasonRepository seasonRepository;
        private final ReviewRepository reviewRepository;

        // Lấy 10 phim nổi bật nhất (10 phim có rating cao nhất)
        @Override
        public List<FeaturedMovieResponse> getFeaturedMovies() {
                return movieRepository.findTop2ByOrderByRatingDesc().stream()
                                .map(movie -> FeaturedMovieResponse.builder()
                                                .id(String.valueOf(movie.getId()))
                                                .title(movie.getTitle())
                                                .posterUrl(movie.getPostUrl())
                                                .bannerUrl(movie.getBannerUrl())
                                                .releaseYear(movie.getReleaseYear())
                                                .rating(movie.getRating())

                                                // Map sang Object GenreResponse
                                                .genre(movie.getGenres().stream()
                                                                .map(genre -> new GenreResponse(
                                                                                String.valueOf(genre.getId()),
                                                                                genre.getName()))
                                                                .collect(Collectors.toList()))

                                                // Map sang Object TagResponse
                                                .tags(movie.getTags().stream()
                                                                .map(tag -> new TagResponse(String.valueOf(tag.getId()),
                                                                                tag.getName()))
                                                                .collect(Collectors.toList()))

                                                .description(movie.getDescription())
                                                .build())
                                .collect(Collectors.toList());
        }

        // Lấy phim thịnh hành nhất (phim có lượt xem cao nhất)
        @Override
        public List<TrendingMovieResponse> getTrendingMovies(int limit) {
                return movieRepository.findTrendingMovies(Limit.of(limit)).stream()
                                .map(movie -> TrendingMovieResponse.builder()
                                                .id(String.valueOf(movie.getId()))
                                                .title(movie.getTitle())
                                                .build())
                                .toList();
        }

        // Lấy chi tiết phim
        @Override
        public MovieDetailResponse getMovieById(Long id) {

                Movie movie = movieRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Phim không tồn tại"));

                // Lấy Seasons
                List<Season> season = seasonRepository.findByMovieIdOrderBySeasonNumberAsc(id);

                // Lấy Reviews
                List<Review> review = reviewRepository.findTop10ByMovieIdOrderByCreatedAtDesc(id);

                // Lấy phim gợi ý
                Set<Long> genreIds = movie.getGenres()
                                .stream()
                                .map(g -> g.getId()).collect(Collectors.toSet());
                List<Movie> relatedMovies = movieRepository.findRelatedMovies(genreIds, id, Limit.of(10));

                // Tổng số lượt xem
                Long totalReview = reviewRepository.countByMovieId(id);

                // Map Movie

                MovieDetailResponse response = MovieDetailResponse.builder()
                                .movie(mapMovieToFeaturedMovieResponse(movie))
                                .episodes(mapSeasonToSeasonResponse(season))
                                .reviews(mapReviewToReviewResponse(review))
                                .relatedMovies(relatedMovies.stream().map(this::mapMovieToFeaturedMovieResponse)
                                                .toList())
                                .stats(MovieStatsResponse.builder()
                                                .totalReviews(totalReview)
                                                .totalViews(movie.getViewCount())
                                                .favorites(movie.getFavorites())
                                                .build())
                                .build();

                return response;

        }

        @Override
        public ApiResponse getReviews(Long id, int page, int limit) {
                Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
                Page<Review> reviewPage = reviewRepository.findByMovieIdOrderByCreatedAtDesc(id, pageable);

                List<ReviewResponse> data = mapReviewToReviewResponse(reviewPage.getContent());

                PaginationMeta pagination = PaginationMeta.builder()
                                .totalItems(reviewPage.getTotalElements())
                                .totalPages(reviewPage.getTotalPages())
                                .currentPage(page)
                                .pageSize(limit)
                                .build();
                return ApiResponse.success("Lấy danh sách đánh giá thành công", data, pagination);
        }

        // HELPER
        // Mapper Movie sang FeaturedMovieResponse
        private FeaturedMovieResponse mapMovieToFeaturedMovieResponse(Movie movie) {
                return FeaturedMovieResponse.builder()
                                .id(movie.getId().toString())
                                .title(movie.getTitle())
                                .posterUrl(movie.getPostUrl())
                                .bannerUrl(movie.getBannerUrl())
                                .releaseYear(movie.getReleaseYear())
                                .rating(movie.getRating())
                                .genre(movie.getGenres().stream()
                                                .map(genre -> new GenreResponse(
                                                                String.valueOf(genre.getId()),
                                                                genre.getName()))
                                                .collect(Collectors.toList()))
                                .tags(movie.getTags().stream()
                                                .map(tag -> new TagResponse(String.valueOf(tag.getId()),
                                                                tag.getName()))
                                                .collect(Collectors.toList()))
                                .description(movie.getDescription())
                                .build();
        }

        // Mapper List<Review> sang List<ReviewResponse>
        private List<SeasonResponse> mapSeasonToSeasonResponse(List<Season> seasons) {
                List<SeasonResponse> response = seasons.stream()
                                .map(s -> SeasonResponse.builder()
                                                .id(String.valueOf(s.getId()))
                                                .title(s.getTitle())
                                                .thumbnailUrl(s.getThumbnailUrl())
                                                .episodes(s.getEpisodes().stream()
                                                                .map(e -> EpisodeResponse.builder()
                                                                                .id(String.valueOf(e.getId()))
                                                                                .title(e.getTitle())
                                                                                .duration(e.getDuration())
                                                                                .build())
                                                                .toList())
                                                .build())
                                .toList();
                return response;
        }

        // Mapper List<Review> sang List<ReviewResponse>
        private List<ReviewResponse> mapReviewToReviewResponse(List<Review> reviews) {
                List<ReviewResponse> response = reviews.stream()
                                .map(r -> ReviewResponse.builder()
                                                .id(String.valueOf(r.getId()))
                                                .userName(r.getUser().getFullName())
                                                .avatarUrl(r.getUser().getAvatarUrl())
                                                .rating(r.getRating())
                                                .content(r.getContent())
                                                .createdAt(r.getCreatedAt().toString())
                                                .build())
                                .toList();
                return response;
        }

}
