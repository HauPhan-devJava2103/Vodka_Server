package com.vn.vodka_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.request.LoginRequest;
import com.vn.vodka_server.dto.request.RegisterRequest;
import com.vn.vodka_server.dto.request.ResetPasswordRequest;
import com.vn.vodka_server.dto.request.SendOtpRequest;
import com.vn.vodka_server.dto.request.VerifyOtpRequest;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.LoginResponse;
import com.vn.vodka_server.service.impl.AuthServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class Authentication {

    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/register/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        try {
            authServiceImpl.sendOtp(request);
            return ResponseEntity.ok(ApiResponse.success("OTP đã được gửi", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authServiceImpl.register(request);
            return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công", response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authServiceImpl.login(request);
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<ApiResponse> forgotPasswordSendOtp(@Valid @RequestBody SendOtpRequest request) {
        try {
            authServiceImpl.forgotPasswordSendOtp(request);
            return ResponseEntity.ok(ApiResponse.success("OTP đã được gửi", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse> forgotPasswordVerifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        try {
            var result = authServiceImpl.forgotPasswordVerifyOtp(request);
            return ResponseEntity.ok(ApiResponse.success("Xác thực thành công", result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse> forgotPasswordReset(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            authServiceImpl.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

}
