package com.vn.vodka_server.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.vodka_server.dto.projection.GenreAdminProjection;
import com.vn.vodka_server.dto.request.CreateGenreRequest;
import com.vn.vodka_server.dto.response.GenreResponse;
import com.vn.vodka_server.dto.response.GenreStatsResponse;
import com.vn.vodka_server.exception.BadRequestException;
import com.vn.vodka_server.exception.ResourceNotFoundException;
import com.vn.vodka_server.model.Genre;
import com.vn.vodka_server.repository.GenreRepository;
import com.vn.vodka_server.service.GenreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public List<GenreResponse> getAllGenres() {
        // Lấy tất cả thể loại từ DB, sau đó dùng Stream API để chuyển đổi sang
        // GenreResponse
        return genreRepository.findAll().stream()
                .map(this::mapToPublicResponse)
                .toList();
    }

    // Admin1: Lấy danh sách genres có phân trang, tìm kiếm, sắp xếp
    @Override
    public Page<GenreResponse> getAdminGenres(int page, int pageSize, String search, String sort) {
        // 1. Parse sort string thành Spring Sort
        Sort sortObj = parseSort(sort);

        // 2. Tạo Pageable — Spring Data tính page từ 0, client gửi từ 1
        Pageable pageable = PageRequest.of(page - 1, pageSize, sortObj);

        // 3. Gọi repository với search và pageable
        Page<GenreAdminProjection> projectionPage = genreRepository.findAdminGenres(search, pageable);

        // 4. Map projection -> GenreResponse
        return projectionPage.map(this::mapToAdminResponse);
    }

    // Admin2: Tạo mới thể loại
    @Override
    public GenreResponse createGenre(CreateGenreRequest request) {
        // 1. Kiểm tra slug đã tồn tại chưa
        if (genreRepository.existsBySlug(request.getSlug())) {
            throw new BadRequestException("Slug '" + request.getSlug() + "' đã tồn tại");
        }

        // 2. Tạo và lưu entity vào DB
        Genre genre = Genre.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .build();
        Genre saved = genreRepository.save(genre);

        // 3. Map sang response — genre mới không có phim nào nên movieCount = 0
        return GenreResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .slug(saved.getSlug())
                .movieCount(0L)
                .createdAt(formatDate(saved.getCreatedAt()))
                .build();
    }

    // Admin3: Lấy chi tiết 1 genre theo ID
    @Override
    public GenreResponse getAdminGenreById(Long id) {
        GenreAdminProjection p = genreRepository.findAdminGenreById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thể loại không tồn tại"));

        return GenreResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .movieCount(p.getMovieCount())
                .createdAt(formatDate(p.getCreatedAt()))
                .updatedAt(formatDate(p.getUpdatedAt()))
                .build();
    }

    // Admin4: Cập nhật genre
    @Override
    public GenreResponse updateGenre(Long id, CreateGenreRequest request) {
        // 1. Tìm genre — ném 404 nếu không tồn tại
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thể loại không tồn tại"));

        // 2. Kiểm tra slug trùng với genre KHÁC (không tính nó)
        if (genreRepository.existsBySlugAndIdNot(request.getSlug(), id)) {
            throw new BadRequestException("Slug '" + request.getSlug() + "' đã tồn tại");
        }

        // 3. Cập nhật — Hibernate tự set updatedAt khi save
        genre.setName(request.getName());
        genre.setSlug(request.getSlug());
        genreRepository.save(genre);

        // 4. Trả về data
        return getAdminGenreById(id);
    }

    // Admin5: Xóa genre
    @Override
    @Transactional
    public void deleteGenre(Long id) {
        // 1. Tìm genre — ném 404 nếu không tồn tại
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thể loại không tồn tại"));

        // 2. Gỡ liên kết với tất cả movie (xóa dữ liệu bảng movie_genre)
        // Hibernate theo dõi thay đổi -> tự sinh DELETE FROM movie_genre
        genre.getMovies().forEach(movie -> movie.getGenres().remove(genre));

        // 3. Xóa genre sau khi đã gỡ liên kết
        genreRepository.delete(genre);
    }

    // Admin6: Thống kê tổng quan
    @Override
    public GenreStatsResponse getGenreStats() {
        // 1. Tổng số genres
        long totalGenres = genreRepository.count();

        // 2. Genre phổ biến nhất — Pageable(0,1) ~ LIMIT 1
        List<GenreAdminProjection> popularList = genreRepository
                .findMostPopularGenre(PageRequest.of(0, 1));
        GenreAdminProjection popularProjection = popularList.isEmpty() ? null : popularList.get(0);
        GenreStatsResponse.MostPopularGenre mostPopular;
        if (popularProjection == null)
            mostPopular = null;
        else
            mostPopular = GenreStatsResponse.MostPopularGenre.builder()
                    .name(popularProjection.getName())
                    .movieCount(popularProjection.getMovieCount())
                    .build();

        // 3. Số phim chưa gắn thể loại
        long unclassified = genreRepository.countMoviesWithNoGenre();

        // 4. Genre mới tạo gần đây nhất
        Genre latestGenre = genreRepository.findTopByOrderByCreatedAtDesc();
        GenreStatsResponse.LatestGenre latest;
        if (latestGenre == null)
            latest = null;
        else
            latest = GenreStatsResponse.LatestGenre.builder()
                    .name(latestGenre.getName())
                    .createdAt(toRelativeTime(latestGenre.getCreatedAt()))
                    .build();

        return GenreStatsResponse.builder()
                .totalGenres(totalGenres)
                .mostPopularGenre(mostPopular)
                .unclassifiedMovies(unclassified)
                .latestGenre(latest)
                .build();
    }

    // Helper methods
    // Format Date -> "dd/MM/yyyy", trả về "N/A" nếu null
    private String formatDate(Date date) {
        if (date == null)
            return "N/A";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

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

    // Parse sort string từ client -> Spring Sort object
    private Sort parseSort(String sort) {
        if (sort == null)
            return Sort.by("createdAt").descending();
        return switch (sort) {
            case "name_asc" -> Sort.by("name").ascending();
            case "movieCount_desc" -> Sort.by("movieCount").descending();
            case "createdAt_desc" -> Sort.by("createdAt").descending();
            default -> Sort.by("createdAt").descending();
        };
    }

    // Helper methods
    // 1. Function map cho Public
    private GenreResponse mapToPublicResponse(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .slug(genre.getSlug())
                .build();
    }

    // 2. Function map cho Admin (Projection)
    private GenreResponse mapToAdminResponse(GenreAdminProjection p) {
        return GenreResponse.builder()
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
