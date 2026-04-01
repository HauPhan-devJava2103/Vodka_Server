package com.vn.vodka_server.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vn.vodka_server.model.Favorite;
import com.vn.vodka_server.model.Movie;
import com.vn.vodka_server.model.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // Tìm phim yêu thích của user
    Page<Favorite> findByUserOrderByCreatedAtDesc(User user, org.springframework.data.domain.Pageable pageable);

    Optional<Favorite> findByUserAndMovie(User user, Movie movie);

    // Đếm tổng số user yêu thích phim
    long countByMovie(Movie movie);
}
