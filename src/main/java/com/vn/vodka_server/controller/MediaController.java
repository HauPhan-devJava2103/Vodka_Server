package com.vn.vodka_server.controller;

import com.vn.vodka_server.dto.request.MediaConfirmRequest;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.UploadResponse;
import com.vn.vodka_server.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    // 1. Lấy signature
    @GetMapping("/signature")
    public ResponseEntity<ApiResponse> getSignature() {
        Map<String, Object> data = mediaService.getUploadSignature();
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin upload Cloudinary thành công", data));
    }

    // 2. Xác nhận upload
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse> confirmMedia(@Valid @RequestBody MediaConfirmRequest request) {
        try {
            UploadResponse data = mediaService.confirmMedia(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Xác nhận media thành công", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Xác nhận media thất bại: " + e.getMessage()));
        }
    }

    // 3. Dọn file rác (chỉ ADMIN)
    @DeleteMapping("/cleanup-tmp")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> cleanupTmp() {
        try {
            Map<String, Object> result = mediaService.cleanupTemporaryMedia();
            return ResponseEntity.ok(ApiResponse.success("Dọn dẹp file tạm thành công", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Dọn dẹp thất bại: " + e.getMessage()));
        }
    }
}
