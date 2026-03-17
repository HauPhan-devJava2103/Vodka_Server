package com.vn.vodka_server.controller;

import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.service.MovieService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // Lấy danh sách phim nổi bật
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse> getFeaturedMovies() {
        return ResponseEntity.ok(ApiResponse.success("Success",
                movieService.getFeaturedMovies()));
    }

    // Lấy danh sách phim thịnh hành
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse> getTrendingMovies(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Success",
                movieService.getTrendingMovies(limit)));
    }

    // API9: Lấy chi tiết phim
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết phim thành công",
                    movieService.getMovieById(id)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // API10: Lấy danh sách đánh giá
    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse> getReviews(@PathVariable Long id, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            return ResponseEntity.ok(movieService.getReviews(id, page, limit));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

}
