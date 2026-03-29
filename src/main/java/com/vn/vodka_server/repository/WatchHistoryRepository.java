package com.vn.vodka_server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Movie;
import com.vn.vodka_server.model.User;
import com.vn.vodka_server.model.WatchHistory;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {

    // Lấy lịch sử xem phim của 1 user, sắp xếp theo thời gian xem gần nhất
    List<WatchHistory> findByUserIdOrderByWatchedAtDesc(Long userId, Limit limit);

    // Lấy danh sách phim đã xem của user
    Page<WatchHistory> findByUserOrderByWatchedAtDesc(User user, Pageable pageable);

    // Tìm lịch sử xem phim của user với movie cụ thể
    Optional<WatchHistory> findByUserAndMovie(User user, Movie movie);

    // Xóa tất cả lịch sử xem theo phim (dùng khi xóa phim)
    void deleteByMovie(Movie movie);
}
