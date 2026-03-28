package com.vn.vodka_server.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.projection.TagAdminProjection;
import com.vn.vodka_server.dto.response.TagResponse;
import com.vn.vodka_server.repository.TagRepository;
import com.vn.vodka_server.service.TagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.findAllByOrderByNameAsc().stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .slug(tag.getSlug())
                        .build())
                .toList();
    }

    // Admin1: Lấy danh sách tags có phân trang, tìm kiếm, sắp xếp
    @Override
    public Page<TagResponse> getAdminTags(int page, int pageSize, String search, String sort) {
        // 1. Parse sort string thành Spring Sort
        Sort sortObj = parseSort(sort);

        // 2. Tạo Pageable — Spring Data tính page từ 0, client gửi từ 1
        Pageable pageable = PageRequest.of(page - 1, pageSize, sortObj);

        // 3. Gọi repository với search và pageable
        Page<TagAdminProjection> projectionPage = tagRepository.findAdminTags(search, pageable);

        // 4. Map projection -> TagResponse
        return projectionPage.map(this::mapToAdminResponse);
    }

    // Helper methods
    // Format Date -> "dd/MM/yyyy", trả về "N/A" nếu null
    private String formatDate(Date date) {
        if (date == null)
            return "N/A";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    // Parse sort string từ client -> Spring Sort object
    private Sort parseSort(String sort) {
        if (sort == null)
            return Sort.by("createdAt").descending();
        return switch (sort) {
            case "name_asc" -> Sort.by("name").ascending();
            case "movieCount_desc" -> Sort.by("movieCount").descending();
            case "viewCount_desc" -> Sort.by("viewCount").descending();
            case "createdAt_desc" -> Sort.by("createdAt").descending();
            default -> Sort.by("createdAt").descending();
        };
    }

    // Map TagAdminProjection -> TagResponse (dùng cho admin)
    private TagResponse mapToAdminResponse(TagAdminProjection p) {
        return TagResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .movieCount(p.getMovieCount())
                .viewCount(p.getViewCount())
                .createdAt(formatDate(p.getCreatedAt()))
                .updatedAt(formatDate(p.getUpdatedAt()))
                .build();
    }
}