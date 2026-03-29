package com.vn.vodka_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.request.CreateMovieRequest;
import com.vn.vodka_server.dto.request.UpdateMovieRequest;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.service.MovieAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieAdminService movieAdminService;

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
}
