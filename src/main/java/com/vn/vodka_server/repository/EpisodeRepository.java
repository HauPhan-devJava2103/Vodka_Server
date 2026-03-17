package com.vn.vodka_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Episode;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {

}
