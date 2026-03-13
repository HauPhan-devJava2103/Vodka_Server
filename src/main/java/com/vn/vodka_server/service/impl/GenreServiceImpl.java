package com.vn.vodka_server.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vn.vodka_server.model.Genre;
import com.vn.vodka_server.repository.GenreRepository;
import com.vn.vodka_server.service.GenreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
