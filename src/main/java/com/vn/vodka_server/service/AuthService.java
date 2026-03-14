package com.vn.vodka_server.service;

import java.util.Map;

import com.vn.vodka_server.dto.request.LoginRequest;
import com.vn.vodka_server.dto.request.RegisterRequest;
import com.vn.vodka_server.dto.request.ResetPasswordRequest;
import com.vn.vodka_server.dto.request.SendOtpRequest;
import com.vn.vodka_server.dto.request.VerifyOtpRequest;
import com.vn.vodka_server.dto.response.LoginResponse;

public interface AuthService {
    void sendOtp(SendOtpRequest request);

    // UC2
    LoginResponse register(RegisterRequest request);

    // UC1
    LoginResponse login(LoginRequest request);

    // UC4 - Quên mật khẩu: gửi OTP
    void forgotPasswordSendOtp(SendOtpRequest request);

    // UC5 - Quên mật khẩu: xác thực OTP
    Map<String, String> forgotPasswordVerifyOtp(VerifyOtpRequest request);

    // UC6 - Quên mật khẩu: đặt lại mật khẩu
    void resetPassword(ResetPasswordRequest request);
}
