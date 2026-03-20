package com.vn.vodka_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateReplyRequest {

    // Nội dung phản hồi, không được để trống
    @NotBlank(message = "Nội dung phản hồi không được để trống")
    private String content;
}
