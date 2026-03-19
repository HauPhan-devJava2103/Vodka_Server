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
@Entity(name = "Tag")
@Table(name = "tag")
public class Tag extends AbstractEntity {
    @Column(name = "name")
    private String name;

    // Field slug để sử dụng trên URL thay cho ID (vd: phim-le, chieu-rap)
    @Column(name = "slug", unique = true)
    private String slug;

    // Relation Ship
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore // Chặn lỗi vòng lặp JSON
    private Set<Movie> movies = new HashSet<>();
}
