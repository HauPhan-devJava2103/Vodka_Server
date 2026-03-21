package com.vn.vodka_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.vodka_server.dto.response.ApiResponse;
import com.vn.vodka_server.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllTags() {
        return ResponseEntity.ok(ApiResponse.success("Success", tagService.getAllTags()));
    }
}
