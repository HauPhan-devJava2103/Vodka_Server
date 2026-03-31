package com.vn.vodka_server.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.response.AdminUserResponse;
import com.vn.vodka_server.dto.response.UserStatsResponse;
import com.vn.vodka_server.exception.BadRequestException;
import com.vn.vodka_server.exception.ResourceNotFoundException;
import com.vn.vodka_server.model.User;
import com.vn.vodka_server.repository.ReviewRepository;
import com.vn.vodka_server.repository.UserRepository;
import com.vn.vodka_server.repository.WatchHistoryRepository;
import com.vn.vodka_server.service.AdminUserService;
import com.vn.vodka_server.service.EmailService;
import com.vn.vodka_server.util.EGender;
import com.vn.vodka_server.util.EProvider;
import com.vn.vodka_server.util.ERole;
import com.vn.vodka_server.util.EStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final SimpleDateFormat DATE_OF_BIRTH_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat CREATED_AT_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // Lấy danh sách user bao gồm lấy danh sách, lọc, phân trang, tìm kiếm, sắp xếp
    @Override
    public Page<AdminUserResponse> getUsers(int page, int pageSize, String search, String statusStr,
            String providerStr, String genderStr, String sort) {

        // Validate phân trang
        if (page < 1)
            throw new BadRequestException("Số trang phải bắt đầu từ 1");
        if (pageSize <= 0 || pageSize > 100)
            throw new BadRequestException("Số lượng mỗi trang phải nằm trong khoảng từ 1 đến 100");

        // Parse status: "active" / "inactive" (hoặc thư thái unactive) -> EStatus
        EStatus parsedStatus = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                if (statusStr.equalsIgnoreCase("unactive"))
                    statusStr = "INACTIVE";

                parsedStatus = EStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Giá trị trạng thái không hợp lệ. Hỗ trợ: active, inactive, unactive");
            }
        }

        // Parse provider: "local" / "google" -> EProvider
        EProvider parsedProvider = null;
        if (providerStr != null && !providerStr.isBlank())
            try {
                parsedProvider = EProvider.valueOf(providerStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Giá trị provider không hợp lệ. Hỗ trợ: local, google");
            }

        // Parse gender: "male" / "female" / "other" -> EGender
        EGender parsedGender = null;
        if (genderStr != null && !genderStr.isBlank())
            try {
                parsedGender = EGender.valueOf(genderStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Giá trị giới tính không hợp lệ. Hỗ trợ: male, female, other");
            }

        String normalizedSearch = (search != null && !search.isBlank()) ? search.trim() : null;

        // Tạo Sort
        Sort sortDef;
        if ("oldest".equalsIgnoreCase(sort) || "cũ nhất".equalsIgnoreCase(sort))
            sortDef = Sort.by("createdAt").ascending();
        else
            sortDef = Sort.by("createdAt").descending(); // Default: newest

        Pageable pageable = PageRequest.of(page - 1, pageSize, sortDef);

        // Truy vấn DB
        Page<User> userPage = userRepository.findAllWithFilters(normalizedSearch, parsedStatus, parsedProvider,
                parsedGender, ERole.USER, pageable);

        // Map User -> AdminUserResponse
        return userPage.map(this::mapToAdminUserResponse);
    }

    // Lấy thống kê user
    @Override
    public UserStatsResponse getUserStats() {
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        long totalUsers = userRepository.countByRole(ERole.USER);
        long activeUsers = userRepository.countByStatusAndRole(EStatus.ACTIVE, ERole.USER);
        long inactiveUsers = userRepository.countByStatusAndRole(EStatus.INACTIVE, ERole.USER);
        long userNewToday = userRepository.countByRoleAndCreatedAtBetween(ERole.USER, startOfToday, now);

        return UserStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .userNewToday(userNewToday)
                .build();
    }

    // Lock / Unlock user (toggle)
    @Override
    public void toggleLock(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id));

        if (user.getRole() == ERole.ADMIN)
            throw new BadRequestException("Không thể thay đổi trạng thái của tài khoản Admin");

        if (user.getStatus() == EStatus.ACTIVE)
            user.setStatus(EStatus.INACTIVE);
        else
            user.setStatus(EStatus.ACTIVE);

        userRepository.save(user);
    }

    // Reset mật khẩu và gửi email
    @Override
    public void resetPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id));

        if (user.getRole() == ERole.ADMIN)
            throw new BadRequestException("Không thể reset mật khẩu tài khoản Admin");

        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new BadRequestException("Người dùng chưa có email, không thể gửi mật khẩu mới");

        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        emailService.sendResetPasswordEmail(user.getEmail(), newPassword);
    }

    // HELPER METHOD

    private AdminUserResponse mapToAdminUserResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName() != null ? user.getFullName() : "N/A")
                .dateOfBirth(formatDateOfBirth(user.getDateOfBirth()))
                .gender(mapGenderToVietnamese(user.getGender()))
                .phone(user.getPhone() != null ? user.getPhone() : "N/A")
                .email(user.getEmail() != null ? user.getEmail() : "N/A")
                .avatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "N/A")
                .provider(user.getProvider() != null ? user.getProvider().name() : EProvider.LOCAL.name())
                .status(user.getStatus() != null ? user.getStatus().name() : "N/A")
                .movieWatched(watchHistoryRepository.countByUser(user))
                .reviewCount(reviewRepository.countByUser(user))
                .createdAt(formatCreatedAt(user.getCreatedAt()))
                .build();
    }

    // Format Date -> "dd/MM/yyyy", trả "N/A" nếu date là null
    private String formatDateOfBirth(Date date) {
        if (date == null)
            return "N/A";
        return DATE_OF_BIRTH_FORMAT.format(date);
    }

    // Format Date -> "yyyy-MM-dd", trả "N/A" nếu date là null
    private String formatCreatedAt(Date date) {
        if (date == null)
            return "N/A";
        return CREATED_AT_FORMAT.format(date);
    }

    // Map EGender -> tiếng Việt hiển thị, trả "N/A" nếu null
    private String mapGenderToVietnamese(EGender gender) {
        if (gender == null)
            return "N/A";
        return switch (gender) {
            case MALE -> "Nam";
            case FEMALE -> "Nữ";
            case OTHER -> "Khác";
        };
    }
}
