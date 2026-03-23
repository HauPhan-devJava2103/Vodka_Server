package com.vn.vodka_server.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.vodka_server.dto.response.*;
import com.vn.vodka_server.exception.BadRequestException;
import com.vn.vodka_server.exception.ResourceNotFoundException;
import com.vn.vodka_server.model.*;
import com.vn.vodka_server.repository.*;
import com.vn.vodka_server.service.MovieService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

        private final MovieRepository movieRepository;
        private final SeasonRepository seasonRepository;
        private final ReviewRepository reviewRepository;
        private final WatchHistoryRepository watchHistoryRepository;
        private final GenreRepository genreRepository;
        private final UserRepository userRepository;
        private final EpisodeRepository episodeRepository;
        private final FavoriteRepository favoriteRepository;

        // Lấy 8 phim nổi bật nhất (8 phim có rating cao nhất)
        @Override
        public List<FeaturedMovieResponse> getFeaturedMovies() {
                List<Movie> featured = movieRepository.findTop8ByOrderByRatingDesc();
                if (featured == null)
                        throw new RuntimeException("Lỗi hệ thống khi truy vấn phim nổi bật");
                return featured.stream()
                                .map(this::mapToFeaturedMovieResponse)
                                .toList();
        }

        // Lấy phim thịnh hành nhất (phim có lượt xem cao nhất)
        @Override
        public List<TrendingMovieResponse> getTrendingMovies(int limit) {
                if (limit <= 0)
                        throw new BadRequestException("Số lượng phim phải lớn hơn 0");
                if (limit > 50)
                        throw new BadRequestException("Không thể lấy quá 50 phim một lần");
                return movieRepository.findTrendingMovies(Limit.of(limit)).stream()
                                .map(this::mapToTrendingMovieResponse)
                                .toList();
        }

        // Tìm phim hot (nhiều view nhất) có phân trang
        // Service chỉ trả về Page<DTO> thô, không đóng gói ApiResponse
        // Việc đóng gói ApiResponse + PaginationMeta sẽ do Controller làm
        @Override
        public Page<TrendingMovieResponse> getNewReleases(int page, int limit) {
                // Chặn dữ liệu rác ngay từ đầu
                if (page < 1)
                        throw new BadRequestException("Số trang phải bắt đầu từ 1");
                // 1. Khởi tạo phân trang
                Pageable pageable = PageRequest.of(page - 1, limit);

                // 2. Truy vấn dữ liệu từ Repository
                Page<Movie> moviePage = movieRepository.findAllByOrderByViewCountDesc(pageable);

                // Báo lỗi 404 nếu khách vào trang không tồn tại
                if (moviePage.isEmpty())
                        throw new ResourceNotFoundException("Trang phim số " + page + " hiện không có dữ liệu.");

                // 3. Dùng .map() của Page để chuyển đổi Entity -> DTO
                // Page.map() tự động giữ nguyên thông tin phân trang (totalItems,
                // totalPages...)
                return moviePage.map(this::mapToTrendingMovieResponse);
        }

        // Lấy chi tiết phim
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

        // API5: Lấy lịch sử xem phim của user
        @Override
        public List<TrendingMovieResponse> getWatchHistory(String email, int limit) {

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException(
                                                "Không tìm thấy tài khoản với email: " + email));

                // Lấy 5 phim mới đc xem gần đây(fix cứng)
                List<WatchHistory> histories = watchHistoryRepository
                                .findByUserIdOrderByWatchedAtDesc(user.getId(), Limit.of(5));

                return histories.stream()
                                .map(WatchHistory::getMovie)
                                .map(this::mapToTrendingMovieResponse)
                                .toList();
        }

        // API6: Lấy phim mới cập nhật gần đây, Sắp xếp theo trường updatedAt giảm dần
        @Override
        public List<TrendingMovieResponse> getRecentlyUpdated(int limit) {
                // Kiểm tra limit hợp lệ
                if (limit <= 0)
                        throw new BadRequestException("Số lượng phim phải lớn hơn 0");
                if (limit > 20)
                        throw new BadRequestException("Không thể lấy quá 50 phim một lần");

                // Lấy 8 phim mới cập nhật gần đây(fix cứng)
                return movieRepository.findAllByOrderByUpdatedAtDesc(Limit.of(8))
                                .stream()
                                .map(this::mapToTrendingMovieResponse)
                                .toList();
        }

        // API7: Lấy phim đánh giá cao nhất, Sắp xếp theo trường rating giảm dần
        @Override
        public List<TrendingMovieResponse> getHighlyRatedMovies(int limit) {
                // Kiểm tra limit hợp lệ
                if (limit <= 0)
                        throw new BadRequestException("Số lượng phim phải lớn hơn 0");
                if (limit > 50)
                        throw new BadRequestException("Không thể lấy quá 50 phim một lần");

                // Lấy 10 phim có đánh giá cao nhất(fix cứng)
                return movieRepository.findHighlyRatedMovies(Limit.of(10))
                                .stream()
                                .map(this::mapToTrendingMovieResponse)
                                .toList();
        }

        // API8: Lọc phim theo thể loại
        @Override
        public List<TrendingMovieResponse> getMoviesByGenre(Long genreId, int limit) {
                // Kiểm tra limit hợp lệ
                if (limit <= 0)
                        throw new BadRequestException("Số lượng phim phải lớn hơn 0");
                if (limit > 50)
                        throw new BadRequestException("Không thể lấy quá 50 phim một lần");

                // Kiểm tra thể loại có tồn tại trong DB không
                if (!genreRepository.existsById(genreId))
                        throw new ResourceNotFoundException("Thể loại với id = " + genreId + " không tồn tại");

                // Lấy 20 phim theo thể loại(fix cứng)
                return movieRepository.findByGenreId(genreId, Limit.of(20))
                                .stream()
                                .map(this::mapToTrendingMovieResponse)
                                .toList();
        }

        // API11: Lấy dữ liệu trang xem phim theo episodeId
        @Override
        public WatchMovieResponse getWatchData(Long episodeId) {
                // Bước 1: Tìm thông tin tập phim hiện tại
                Episode episode = episodeRepository.findById(episodeId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy tập phim với id = " + episodeId));

                // Bước 2: Lấy thông tin phim từ tập phim
                Season currentSeason = episode.getSeason();
                Movie movie = currentSeason.getMovie();

                // Bước 3: Lấy danh sách các mùa và tập của phim đỏ
                List<Season> seasons = seasonRepository
                                .findByMovieIdOrderBySeasonNumberAsc(movie.getId());

                // Bước 4: Lấy 10 đánh giá mới nhất
                List<Review> reviews = reviewRepository
                                .findTop10ByMovieIdOrderByCreatedAtDesc(movie.getId());

                // Bước 5: Lấy danh sách phim liên quan cùng thể loại
                Set<Long> genreIds = movie.getGenres().stream()
                                .map(Genre::getId)
                                .collect(Collectors.toSet());
                List<Movie> relatedMovies = movieRepository
                                .findRelatedMovies(genreIds, movie.getId(), Limit.of(10));

                // Bước 6: Đếm tổng số đánh giá của phim
                Long totalReviews = reviewRepository.countByMovieId(movie.getId());

                // Bước 7: Đóng gói dữ liệu trả về DTO
                return WatchMovieResponse.builder()
                                .movie(WatchMovieResponse.MovieInfo.builder()
                                                .id(movie.getId())
                                                .title(movie.getTitle())
                                                .build())
                                .currentEpisode(WatchMovieResponse.CurrentEpisodeInfo.builder()
                                                .id(episode.getId())
                                                .title(episode.getTitle())
                                                .duration(episode.getDuration())
                                                .videoUrl(episode.getVideoUrl())
                                                .description(episode.getDescription())
                                                .build())
                                .seasons(mapSeasonToSeasonResponse(seasons))
                                .reviews(mapReviewToReviewResponse(reviews))
                                .relatedMovies(relatedMovies.stream()
                                                .map(this::mapToRelatedMovieInfo)
                                                .toList())
                                .stats(WatchMovieResponse.MovieStatsInfo.builder()
                                                .totalViews(movie.getViewCount())
                                                .totalReviews(totalReviews)
                                                .build())
                                .build();
        }

        // API12: Lọc phim đa điều kiện bằng Slug
        @Override
        public Page<FilterMovieResponse> filterMovies(String keyword, String tagSlug,
                        List<String> genreSlugs, int page, int pageSize) {

                // 1. Validate phân trang
                if (page < 1)
                        throw new BadRequestException("Số trang phải bắt đầu từ 1");
                if (pageSize <= 0 || pageSize > 50)
                        throw new BadRequestException("Kích thước trang phải từ 1 tới 50");

                // 2. Chuẩn bị tham số an toàn (tránh chuỗi rỗng)
                String safeKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
                String safeTagSlug = (tagSlug != null && !tagSlug.trim().isEmpty()) ? tagSlug.trim() : null;
                List<String> safeGenreSlugs = (genreSlugs != null && !genreSlugs.isEmpty()) ? genreSlugs : null;

                // 3. Khởi tạo phân trang (sắp xếp ngầm định có thể là theo ID hoặc criteria
                // khác, hiện để trống)
                Pageable pageable = PageRequest.of(page - 1, pageSize);

                // 4. Query DB
                Page<Movie> moviePage = movieRepository.findFilteredMovies(
                                safeKeyword, safeTagSlug, safeGenreSlugs, pageable);

                // 5. Map sang DTO
                return moviePage.map(this::mapToFilterMovieResponse);
        }

        // HELPER
        // Mapper Movie sang FeaturedMovieResponse
        private FeaturedMovieResponse mapMovieToFeaturedMovieResponse(Movie movie) {
                return FeaturedMovieResponse.builder()
                                .id(movie.getId())
                                .title(movie.getTitle())
                                .posterUrl(movie.getPostUrl())
                                .bannerUrl(movie.getBannerUrl())
                                .releaseYear(movie.getReleaseYear())
                                .rating(movie.getRating())
                                .genre(movie.getGenres().stream()
                                                .map(this::mapToGenreResponse)
                                                .collect(Collectors.toList()))
                                .tags(movie.getTags().stream()
                                                .map(this::mapToTagResponse)
                                                .collect(Collectors.toList()))
                                .description(movie.getDescription())
                                .build();
        }

        // Mapper List<Review> sang List<ReviewResponse>
        private List<SeasonResponse> mapSeasonToSeasonResponse(List<Season> seasons) {
                List<SeasonResponse> response = seasons.stream()
                                .map(s -> SeasonResponse.builder()
                                                .id(s.getId())
                                                .title(s.getTitle())
                                                .thumbnailUrl(s.getThumbnailUrl())
                                                .episodes(s.getEpisodes().stream()
                                                                .map(e -> EpisodeResponse.builder()
                                                                                .id(e.getId())
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
                                                .id(r.getId())
                                                .userName(r.getUser().getFullName())
                                                .avatarUrl(r.getUser().getAvatarUrl())
                                                .rating(r.getRating())
                                                .content(r.getContent())
                                                // Format ngày giờ sang chuẩn ISO 8601
                                                .createdAt(r.getCreatedAt() != null
                                                                ? r.getCreatedAt().toInstant().toString()
                                                                : null)
                                                .build())

                                .toList();
                return response;
        }

        // Mapper List<Movie> sang List<TrendingMovieResponse>
        private TrendingMovieResponse mapToTrendingMovieResponse(Movie m) {
                return TrendingMovieResponse.builder()
                                .id(m.getId())
                                .title(m.getTitle())
                                .build();
        }

        // Mapper Genre sang GenreResponse
        private GenreResponse mapToGenreResponse(Genre genre) {
                return GenreResponse.builder()
                                .id(genre.getId())
                                .name(genre.getName())
                                .slug(genre.getSlug())
                                .build();
        }

        // Mapper Tag sang TagResponse
        private TagResponse mapToTagResponse(Tag tag) {
                return TagResponse.builder()
                                .id(tag.getId())
                                .name(tag.getName())
                                .slug(tag.getSlug())
                                .build();
        }

        // Mapper Movie sang FeaturedMovieResponse
        private FeaturedMovieResponse mapToFeaturedMovieResponse(Movie movie) {
                return FeaturedMovieResponse.builder()
                                .id(movie.getId())
                                .title(movie.getTitle())
                                .posterUrl(movie.getPostUrl())
                                .bannerUrl(movie.getBannerUrl())
                                .releaseYear(movie.getReleaseYear())
                                .rating(movie.getRating())
                                .description(movie.getDescription())
                                // Map danh sách Genre
                                .genre(movie.getGenres().stream()
                                                .map(this::mapToGenreResponse)
                                                .toList())
                                // Map danh sách Tag
                                .tags(movie.getTags().stream()
                                                .map(this::mapToTagResponse)
                                                .toList())
                                .build();
        }

        // API11: Mapper Movie sang RelatedMovieInfo
        private WatchMovieResponse.RelatedMovieInfo mapToRelatedMovieInfo(Movie movie) {
                return WatchMovieResponse.RelatedMovieInfo.builder()
                                .id(movie.getId())
                                .title(movie.getTitle())
                                .posterUrl(movie.getPostUrl())
                                .releaseYear(movie.getReleaseYear())
                                .rating(movie.getRating())
                                .genre(movie.getGenres().stream()
                                                .map(this::mapToGenreResponse)
                                                .collect(Collectors.toList()))
                                .tags(movie.getTags().stream()
                                                .map(this::mapToTagResponse)
                                                .collect(Collectors.toList()))
                                .build();
        }

        // API12 Filter: Mapper Movie sang FilterMovieResponse
        private FilterMovieResponse mapToFilterMovieResponse(Movie movie) {
                // Lấy thời lượng từ tập 1 của phần 1
                Double firstEpisodeDuration = null;
                if (movie.getSeasons() != null && !movie.getSeasons().isEmpty()) {
                        Season firstSeason = movie.getSeasons().get(0);
                        if (firstSeason.getEpisodes() != null && !firstSeason.getEpisodes().isEmpty()) {
                                firstEpisodeDuration = firstSeason.getEpisodes().get(0).getDuration();
                        }
                }

                return FilterMovieResponse.builder()
                                .id(movie.getId())
                                .title(movie.getTitle())
                                .posterUrl(movie.getPostUrl())
                                .releaseYear(movie.getReleaseYear() != null ? movie.getReleaseYear() : 0)
                                .rating(movie.getRating() != null ? movie.getRating() : 0.0)
                                // Trả về Integer
                                .duration(firstEpisodeDuration != null ? firstEpisodeDuration.intValue() : null)
                                // Trả về slug làm id cho Genre
                                .genre(movie.getGenres().stream()
                                                .map(g -> new FilterMovieResponse.GenreInfo(g.getSlug(), g.getName()))
                                                .toList())
                                // Trả về slug làm id cho Tag
                                .tags(movie.getTags().stream()
                                                .map(t -> new FilterMovieResponse.TagInfo(t.getSlug(), t.getName()))
                                                .toList())
                                .build();
        }

        // Toggle yêu thích phim: thêm nếu chưa yêu thích, xóa nếu đã yêu thích
        @Override
        @Transactional
        public String toggleFavorite(String email, Long movieId) {

                // Tìm user theo email
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy tài khoản với email: " + email));

                // Tìm phim theo id
                Movie movie = movieRepository.findById(movieId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy phim với id = " + movieId));

                Optional<Favorite> existing = favoriteRepository.findByUserIdAndMovieId(user.getId(), movieId);

                if (existing.isPresent()) {
                        // Đã yêu thích → bỏ yêu thích và giảm counter
                        favoriteRepository.delete(existing.get());
                        if (movie.getFavorites() != null && movie.getFavorites() > 0) {
                                movie.setFavorites(movie.getFavorites() - 1);
                        }
                        movieRepository.save(movie);
                        return "Đã bỏ yêu thích phim";
                } else {
                        // Chưa yêu thích → thêm vào và tăng counter
                        Favorite favorite = Favorite.builder()
                                        .user(user)
                                        .movie(movie)
                                        .build();
                        favoriteRepository.save(favorite);
                        movie.setFavorites(movie.getFavorites() == null ? 1L : movie.getFavorites() + 1);
                        movieRepository.save(movie);
                        return "Đã thêm phim vào danh sách yêu thích";
                }
        }

        // Lấy danh sách phim yêu thích của user, có phân trang
        @Override
        public Page<FavoriteMovieResponse> getFavorites(String email, int page, int limit) {

                if (page < 1)
                        throw new BadRequestException("Số trang phải bắt đầu từ 1");

                // Tìm user theo email
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy tài khoản với email: " + email));

                Pageable pageable = PageRequest.of(page - 1, limit);

                // Lấy danh sách yêu thích, mới nhất lên đầu
                Page<Favorite> favoritePage = favoriteRepository
                                .findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

                // Map sang DTO
                return favoritePage.map(fav -> FavoriteMovieResponse.builder()
                                .id(String.valueOf(fav.getId()))
                                .movieId(String.valueOf(fav.getMovie().getId()))
                                .title(fav.getMovie().getTitle())
                                .posterUrl(fav.getMovie().getPostUrl())
                                .rating(fav.getMovie().getRating())
                                .addedAt(fav.getCreatedAt() != null
                                                ? fav.getCreatedAt().toInstant().toString()
                                                : null)
                                .build());
        }
}
