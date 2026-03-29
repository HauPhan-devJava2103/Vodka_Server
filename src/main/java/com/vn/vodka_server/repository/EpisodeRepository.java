package com.vn.vodka_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Episode;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    // Lấy tất cả episodes của 1 season để merge khi update phim
    List<Episode> findBySeasonId(Long seasonId);
}
