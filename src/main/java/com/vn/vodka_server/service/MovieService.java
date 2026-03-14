package com.vn.vodka_server.service;

import java.util.List;

import com.vn.vodka_server.dto.response.FeaturedMovieResponse;

public interface MovieService {
    // Lấy tất cả phim với genres và tags được load cùng
    List<FeaturedMovieResponse> getFeaturedMovies();
}
