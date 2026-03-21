package com.vn.vodka_server.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Genre")
@Table(name = "genre")
public class Genre extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    // Field slug để sử dụng trên URL thay cho ID (vd: phim-bo, hanh-dong)
    @Column(name = "slug", unique = true)
    private String slug;

    // Relation Ship
    @ManyToMany(mappedBy = "genres")
    @JsonIgnore // Chặn lỗi vòng lặp vô tận
    @Builder.Default
    private Set<Movie> movies = new HashSet<>();
}
