package com.vn.vodka_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.vodka_server.model.Tag;

@Repository
public interface  TagRepository extends JpaRepository<Tag, Long> {
    java.util.List<Tag> findAllByVisibleTrueOrderByNameAsc();
}
