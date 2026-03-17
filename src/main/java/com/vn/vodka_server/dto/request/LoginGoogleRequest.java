package com.vn.vodka_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginGoogleRequest {
    @NotBlank(message = "idToken không được để trống")
    private String idToken;
}
