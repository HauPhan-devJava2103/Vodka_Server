package com.vn.vodka_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
