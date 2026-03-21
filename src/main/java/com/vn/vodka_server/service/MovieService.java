package com.vn.vodka_server.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.FeaturedMovieResponse;
import com.vn.vodka_server.dto.response.FilterMovieResponse;
import com.vn.vodka_server.dto.response.MovieDetailResponse;
import com.vn.vodka_server.dto.response.TrendingMovieResponse;
import com.vn.vodka_server.dto.response.WatchMovieResponse;

public interface MovieService {
    // Lấy tất cả phim với genres và tags được load cùng
    List<FeaturedMovieResponse> getFeaturedMovies();

    // Lấy phim thịnh hành nhất (phim có lượt xem cao nhất)
    List<TrendingMovieResponse> getTrendingMovies(int limit);

    // Tìm phim hot (nhiều view nhất) có phân trang
    // Trả về Page<DTO> thay vì ApiResponse - Service chỉ trả dữ liệu thô
    Page<TrendingMovieResponse> getNewReleases(int page, int limit);

    // Lấy chi tiết phim
    MovieDetailResponse getMovieById(Long id);

    // Lấy danh sách đánh giá
    ApiResponse getReviews(Long id, int page, int limit);

    // API5: Lấy lịch sử xem phim của user
    List<TrendingMovieResponse> getWatchHistory(String email, int limit);

    // API6: Lấy phim mới cập nhật gần đây
    List<TrendingMovieResponse> getRecentlyUpdated(int limit);

    // API7: Lấy phim có đánh giá cao nhất
    List<TrendingMovieResponse> getHighlyRatedMovies(int limit);

    // API8: Lọc phim theo thể loại
    List<TrendingMovieResponse> getMoviesByGenre(Long genreId, int limit);

    // Add API11: Lấy dữ liệu trang xem phim theo episodeId
    WatchMovieResponse getWatchData(Long episodeId);

    // API12: Lọc phim đa điều kiện
    Page<FilterMovieResponse> filterMovies(String keyword, String tagSlug,
            List<String> genreSlugs, int page, int pageSize);
}
