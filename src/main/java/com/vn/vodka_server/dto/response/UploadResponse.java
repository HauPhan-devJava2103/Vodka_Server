package com.vn.vodka_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    private String publicId;
    private String secureUrl;
    private String format;
    private Integer width;
    private Integer height;
    private Long bytes;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double duration; // video, audio

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resourceType; // video, audio, image
}
