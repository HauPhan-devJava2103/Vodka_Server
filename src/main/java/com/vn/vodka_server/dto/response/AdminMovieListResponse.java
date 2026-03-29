package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminMovieListResponse {
    private Long id;
    private String title;
    private String movieCode;
    private String posterUrl;
    private String movieType;
    private Integer releaseYear;
    private Double rating;
    private Long views;
    private Long favorites;
    private String createdAt;
}
