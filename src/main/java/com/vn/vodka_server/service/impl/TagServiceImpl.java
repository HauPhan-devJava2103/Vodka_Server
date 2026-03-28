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
import com.vn.vodka_server.dto.response.TagStatsResponse;
import com.vn.vodka_server.model.Tag;
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

    // Admin2: Thống kê tổng quan tags
    @Override
    public TagStatsResponse getTagStats() {
        // 1. Tổng số tags
        long totalTags = tagRepository.count();

        // 2. Tag phổ biến nhất — Pageable(0,1) ~ LIMIT 1
        List<TagAdminProjection> popularList = tagRepository.findMostPopularTag(PageRequest.of(0, 1));
        TagAdminProjection popularP = popularList.isEmpty() ? null : popularList.get(0);
        TagStatsResponse.MostPopularTag mostPopular;
        if (popularP == null)
            mostPopular = null;
        else
            mostPopular = TagStatsResponse.MostPopularTag.builder()
                    .name(popularP.getName())
                    .movieCount(popularP.getMovieCount())
                    .build();

        // 3. Số phim chưa gắn tag nào
        long unclassified = tagRepository.countMoviesWithNoTag();

        // 4. Tag mới tạo gần đây nhất
        Tag latest = tagRepository.findTopByOrderByCreatedAtDesc();
        TagStatsResponse.LatestTag latestTag;
        if (latest == null)
            latestTag = null;
        else
            latestTag = TagStatsResponse.LatestTag.builder()
                    .name(latest.getName())
                    .createdAt(toRelativeTime(latest.getCreatedAt()))
                    .build();

        return TagStatsResponse.builder()
                .totalTags(totalTags)
                .mostPopularTag(mostPopular)
                .unclassifiedMovies(unclassified)
                .latestTag(latestTag)
                .build();
    }

    // Helper methods
    // Chuyển date thành chuỗi dạng: "Hôm nay", "Hôm qua", "N ngày trước"
    private String toRelativeTime(Date date) {
        if (date == null)
            return "N/A";
        long days = (System.currentTimeMillis() - date.getTime()) / 86400000;
        if (days == 0)
            return "Hôm nay";
        if (days == 1)
            return "Hôm qua";
        return days + " ngày trước";
    }

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