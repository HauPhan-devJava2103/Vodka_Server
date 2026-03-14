package com.vn.vodka_server.service.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.request.LoginRequest;
import com.vn.vodka_server.dto.request.RegisterRequest;
import com.vn.vodka_server.dto.request.ResetPasswordRequest;
import com.vn.vodka_server.dto.request.SendOtpRequest;
import com.vn.vodka_server.dto.request.VerifyOtpRequest;
import com.vn.vodka_server.dto.response.LoginResponse;
import com.vn.vodka_server.dto.response.UserInfo;
import com.vn.vodka_server.model.User;
import com.vn.vodka_server.repository.UserRepository;
import com.vn.vodka_server.service.AuthService;
import com.vn.vodka_server.service.EmailService;
import com.vn.vodka_server.service.OtpService;
import com.vn.vodka_server.util.ERole;
import com.vn.vodka_server.util.EStatus;
import com.vn.vodka_server.util.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public void sendOtp(SendOtpRequest request) {
        String email = request.getEmail();
        // 1. Kiểm tra email tồn tại
        requireEmailNotExists(email);
        // 2. Sinh OTP & lưu vào bộ nhớ và gửi
        generateAndSendOtp(email);
    }

    @Override
    public LoginResponse register(RegisterRequest request) {

        String email = request.getEmail();

        requireEmailNotExists(email);
        // 2. Xác thực OTP
        verifyOtp(request.getEmail(), request.getOtp());

        // 3. Tạo name từ email (lấy phần trước @)
        String name = email.substring(0, email.indexOf("@"));

        // 4. Ảnh đại diện mặc định
        String defaultAvatar = "https://res.cloudinary.com/dcrxiky8s/image/upload/v1773409663/avatardefault.png";

        // 5. Tạo User mới
        User user = User.builder()
                .email(email)
                .fullName(name)
                .avatarUrl(defaultAvatar)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(ERole.USER)
                .status(EStatus.ACTIVE)
                .build();

        userRepository.save(user);

        // 6. Tạo JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
        String token = jwtUtils.generateToken(userDetails);

        // 7. Trả về response giống login
        UserInfo userInfo = new UserInfo(user.getId(), user.getEmail(), user.getFullName(), user.getAvatarUrl());
        return new LoginResponse(token, userInfo);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        // 3. Tạo JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
        String token = jwtUtils.generateToken(userDetails);
        UserInfo userInfo = new UserInfo(user.getId(), user.getEmail(), user.getFullName(), user.getAvatarUrl());
        return new LoginResponse(token, userInfo);

    }

    @Override
    public void forgotPasswordSendOtp(SendOtpRequest request) {
        String email = request.getEmail();

        // 1. Kiểm tra email có tồn tại không
        requireEmailExist(email);
        // 2. Sinh OTP
        generateAndSendOtp(email);
    }

    @Override
    public Map<String, String> forgotPasswordVerifyOtp(VerifyOtpRequest request) {
        String email = request.getEmail();

        // 1. Kiểm tra email có tồn tại không
        requireEmailExist(email);
        // 2. Xác thực OTP
        verifyOtp(email, request.getOtp());

        // 3. Tạo reset token
        String resetToken = UUID.randomUUID().toString();
        otpService.storeResetToken(email, resetToken);

        return Map.of("resetToken", resetToken);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Xác thực reset token và lấy email
        String email = otpService.validateResetToken(request.getResetToken());
        if (email == null) {
            throw new RuntimeException("Reset token không hợp lệ hoặc đã hết hạn");
        }

        // 2. Kiểm tra email khớp
        if (!email.equals(request.getEmail())) {
            throw new RuntimeException("Email không khớp với reset token");
        }

        // 3. Tìm user và cập nhật mật khẩu
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    // HELPER
    private void requireEmailExist(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email không tồn tại trong hệ thống");
        }
    }

    private void requireEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã được sử dụng");
        }
    }

    private void generateAndSendOtp(String email) {
        String otp = otpService.generateOtp();
        otpService.storeOtp(email, otp);
        emailService.sendOtpEmail(email, otp);
    }

    private void verifyOtp(String email, String otp) {
        if (!otpService.validateOtp(email, otp)) {
            throw new RuntimeException("OTP không hợp lệ hoặc đã hết hạn");
        }
    }
}
