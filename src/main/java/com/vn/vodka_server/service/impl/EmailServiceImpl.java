package com.vn.vodka_server.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Mã xác thực OTP - Vodka Server");
            helper.setText(
                    "<div style='font-family:Arial,sans-serif; padding:20px;'>"
                            + "<h2>Xác thực tài khoản</h2>"
                            + "<p>Mã OTP của bạn là:</p>"
                            + "<h1 style='color:#4CAF50; letter-spacing:5px;'>" + otp + "</h1>"
                            + "<p>Mã có hiệu lực trong <strong>1 phút</strong>.</p>"
                            + "<p>Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>"
                            + "</div>",
                    true);

            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}", toEmail, e);
            throw new RuntimeException("Gửi email thất bại");
        }
    }
}
