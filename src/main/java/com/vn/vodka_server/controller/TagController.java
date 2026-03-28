package com.vn.vodka_server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.dto.response.TagResponse;
import com.vn.vodka_server.service.TagService;

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

    // API Admin: Lấy danh sách tags với phân trang, tìm kiếm, sắp xếp
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
}