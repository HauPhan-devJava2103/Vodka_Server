package com.vn.vodka_server.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vn.vodka_server.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // Validate file không rỗng
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        // Validate loại file
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh: JPG, PNG, WEBP");
        }

        // Upload lên Cloudinary
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "vodka_server",
                "resource_type", "image"
        ));

        // Trả về secure URL
        return uploadResult.get("secure_url").toString();
    }
}
