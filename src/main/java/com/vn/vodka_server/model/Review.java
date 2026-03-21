package com.vn.vodka_server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Review")
@Table(name = "review")
public class Review extends AbstractEntity {

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // Relationship

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnore
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "reply_to_id")
    private String replyToId;

    // Danh sách reply của review này
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<ReviewReply> replies = new ArrayList<>();

}
