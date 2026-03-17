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
public class SeasonResponse {
    private String id;
    private String title;
    private String thumbnailUrl;
    private List<EpisodeResponse> episodes;
}