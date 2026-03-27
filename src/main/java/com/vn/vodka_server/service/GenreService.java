package com.vn.vodka_server.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.vn.vodka_server.dto.response.GenreResponse;

public interface GenreService {
    // Lấy danh sách tất cả các thể loại và chuyển đổi sang DTO
    List<GenreResponse> getAllGenres();

    // Admin: Lấy danh sách genres có phân trang, tìm kiếm, sắp xếp
    Page<GenreResponse> getAdminGenres(int page, int pageSize, String search, String sort);
}
