package com.vn.vodka_server.repository;

import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.WatchHistory;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {

    // Lấy lịch sử xem phim của 1 user, sắp xếp theo thời gian xem gần nhất
    List<WatchHistory> findByUserIdOrderByWatchedAtDesc(Long userId, Limit limit);
}
