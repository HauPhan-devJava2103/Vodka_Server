package com.vn.vodka_server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.request.CreateGenreRequest;
import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.dto.response.GenreResponse;
import com.vn.vodka_server.dto.response.PaginationMeta;
import com.vn.vodka_server.service.GenreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    // API Public: Lấy tất cả genres (không cần đăng nhập)
    @GetMapping("/api/genres")
    public ResponseEntity<ApiResponse> getAllGenres() {
        return ResponseEntity.ok(ApiResponse.success("Success", genreService.getAllGenres()));
    }

    // API Admin1: Lấy danh sách genres với phân trang, tìm kiếm, sắp xếp
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

    // API Admin2: Tạo mới thể loại
    @PostMapping("/api/admin/genres")
    public ResponseEntity<ApiResponse> createGenre(@Valid @RequestBody CreateGenreRequest request) {
        GenreResponse created = genreService.createGenre(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo thể loại thành công", created));
    }
}

