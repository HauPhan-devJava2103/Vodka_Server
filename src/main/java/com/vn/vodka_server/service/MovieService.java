package com.vn.vodka_server.service;

import java.util.List;

import com.vn.vodka_server.dto.response.FeaturedMovieResponse;
import com.vn.vodka_server.dto.response.MovieDetailResponse;
import com.vn.vodka_server.dto.response.TrendingMovieResponse;

public interface MovieService {
    // Lấy tất cả phim với genres và tags được load cùng
    List<FeaturedMovieResponse> getFeaturedMovies();

    // Lấy phim thịnh hành nhất (phim có lượt xem cao nhất)
    List<TrendingMovieResponse> getTrendingMovies(int limit);

    // Lấy chi tiết phim
    MovieDetailResponse getMovieById(Long id);
}
