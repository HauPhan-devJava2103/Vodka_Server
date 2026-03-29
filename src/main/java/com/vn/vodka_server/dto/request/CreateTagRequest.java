package com.vn.vodka_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTagRequest {

    @NotBlank(message = "Tên tag không được để trống")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    private String slug;
}
