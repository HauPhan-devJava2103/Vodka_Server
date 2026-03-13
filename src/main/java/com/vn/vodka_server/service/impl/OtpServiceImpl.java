package com.vn.vodka_server.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.vn.vodka_server.service.OtpService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, ResetTokenData> resetTokenStorage = new ConcurrentHashMap<>();

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 1;
    private static final int RESET_TOKEN_EXPIRY_MINUTES = 10;

    private record OtpData(String otp, LocalDateTime createdAt) {
    }

    private record ResetTokenData(String email, LocalDateTime createdAt) {
    }

    @Override
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10)); // 0-9
        }
        return otp.toString();
    }

    @Override
    public void storeOtp(String email, String otp) {
        otpStorage.put(email, new OtpData(otp, LocalDateTime.now()));
        log.info("OTP stored for email: {}", email);
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        OtpData otpData = otpStorage.get(email);

        if (otpData == null) {
            log.warn("No OTP found for email: {}", email);
            return false;
        }

        // Kiểm tra hết hạn
        if (otpData.createdAt().plusMinutes(OTP_EXPIRY_MINUTES)
                .isBefore(LocalDateTime.now())) {
            otpStorage.remove(email);
            log.warn("OTP expired for email: {}", email);
            return false;
        }
        if (!otpData.otp().equals(otp)) {
            log.warn("Invalid OTP for email: {}", email);
            return false;
        }

        // Xóa OTP nếu thành công
        otpStorage.remove(email);
        log.info("OTP validated for email: {}", email);
        return true;
    }

    @Override
    public void storeResetToken(String email, String resetToken) {
        resetTokenStorage.put(resetToken, new ResetTokenData(email, LocalDateTime.now()));
        log.info("Reset token stored for email: {}", email);
    }

    @Override
    public String validateResetToken(String resetToken) {
        ResetTokenData data = resetTokenStorage.get(resetToken);

        if (data == null) {
            log.warn("Reset token not found");
            return null;
        }

        // Kiểm tra hết hạn (10 phút)
        if (data.createdAt().plusMinutes(RESET_TOKEN_EXPIRY_MINUTES)
                .isBefore(LocalDateTime.now())) {
            resetTokenStorage.remove(resetToken);
            log.warn("Reset token expired for email: {}", data.email());
            return null;
        }

        // Xóa token sau khi dùng
        resetTokenStorage.remove(resetToken);
        return data.email();
    }

}
