package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminEpisodeResponse {
    private Long id;
    private Integer episodeNumber;
    private String title;
    private String description;
    private String videoUrl;
    private Double duration;
}
