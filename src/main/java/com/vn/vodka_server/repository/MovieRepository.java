package com.vn.vodka_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vn.vodka_server.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Lấy tất cả phim với genres và tags được load cùng
    @EntityGraph(attributePaths = { "genres", "tags" })
    List<Movie> findAll();

    // Lấy 10 phim nổi bật nhất (10 phim có rating cao nhất)
    @EntityGraph(attributePaths = { "genres", "tags" })
    List<Movie> findTop2ByOrderByRatingDesc();

    // Lấy phim thịnh hành nhất (phim có lượt xem cao nhất)
    @Query("SELECT m FROM Movie m ORDER BY m.viewCount DESC")
    List<Movie> findTrendingMovies(Limit limit);

    // Tìm phim hot (nhiều view nhất) có phân trang
    Page<Movie> findAllByOrderByViewCountDesc(Pageable pageable);

}
