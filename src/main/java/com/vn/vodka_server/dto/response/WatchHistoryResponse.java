package com.vn.vodka_server.dto.response;

import java.util.List;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WatchHistoryResponse {
    private Long id;
    private String title;
    private String posterUrl;
    private String bannerUrl;
    private int releaseYear;
    private List<GenreResponse> genre;
    private double rating;
    private List<TagResponse> tags;
    private String description;
    private String watchedAt;
}
