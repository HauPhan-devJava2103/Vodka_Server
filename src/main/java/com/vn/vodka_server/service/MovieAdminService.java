package com.vn.vodka_server.service;

import com.vn.vodka_server.dto.request.CreateMovieRequest;

public interface MovieAdminService {

    // Tạo mới phim (SERIES hoặc SINGLE) kèm seasons và episodes
    void createMovie(CreateMovieRequest request);
}
