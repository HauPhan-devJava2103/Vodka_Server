package com.vn.vodka_server.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Bảng lưu lịch sử xem phim của từng user
// Mỗi bản ghi = 1 lần user xem 1 phim
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "WatchHistory")
@Table(name = "watch_history")
public class WatchHistory extends AbstractEntity {

    // User xem phim
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Phim được xem
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    // Thời điểm xem phim
    @Column(name = "watched_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date watchedAt;
}
