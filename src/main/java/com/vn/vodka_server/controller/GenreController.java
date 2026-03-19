package com.vn.vodka_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.service.GenreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllGenres() {
        // API1: Trả về danh sách thể loại đã được chuyển đổi thành DTO (GenreResponse)
        return ResponseEntity.ok(ApiResponse.success("Success", genreService.getAllGenres()));
    }
}
