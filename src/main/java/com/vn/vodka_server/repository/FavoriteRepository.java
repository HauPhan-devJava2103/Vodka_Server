package com.vn.vodka_server.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Kiểm tra user đã yêu thích phim này chưa
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    // Lấy bản ghi yêu thích theo user và phim
    Optional<Favorite> findByUserIdAndMovieId(Long userId, Long movieId);

    // Lấy danh sách phim yêu thích của user, phân trang, mới nhất lên đầu
    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
