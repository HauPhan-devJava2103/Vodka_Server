package com.vn.vodka_server.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieDetailResponse {
    private FeaturedMovieResponse movie;

    List<SeasonResponse> episodes;

    List<ReviewResponse> reviews;

    // Phim gợi ý thể loại
    List<FeaturedMovieResponse> relatedMovies;

    private MovieStatsResponse stats;
}
