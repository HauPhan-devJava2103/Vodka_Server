package com.vn.vodka_server.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.request.CreateReviewRequest;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.FeaturedMovieResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.service.MovieService;
import com.vn.vodka_server.util.PaginationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // API1: Lấy danh sách phim nổi bật
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse> getFeaturedMovies() {
        return ResponseEntity.ok(ApiResponse.success("Success",
                movieService.getFeaturedMovies()));
    }

    // API3: Tìm phim hot (nhiều view nhất) có phân trang
    @GetMapping("/new-releases")
    public ResponseEntity<ApiResponse> getNewReleases(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        // 1. Nhận Page thô từ Service
        Page<FeaturedMovieResponse> resultPage = movieService.getNewReleases(page, limit);

        // 2. Controller đóng gói metadata phân trang
        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, page);

        // 3. Trả về ApiResponse thống nhất
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Success")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }

    // API4: Lấy danh sách phim thịnh hành
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse> getTrendingMovies(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Success",
                movieService.getTrendingMovies(limit)));
    }

    // API5: Lấy lịch sử xem phim của user
    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getWatchHistory(
            Principal principal,
            @RequestParam(defaultValue = "5") int limit) {
        String email = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(ApiResponse.success("Success",
                movieService.getWatchHistory(email, limit)));
    }

    // API6: Lấy phim mới cập nhật gần đây
    @GetMapping("/recently-updated")
    public ResponseEntity<ApiResponse> getRecentlyUpdated(
            @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Success",
                movieService.getRecentlyUpdated(limit)));
    }

    // API7: Lấy phim đánh giá cao nhất
    @GetMapping("/highly-rated")
    public ResponseEntity<ApiResponse> getHighlyRatedMovies(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Success",
                movieService.getHighlyRatedMovies(limit)));
    }

    // API8: Lọc phim theo thể loại
    @GetMapping("/genre/{genreName}")
    public ResponseEntity<ApiResponse> getMoviesByGenre(
            @PathVariable String genreName,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Lấy phim theo thể loại thành công",
                movieService.getMoviesByGenre(genreName, limit)));
    }

    // API9: Lấy chi tiết phim
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết phim thành công",
                    movieService.getMovieById(id)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // API10: Lấy danh sách đánh giá
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse> getReviews(@PathVariable Long id, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            return ResponseEntity.ok(movieService.getReviews(id, page, limit));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // API11: Lấy dữ liệu xem phim theo episodeId
    @GetMapping("/watch/{episodeId}")
    public ResponseEntity<ApiResponse> getWatchData(@PathVariable Long episodeId) {
        return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu xem phim thành công",
                movieService.getWatchData(episodeId)));

    }

    // API12: Lọc phim đa điều kiện bằng Slug
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> filterMovies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) java.util.List<String> genres,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int pageSize) {

        Page<FeaturedMovieResponse> resultPage = movieService.filterMovies(
                keyword, tag, genres, page, pageSize);

        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, page);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách phim thành công")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }

    // POST /api/movies/reviews — Tạo review gốc hoặc reply bình luận
    // Yêu cầu đăng nhập (JWT). Email lấy từ Principal do Spring Security inject
    // replyToId == null -> tạo review gốc -> trả ReviewResponse
    // replyToId != null -> tạo reply -> trả ReviewResponse.ReplyInfo
    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse> createReview(
            @RequestBody @Valid CreateReviewRequest request,
            Principal principal) {
        Object result = movieService.createReview(request, principal.getName());

        // Xác định message theo loại (review gốc hay reply)
        String message = request.getReplyToId() == null
                ? "Thêm đánh giá thành công"
                : "Phản hồi bình luận thành công";

        return ResponseEntity.ok(ApiResponse.success(message, result));
    }

    // Thêm xóa Phim yêu thích
    @PostMapping("/{id}/favorite")
    public ResponseEntity<ApiResponse> toggleFavorite(
            @PathVariable Long id,
            Principal principal) {
        try {
            boolean isFavorited = movieService.toggleFavorite(id, principal.getName());
            return ResponseEntity
                    .ok(ApiResponse.success("Cập nhật danh sách yêu thích thành công",
                            Map.of("isFavorited", isFavorited)));

        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }

    }

    // Kiểm tra phim đã yêu thích hay chưa
    @GetMapping("/{id}/favorite-status")
    public ResponseEntity<ApiResponse> getFavoriteStatus(
            @PathVariable Long id,
            Principal principal) {

        String email = principal != null ? principal.getName() : null;

        boolean isFavorite = movieService.checkIsFavorite(id, email);

        return ResponseEntity.ok(ApiResponse.success("Lấy trạng thái yêu thích thành công", isFavorite));
    }
}
