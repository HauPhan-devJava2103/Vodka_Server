package com.vn.vodka_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateGenreRequest {

    @NotBlank(message = "Tên thể loại không được để trống")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    private String slug;
}
