package com.vn.vodka_server.dto.request;

import com.google.auto.value.AutoValue.Builder;

import lombok.Data;

@Data
@Builder
public class UpdateHistoryRequest {
    private Long movieId;
}
