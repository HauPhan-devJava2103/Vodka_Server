package com.vn.vodka_server.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp);
}
