package com.vn.vodka_server.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.vn.vodka_server.dto.request.CreateGenreRequest;
import com.vn.vodka_server.dto.response.GenreResponse;
import com.vn.vodka_server.dto.response.GenreStatsResponse;

public interface GenreService {
    // Lấy danh sách tất cả các thể loại và chuyển đổi sang DTO
    List<GenreResponse> getAllGenres();

    // Admin1: Lấy danh sách genres có phân trang, tìm kiếm, sắp xếp
    Page<GenreResponse> getAdminGenres(int page, int pageSize, String search, String sort);

    // Admin2: Tạo mới thể loại
    GenreResponse createGenre(CreateGenreRequest request);

    // Admin3: Lấy chi tiết 1 genre theo ID
    GenreResponse getAdminGenreById(Long id);

    // Admin4: Cập nhật genre
    GenreResponse updateGenre(Long id, CreateGenreRequest request);

    // Admin5: Xóa genre
    void deleteGenre(Long id);

    // Admin6: Thống kê tổng quan
    GenreStatsResponse getGenreStats();
}


