package com.vn.vodka_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.ReviewReply;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {

    // Lấy tất cả phản hồi của một review, sắp xếp từ cũ đến mới
    List<ReviewReply> findByReviewIdOrderByCreatedAtAsc(Long reviewId);
}
