package com.vn.vodka_server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.request.CreateMovieRequest;
import com.vn.vodka_server.dto.request.UpdateMovieRequest;
import com.vn.vodka_server.dto.response.AdminMovieListResponse;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.service.MovieAdminService;
import com.vn.vodka_server.util.PaginationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieAdminService movieAdminService;

    // Lấy danh sách phim cho Admin
    @GetMapping
    public ResponseEntity<ApiResponse> getMovieList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Double rating,
            @RequestParam(defaultValue = "moi-nhat") String sort) {

        Page<AdminMovieListResponse> resultPage = movieAdminService.getMovieList(
                page, limit, genre, year, rating, sort);
        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, page);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách phim thành công")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }

    // Tạo mới phim (SERIES hoặc SINGLE)
    @PostMapping
    public ResponseEntity<ApiResponse> createMovie(
            @Valid @RequestBody CreateMovieRequest request) {
        movieAdminService.createMovie(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo phim thành công", null));
    }

    // Cập nhật phim đã tồn tại (merge seasons/episodes)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMovieRequest request) {
        movieAdminService.updateMovie(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật phim thành công", null));
    }

    // Lấy chi tiết phim cho Admin
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieDetail(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy thông tin chi tiết phim thành công",
                        movieAdminService.getMovieDetail(id)));
    }

    // Xóa phim theo id
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMovie(@PathVariable Long id) {
        movieAdminService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa phim thành công", null));
    }
}
