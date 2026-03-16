package com.vn.vodka_server.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import com.vn.vodka_server.dto.response.FeaturedMovieResponse;
import com.vn.vodka_server.dto.response.GenreResponse;
import com.vn.vodka_server.dto.response.TagResponse;
import com.vn.vodka_server.dto.response.TrendingMovieResponse;
import com.vn.vodka_server.repository.MovieRepository;
import com.vn.vodka_server.service.MovieService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

        private final MovieRepository movieRepository;

        // Lấy 10 phim nổi bật nhất (10 phim có rating cao nhất)
        @Override
        public List<FeaturedMovieResponse> getFeaturedMovies() {
                return movieRepository.findTop2ByOrderByRatingDesc().stream()
                                .map(movie -> FeaturedMovieResponse.builder()
                                                .id(String.valueOf(movie.getId()))
                                                .title(movie.getTitle())
                                                .posterUrl(movie.getPostUrl())
                                                .bannerUrl(movie.getBannerUrl())
                                                .releaseYear(movie.getReleaseYear())
                                                .rating(movie.getRating())

                                                // Map sang Object GenreResponse
                                                .genre(movie.getGenres().stream()
                                                                .map(genre -> new GenreResponse(
                                                                                String.valueOf(genre.getId()),
                                                                                genre.getName()))
                                                                .collect(Collectors.toList()))

                                                // Map sang Object TagResponse
                                                .tags(movie.getTags().stream()
                                                                .map(tag -> new TagResponse(String.valueOf(tag.getId()),
                                                                                tag.getName()))
                                                                .collect(Collectors.toList()))

                                                .description(movie.getDescription())
                                                .build())
                                .collect(Collectors.toList());
        }

        // Lấy phim thịnh hành nhất (phim có lượt xem cao nhất)
        @Override
        public List<TrendingMovieResponse> getTrendingMovies(int limit) {
                return movieRepository.findTrendingMovies(Limit.of(limit)).stream()
                                .map(movie -> TrendingMovieResponse.builder()
                                                .id(String.valueOf(movie.getId()))
                                                .title(movie.getTitle())
                                                .build())
                                .toList();
        }
}
