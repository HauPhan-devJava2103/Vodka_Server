package com.vn.vodka_server.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.vn.vodka_server.dto.request.MediaConfirmRequest;
import com.vn.vodka_server.dto.response.UploadResponse;
import com.vn.vodka_server.model.Media;
import com.vn.vodka_server.model.Movie;
import com.vn.vodka_server.model.User;
import com.vn.vodka_server.repository.MediaRepository;
import com.vn.vodka_server.repository.MovieRepository;
import com.vn.vodka_server.repository.UserRepository;
import com.vn.vodka_server.service.MediaService;
import com.vn.vodka_server.util.EMediaType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {

    private final Cloudinary cloudinary;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Value("${cloudinary.folder:vodka_server}")
    private String folderName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    // 1. Lấy Signature
    @Override
    public Map<String, Object> getUploadSignature() {
        long timestamp = System.currentTimeMillis() / 1000;
        String tags = "tmp";

        Map<String, Object> paramsToSign = new HashMap<>();
        paramsToSign.put("timestamp", timestamp);
        paramsToSign.put("folder", folderName);
        paramsToSign.put("tags", tags);

        // Ký signature bằng api_secret
        String signature = cloudinary.apiSignRequest(paramsToSign, apiSecret);

        Map<String, Object> result = new HashMap<>();
        result.put("signature", signature);
        result.put("timestamp", timestamp);
        result.put("folder", folderName);
        result.put("api_key", apiKey);
        result.put("cloud_name", cloudName);
        result.put("tags", tags);

        return result;
    }

    // 2. Xác nhận Media
    @Override
    @Transactional
    public UploadResponse confirmMedia(MediaConfirmRequest request) {
        String resourceType;
        if ("image".equals(request.getResourceType())) {
            resourceType = "IMAGE";
        } else if ("video".equals(request.getResourceType())) {
            if (Boolean.TRUE.equals(request.getIsAudio())) {
                resourceType = "AUDIO";
            } else {
                resourceType = "VIDEO";
            }
        } else {
            resourceType = "IMAGE";

        }
        // Lấy user hiện tại từ JWT token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user đang đăng nhập"));

        // Movie id
        Movie movie = null;
        if (request.getMovieId() != null) {
            movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy phim với id = " + request.getMovieId()));
        }

        Media media = Media.builder()
                .publicId(request.getPublicId())
                .secureUrl(request.getSecureUrl())
                .resourceType(EMediaType.valueOf(resourceType))
                .format(request.getFormat())
                .width(request.getWidth())
                .height(request.getHeight())
                .bytes(request.getBytes())
                .duration(request.getDuration())
                .movie(movie)
                .uploadedBy(currentUser)
                .build();
        mediaRepository.save(media);

        // Xóa tag "tmp" xác nhận file giữ lại vĩnh viễn
        try {
            cloudinary.uploader().removeTag("tmp",
                    new String[] { request.getPublicId() },
                    ObjectUtils.asMap("resource_type", request.getResourceType()));
        } catch (Exception e) {
            log.warn("Không thể xóa tag 'tmp' cho publicId={}: {}", request.getPublicId(), e.getMessage());

        }

        // Map sang UploadResponse
        UploadResponse.UploadResponseBuilder builder = UploadResponse.builder()
                .publicId(request.getPublicId())
                .secureUrl(request.getSecureUrl())
                .format(request.getFormat())
                .width(request.getWidth())
                .height(request.getHeight())
                .bytes(request.getBytes());

        // Chỉ set duration & resourceType nếu là video/audio
        if (!"IMAGE".equals(resourceType)) {
            builder.duration(request.getDuration());
            builder.resourceType(resourceType);
        }

        return builder.build();
    }

    // 3. Dọn dẹp file tạm
    @Override
    public Map<String, Object> cleanupTemporaryMedia() {
        Map<String, Object> totalDeleted = new HashMap<>();
        try {
            log.info("Bắt đầu dọn dẹp các file media có tag 'tmp'...");

            ApiResponse imageResult = cloudinary.api().deleteResourcesByTag("tmp",
                    ObjectUtils.asMap("resource_type", "image"));
            ApiResponse videoResult = cloudinary.api().deleteResourcesByTag("tmp",
                    ObjectUtils.asMap("resource_type", "video"));

            totalDeleted.put("images", imageResult.get("deleted"));
            totalDeleted.put("videos", videoResult.get("deleted"));

            log.info("Dọn dẹp hoàn tất: {}", totalDeleted);
        } catch (Exception e) {
            log.error("Lỗi khi dọn dẹp media tạm thời trên Cloudinary", e);
            throw new RuntimeException("Đã xảy ra lỗi khi xóa file tạm trên Cloudinary");
        }
        return totalDeleted;
    }

    // CRON: Tự động dọn rác mỗi 3 giờ
    @Scheduled(fixedRate = 3 * 60 * 60 * 1000) // 3 giờ
    public void scheduledCleanup() {
        log.info("[CRON] Tự động kích hoạt dọn rác Cloudinary...");
        try {
            cleanupTemporaryMedia();
        } catch (Exception e) {
            log.error("[CRON] Lỗi khi dọn rác tự động", e);
        }
    }
}
