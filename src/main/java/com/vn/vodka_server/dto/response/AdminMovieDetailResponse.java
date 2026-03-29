package com.vn.vodka_server.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminMovieDetailResponse {
    private Long id;
    private String title;
    private String description;
    private Integer releaseYear;
    private String posterUrl;
    private String bannerUrl;
    private String movieType;
    private List<GenreResponse> genres;
    private List<TagResponse> tags;
    private List<AdminSeasonResponse> seasons;
}
