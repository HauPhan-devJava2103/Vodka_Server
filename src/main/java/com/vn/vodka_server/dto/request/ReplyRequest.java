package com.vn.vodka_server.dto.request;

import com.google.auto.value.AutoValue.Builder;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Builder
public class ReplyRequest {
    @NotBlank(message = "Nội dung không được để trống")
    private String content;
}
