package com.vn.vodka_server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.request.CreateTagRequest;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.dto.response.TagResponse;
import com.vn.vodka_server.service.TagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    // API Public: Lấy tất cả tags (không cần đăng nhập)
    @GetMapping("/api/tags")
    public ResponseEntity<ApiResponse> getAllTags() {
        return ResponseEntity.ok(ApiResponse.success("Success", tagService.getAllTags()));
    }

    // API Admin1: Lấy danh sách tags với phân trang, tìm kiếm, sắp xếp
    @GetMapping("/api/admin/tags")
    public ResponseEntity<ApiResponse> getAdminTags(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {

        Page<TagResponse> result = tagService.getAdminTags(page, pageSize, search, sort);

        PaginationMeta pagination = PaginationMeta.builder()
                .totalItems(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .currentPage(page)
                .pageSize(pageSize)
                .build();

        return ResponseEntity.ok(ApiResponse.success("OK", result.getContent(), pagination));
    }

    // API Admin2: Thống kê tổng quan tags
    @GetMapping("/api/admin/tags/stats")
    public ResponseEntity<ApiResponse> getTagStats() {
        return ResponseEntity.ok(ApiResponse.success("OK", tagService.getTagStats()));
    }

    // API Admin3: Tạo mới tag
    @PostMapping("/api/admin/tags")
    public ResponseEntity<ApiResponse> createTag(@Valid @RequestBody CreateTagRequest request) {
        TagResponse created = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo tag thành công", created));
    }
}
