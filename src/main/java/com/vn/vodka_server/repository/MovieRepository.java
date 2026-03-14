package com.vn.vodka_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Lấy tất cả phim với genres và tags được load cùng
    @EntityGraph(attributePaths = { "genres", "tags" })
    List<Movie> findAll();

    // Lấy 10 phim nổi bật nhất (10 phim có rating cao nhất)
    @EntityGraph(attributePaths = { "genres", "tags" })
    List<Movie> findTop2ByOrderByRatingDesc();
}
