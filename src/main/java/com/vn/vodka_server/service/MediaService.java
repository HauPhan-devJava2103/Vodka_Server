package com.vn.vodka_server.service;

import com.vn.vodka_server.dto.request.MediaConfirmRequest;
import com.vn.vodka_server.dto.response.UploadResponse;

import java.util.Map;

public interface MediaService {
    Map<String, Object> getUploadSignature();
    UploadResponse confirmMedia(MediaConfirmRequest request);
    Map<String, Object> cleanupTemporaryMedia();
}
