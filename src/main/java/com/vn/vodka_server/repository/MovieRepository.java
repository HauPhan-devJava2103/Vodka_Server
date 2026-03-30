package com.vn.vodka_server.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
        // Lấy tất cả phim với genres và tags được load cùng
        @EntityGraph(attributePaths = { "genres", "tags" })
        List<Movie> findAll();

        // Lấy 8 phim nổi bật nhất (8 phim có rating cao nhất)
        @EntityGraph(attributePaths = { "genres", "tags" })
        List<Movie> findTop8ByOrderByRatingDesc();

        // Lấy phim thịnh hành nhất (phim có lượt xem cao nhất)
        @Query("SELECT m FROM Movie m ORDER BY m.viewCount DESC")
        List<Movie> findTrendingMovies(Limit limit);

        // Tìm phim hot (nhiều view nhất) có phân trang
        Page<Movie> findAllByOrderByViewCountDesc(Pageable pageable);

        // Lấy danh sách gợi ý phim liên quan về thể loại
        @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.id IN :genreIds AND m.id <> :movieId")
        List<Movie> findRelatedMovies(@Param("genreIds") Set<Long> genreIds, @Param("movieId") Long movieId,
                        Limit limit);

        // API6: Lấy phim mới cập nhật nhất (sắp xếp theo updatedAt giảm dần)
        List<Movie> findAllByOrderByUpdatedAtDesc(Limit limit);

        // API7: Lấy phim đánh giá cao nhất (sắp xếp theo rating giảm dần)
        @Query("SELECT m FROM Movie m ORDER BY m.rating DESC")
        List<Movie> findHighlyRatedMovies(Limit limit);

        // API8: Lọc phim theo thể loại slug (JOIN qua bảng trung gian movie_genre)
        @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.slug = :genreSlug")
        List<Movie> findByGenreSlug(@Param("genreSlug") String genreSlug, Limit limit);

        // API12: Lọc phim đa điều kiện bằng Slug
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN m.tags t " +
                        "LEFT JOIN m.genres g " +
                        "WHERE (:keyword IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:tagSlug IS NULL OR t.slug = :tagSlug) " +
                        "AND (:genreSlugs IS NULL OR g.slug IN :genreSlugs)")
        Page<Movie> findFilteredMovies(
                        @Param("keyword") String keyword,
                        @Param("tagSlug") String tagSlug,
                        @Param("genreSlugs") List<String> genreSlugs,
                        Pageable pageable);

        // Admin: Lấy danh sách phim có lọc theo genre, year, rating
        @Query("SELECT DISTINCT m FROM Movie m " +
                        "LEFT JOIN m.genres g " +
                        "WHERE (:genreSlug IS NULL OR g.slug = :genreSlug) " +
                        "AND (:year IS NULL OR m.releaseYear = :year) " +
                        "AND (:minRating IS NULL OR m.rating >= :minRating)")
        Page<Movie> findAdminMovies(
                        @Param("genreSlug") String genreSlug,
                        @Param("year") Integer year,
                        @Param("minRating") Double minRating,
                        Pageable pageable);

        // Đếm phim tạo trong khoảng thời gian
        @Query("SELECT COUNT(m) FROM Movie m WHERE m.createdAt >= :from AND m.createdAt <= :to")
        long countByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

        // Rating trung bình của phim trong khoảng thời gian
        @Query("SELECT COALESCE(AVG(m.rating), 0) FROM Movie m WHERE m.createdAt >= :from AND m.createdAt <= :to")
        double averageRatingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

        // Tổng favorites trong khoảng thời gian
        @Query("SELECT COALESCE(SUM(m.favorites), 0) FROM Movie m WHERE m.createdAt >= :from AND m.createdAt <= :to")
        long sumFavoritesBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

        // Tổng ViewCount trong khoảng thời gian
        @Query("SELECT COALESCE(SUM(m.viewCount), 0) FROM Movie m WHERE m.createdAt >= :from AND m.createdAt <= :to")
        long sumViewCountBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

        // Rating trung bình toàn bộ phim
        @Query("SELECT COALESCE(AVG(m.rating), 0) FROM Movie m")
        double averageRating();

        // Tổng lượt xem toàn bộ
        @Query("SELECT COALESCE(SUM(m.viewCount), 0) FROM Movie m")
        long sumViewCount();

        // Tổng yêu thích toàn bộ
        @Query("SELECT COALESCE(SUM(m.favorites), 0) FROM Movie m")
        long sumFavorites();

        // Lấy N Record Movie gần nhất
        List<Movie> findTopByOrderByUpdatedAtDesc(Limit limit);

}
