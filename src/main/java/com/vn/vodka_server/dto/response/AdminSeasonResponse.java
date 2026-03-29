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
public class AdminSeasonResponse {
    private Long id;
    private Integer seasonNumber;
    private String title;
    private List<AdminEpisodeResponse> episodes;
}
