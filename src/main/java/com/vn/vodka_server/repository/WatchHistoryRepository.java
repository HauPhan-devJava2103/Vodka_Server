package com.vn.vodka_server.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Lấy số lượt xem theo từng ngày trong N ngày gần nhất
    @Query("SELECT FUNCTION('DATE', wh.watchedAt), COUNT(wh) " +
            "FROM WatchHistory wh " +
            "WHERE wh.watchedAt >= :from " +
            "GROUP BY FUNCTION('DATE', wh.watchedAt) " +
            "ORDER BY FUNCTION('DATE', wh.watchedAt) ASC")
    List<Object[]> countDailyViewsSince(@Param("from") LocalDateTime from);

    // Đếm tổng lượt xem trong khoảng thời gian
    @Query("SELECT COUNT(wh) FROM WatchHistory wh WHERE wh.watchedAt >= :from AND wh.watchedAt <= :to")
    long countByWatchedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Đếm số phim user đã xem
    long countByUser(User user);
}
