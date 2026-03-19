package com.vn.vodka_server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Entity(name = "Episode")
@Table(name = "episode")
public class Episode extends AbstractEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "duration")
    private Double duration;

    @Column(name = "episode_number")
    private Integer episodeNumber;

    // Add API11: Thêm đường dẫn video và mô tả cho tập phim
    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Relationship

    @ManyToOne
    @JoinColumn(name = "season_id", nullable = false)
    @JsonIgnore
    private Season season;
}
