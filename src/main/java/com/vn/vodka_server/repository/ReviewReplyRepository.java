package com.vn.vodka_server.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.ReviewReply;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {

    // Stats: Tổng số Replies trong một khoảng thời gian (trends)
    @Query("SELECT COUNT(rp) FROM ReviewReply rp WHERE rp.createdAt >= :from AND rp.createdAt < :to")
    long countByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
