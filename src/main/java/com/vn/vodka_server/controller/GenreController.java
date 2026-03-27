package com.vn.vodka_server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.GenreResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.service.GenreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    // API Public: Lấy tất cả genres (không cần đăng nhập)
    @GetMapping("/api/genres")
    public ResponseEntity<ApiResponse> getAllGenres() {
        // API1: Trả về danh sách thể loại đã được chuyển đổi thành DTO (GenreResponse)
        return ResponseEntity.ok(ApiResponse.success("Success", genreService.getAllGenres()));
    }

    // API Admin: Lấy danh sách genres với phân trang, tìm kiếm, sắp xếp
    @GetMapping("/api/admin/genres")
    public ResponseEntity<ApiResponse> getAdminGenres(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {

        Page<GenreResponse> result = genreService.getAdminGenres(page, pageSize, search, sort);

        PaginationMeta pagination = PaginationMeta.builder()
                .totalItems(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .currentPage(page)
                .pageSize(pageSize)
                .build();

        return ResponseEntity.ok(ApiResponse.success("OK", result.getContent(), pagination));
    }
}
