package com.vn.vodka_server.service;

import com.vn.vodka_server.dto.request.CreateMovieRequest;
import com.vn.vodka_server.dto.request.UpdateMovieRequest;
import com.vn.vodka_server.dto.response.AdminMovieDetailResponse;

public interface MovieAdminService {

    // Tạo mới phim (SERIES hoặc SINGLE) kèm seasons và episodes
    void createMovie(CreateMovieRequest request);

    // Cập nhật phim đã tồn tại (merge seasons/episodes)
    void updateMovie(Long id, UpdateMovieRequest request);

    // Lấy chi tiết phim cho Admin
    AdminMovieDetailResponse getMovieDetail(Long id);
}
