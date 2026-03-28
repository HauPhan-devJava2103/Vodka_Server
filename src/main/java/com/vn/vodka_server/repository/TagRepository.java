package com.vn.vodka_server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.dto.projection.TagAdminProjection;
import com.vn.vodka_server.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByOrderByNameAsc();

    // Admin: Lấy danh sách tags có phân trang, tìm kiếm, sắp xếp
    // left join để giữ lại tag chưa có phim nào (movieCount = 0)
    // coalesce để tránh sum trả về null khi tag ko có phim
    @Query("""
                SELECT t.id AS id, t.name AS name, t.slug AS slug,
                       COUNT(DISTINCT m.id) AS movieCount,
                       COALESCE(SUM(m.viewCount), 0) AS viewCount,
                       t.createdAt AS createdAt, t.updatedAt AS updatedAt
                FROM Tag t
                LEFT JOIN t.movies m
                WHERE (:search IS NULL
                    OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')))
                GROUP BY t.id, t.name, t.slug, t.createdAt, t.updatedAt
            """)
    Page<TagAdminProjection> findAdminTags(@Param("search") String search, Pageable pageable);

    // Stats: Tag phổ biến nhất (INNER JOIN — chỉ lấy tag đã có phim)
    @Query("""
                SELECT t.name AS name, COUNT(m.id) AS movieCount
                FROM Tag t
                JOIN t.movies m
                GROUP BY t.id, t.name
                ORDER BY COUNT(m.id) DESC
            """)
    List<TagAdminProjection> findMostPopularTag(Pageable pageable);

    // Stats: Đếm phim chưa gắn tag nào
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.tags IS EMPTY")
    long countMoviesWithNoTag();

    // Stats: Tag mới tạo gần đây nhất
    Tag findTopByOrderByCreatedAtDesc();
}
