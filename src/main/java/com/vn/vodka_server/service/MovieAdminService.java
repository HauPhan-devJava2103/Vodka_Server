package com.vn.vodka_server.service;

import org.springframework.data.domain.Page;

import com.vn.vodka_server.dto.request.CreateMovieRequest;
import com.vn.vodka_server.dto.request.UpdateMovieRequest;
import com.vn.vodka_server.dto.response.AdminMovieDetailResponse;
import com.vn.vodka_server.dto.response.AdminMovieListResponse;
import com.vn.vodka_server.dto.response.AdminMovieStatsResponse;

public interface MovieAdminService {

    // Tạo mới phim (SERIES hoặc SINGLE) kèm seasons và episodes
    void createMovie(CreateMovieRequest request);

    // Cập nhật phim đã tồn tại (merge seasons/episodes)
    void updateMovie(Long id, UpdateMovieRequest request);

    // Lấy chi tiết phim cho Admin
    AdminMovieDetailResponse getMovieDetail(Long id);

    // Lấy danh sách phim cho Admin (phân trang + lọc + sắp xếp)
    Page<AdminMovieListResponse> getMovieList(int page, int limit, String genreSlug,
            Integer year, Double minRating, String sort);

    // Xóa phim theo id
    void deleteMovie(Long id);

    // Lấy thống kê phim
    AdminMovieStatsResponse getMovieStats();
}
