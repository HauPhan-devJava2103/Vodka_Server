package com.vn.vodka_server.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
@Entity(name = "Movie")
@Table(name = "movie")
public class Movie extends AbstractEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "post_url")
    private String postUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "description")
    private String description;

    // Thêm mới (Văn)
    @Column(name = "view_count")
    private Long viewCount;

    // Relation Ship

    // Movie - Genre
    @ManyToMany
    @JoinTable(name = "movie_genre", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new HashSet<>();

    // Movie - Tag
    @ManyToMany
    @JoinTable(name = "movie_tag", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    // Movie - Media (danh sách media thuộc phim này)
    @OneToMany(mappedBy = "movie")
    private List<Media> medias = new ArrayList<>();

}
