package com.vn.vodka_server.service;

public interface OtpService {

    String generateOtp();

    void storeOtp(String email, String otp);

    boolean validateOtp(String email, String otp);

    // Reset token cho forgot password
    void storeResetToken(String email, String resetToken);

    String validateResetToken(String resetToken);
}
