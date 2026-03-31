package com.vn.vodka_server.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp);

    // Gửi email reset mật khẩu
    void sendResetPasswordEmail(String toEmail, String newPassword);
}
