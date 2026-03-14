package com.vn.vodka_server.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.response.GenreResponse;
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
                .map(genre -> GenreResponse.builder() // Mỗi đối tượng đi qua sẽ được biến đổi thành GenreResponse
                        .id(String.valueOf(genre.getId())) // dùng builder tiện và sạch hơn tạo contructor
                        .name(genre.getName())
                        .build())
                .toList(); // Trả về danh sách GenreResponse
    }
}
