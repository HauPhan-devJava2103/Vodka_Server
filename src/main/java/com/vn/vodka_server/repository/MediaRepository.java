package com.vn.vodka_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vn.vodka_server.model.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {
    Optional<Media> findByPublicId(String publicId);
}
