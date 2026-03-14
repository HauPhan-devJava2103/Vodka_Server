package com.vn.vodka_server.service;

import java.util.List;

import com.vn.vodka_server.dto.response.GenreResponse;

public interface GenreService {
    // Lấy danh sách tất cả các thể loại và chuyển đổi sang DTO
    List<GenreResponse> getAllGenres();
}
