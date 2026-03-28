package com.vn.vodka_server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.dto.projection.GenreAdminProjection;
import com.vn.vodka_server.model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    // Kiểm tra slug đã tồn tại (dùng khi create)
    boolean existsBySlug(String slug);

    // Kiểm tra slug đã tồn tại ở genre KHÁC chưa (dùng khi update)
    boolean existsBySlugAndIdNot(String slug, Long id);

    // Admin: Lấy danh sách genres có phân trang, tìm kiếm, sắp xếp
    @Query("""
                SELECT g.id AS id, g.name AS name, g.slug AS slug,
                       COUNT(DISTINCT m.id) AS movieCount, COALESCE(SUM(m.viewCount), 0) AS viewCount,
                       g.createdAt AS createdAt, g.updatedAt AS updatedAt
                FROM Genre g
                LEFT JOIN g.movies m
                WHERE (:search IS NULL
                    OR LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%')))
                GROUP BY g.id, g.name, g.slug, g.createdAt, g.updatedAt
            """)
    Page<GenreAdminProjection> findAdminGenres(
            @Param("search") String search,
            Pageable pageable);

    // Admin: Lấy chi tiết 1 genre theo ID (kèm movieCount)
    @Query("""
                SELECT g.id AS id, g.name AS name, g.slug AS slug,
                       COUNT(DISTINCT m.id) AS movieCount,
                       g.createdAt AS createdAt, g.updatedAt AS updatedAt
                FROM Genre g
                LEFT JOIN g.movies m
                WHERE g.id = :id
                GROUP BY g.id, g.name, g.slug, g.createdAt, g.updatedAt
            """)
    Optional<GenreAdminProjection> findAdminGenreById(@Param("id") Long id);

    // Stats: Genre có nhiều phim nhất (JPQL + Pageable để lấy top 1)
    @Query("""
                SELECT g.name AS name, COUNT(m.id) AS movieCount
                FROM Genre g
                JOIN g.movies m
                GROUP BY g.id, g.name
                ORDER BY COUNT(m.id) DESC
            """)
    List<GenreAdminProjection> findMostPopularGenre(Pageable pageable);

    // Stats: Đếm phim chưa gắn thể loại nào
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.genres IS EMPTY")
    long countMoviesWithNoGenre();

    // Stats: Genre mới tạo gần đây nhất
    Genre findTopByOrderByCreatedAtDesc();
}

