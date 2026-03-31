package com.vn.vodka_server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.vn.vodka_server.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private static final String OTP_EXPIRY_DISPLAY = "10 phút";

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("email", toEmail);
            context.setVariable("expiryDisplay", OTP_EXPIRY_DISPLAY);

            // Split OTP into individual digits
            List<String> otpDigits = new ArrayList<>();
            for (char c : otp.toCharArray()) {
                otpDigits.add(String.valueOf(c));
            }
            context.setVariable("otpDigits", otpDigits);

            // Render HTML from template
            String htmlContent = templateEngine.process("otp-email", context);

            // Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Mã xác thực OTP - VODKA");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}", toEmail, e);
            throw new RuntimeException("Gửi email thất bại");
        }
    }

    // Gửi email reset mk
    @Override
    public void sendResetPasswordEmail(String toEmail, String newPassword) {
        try {
            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("email", toEmail);
            context.setVariable("newPassword", newPassword);

            // Render HTML from template
            String htmlContent = templateEngine.process("reset-password-email", context);

            // Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Mật khẩu mới - VODKA");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Reset password email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send reset password email to {}", toEmail, e);
            throw new RuntimeException("Gửi email thất bại");
        }
    }
}
