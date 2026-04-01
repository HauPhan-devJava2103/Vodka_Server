package com.vn.vodka_server.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Review;
import com.vn.vodka_server.model.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

        // Lấy 10 Review mới nhất
        List<Review> findTop10ByMovieIdOrderByCreatedAtDesc(Long movieId);

        // Tổng số Review
        Long countByMovieId(Long movieId);

        // Tính trung bình rating của 1 phim cụ thể
        @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.movie.id = :movieId")
        double averageRatingByMovieId(@Param("movieId") Long movieId);

        // Lấy danh sách đánh giá theo movie id
        Page<Review> findByMovieIdOrderByCreatedAtDesc(Long movieId, Pageable pageable);

        // Lấy danh sách đánh giá của tôi
        Page<Review> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

        @EntityGraph(attributePaths = { "user", "movie" })
        @Query("""
                            SELECT r FROM Review r
                            WHERE (:search IS NULL
                                OR LOWER(r.user.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(r.content) LIKE LOWER(CONCAT('%', :search, '%'))
                                OR LOWER(r.movie.title) LIKE LOWER(CONCAT('%', :search, '%')))
                                AND (:rating IS NULL OR r.rating = :rating)
                        """)
        Page<Review> findAdminReviews(
                        @Param("search") String search,
                        @Param("rating") Double rating,
                        Pageable pageable);

        // Stats: Rating trung bình tất cả review
        @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r")
        double averageRating();

        // Stats: Số phim có ít nhất 1 Review
        @Query("SELECT COUNT(DISTINCT r.movie.id) FROM Review r")
        long countDistinctMovie();

        // Stats: Đếm tổng Review trong khoảng thời gian (trends)
        @Query("SELECT COUNT(r) FROM Review r WHERE r.createdAt >= :from AND r.createdAt < :to")
        long countByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

        // Lấy danh sách đánh giá mới nhất
        @EntityGraph(attributePaths = { "user", "movie" })
        List<Review> findByOrderByUpdatedAtDesc(Limit limit);

        // Đếm số review của một user
        long countByUser(User user);
}
