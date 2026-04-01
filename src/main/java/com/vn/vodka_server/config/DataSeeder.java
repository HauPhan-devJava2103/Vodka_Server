package com.vn.vodka_server.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.vn.vodka_server.model.User;
import com.vn.vodka_server.repository.UserRepository;
import com.vn.vodka_server.util.EProvider;
import com.vn.vodka_server.util.ERole;
import com.vn.vodka_server.util.EStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminAccount();
    }

    private void seedAdminAccount() {
        String adminEmail = "admin@gmail.com";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Tài khoản email admin đã tồn tại hệ thống.");
            return;
        }

        User admin = User.builder()
                .fullName("Administrator")
                .email(adminEmail)
                .password(passwordEncoder.encode("admin123"))
                .role(ERole.ADMIN)
                .status(EStatus.ACTIVE)
                .provider(EProvider.LOCAL)
                .build();

        userRepository.save(admin);
        log.info("Tài khoản email admin đã được tạo thành công (email: {})", adminEmail);
    }
}
