package com.vn.vodka_server.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.vn.vodka_server.dto.request.CreateTagRequest;
import com.vn.vodka_server.dto.response.TagResponse;
import com.vn.vodka_server.dto.response.TagStatsResponse;

public interface TagService {
    List<TagResponse> getAllTags();

    // Admin1: Lấy danh sách tags có phân trang, tìm kiếm, sắp xếp
    Page<TagResponse> getAdminTags(int page, int pageSize, String search, String sort);

    // Admin2: Thống kê tổng quan tags
    TagStatsResponse getTagStats();

    // Admin3: Tạo tag mới
    TagResponse createTag(CreateTagRequest request);
}