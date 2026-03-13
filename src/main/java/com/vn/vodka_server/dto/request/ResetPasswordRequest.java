package com.vn.vodka_server.dto.request;

import java.io.Serializable;

import com.vn.vodka_server.validator.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest implements Serializable {
    @Email
    @NotBlank
    private String email;

    @NotBlank(message = "Password không được để trống")
    @StrongPassword
    private String password;

    @NotBlank
    private String resetToken;
}
