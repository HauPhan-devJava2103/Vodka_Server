package com.vn.vodka_server.dto.request;

import com.vn.vodka_server.validator.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "OTP không được để trống")
    private String otp;

    @NotBlank(message = "Mật khẩu không được để trống")
    @StrongPassword
    private String password;
}