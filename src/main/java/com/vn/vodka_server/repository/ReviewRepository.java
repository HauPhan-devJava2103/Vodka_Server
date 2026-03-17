package com.vn.vodka_server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Lấy 10 Review mới nhất
    List<Review> findTop10ByMovieIdOrderByCreatedAtDesc(Long movieId);

    // Tổng số Review
    Long countByMovieId(Long movieId);

    // Lấy danh sách đánh giá theo movie id
    Page<Review> findByMovieIdOrderByCreatedAtDesc(Long movieId, Pageable pageable);

}
