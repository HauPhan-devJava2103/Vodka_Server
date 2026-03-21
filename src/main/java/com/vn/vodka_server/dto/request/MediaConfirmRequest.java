package com.vn.vodka_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MediaConfirmRequest {
    @NotBlank(message = "publicId không được để trống")
    private String publicId;

    @NotBlank(message = "secureUrl không được để trống")
    private String secureUrl;

    @NotBlank(message = "resourceType không được để trống")
    private String resourceType;

    private String format;
    private Integer width;
    private Integer height;
    private Long bytes;
    private Double duration;
    private Boolean isAudio;

    // ID phim
    private Long movieId;
}
