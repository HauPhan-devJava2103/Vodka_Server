package com.vn.vodka_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Season;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {

    List<Season> findByMovieIdOrderBySeasonNumberAsc(Long id);

}
