package com.vn.vodka_server.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.vodka_server.dto.request.CreateMovieRequest;
import com.vn.vodka_server.dto.request.CreateMovieRequest.EpisodeRequest;
import com.vn.vodka_server.dto.request.CreateMovieRequest.GenreInput;
import com.vn.vodka_server.dto.request.CreateMovieRequest.SeasonRequest;
import com.vn.vodka_server.dto.request.CreateMovieRequest.TagInput;
import com.vn.vodka_server.exception.BadRequestException;
import com.vn.vodka_server.model.Episode;
import com.vn.vodka_server.model.Genre;
import com.vn.vodka_server.model.Movie;
import com.vn.vodka_server.model.Season;
import com.vn.vodka_server.model.Tag;
import com.vn.vodka_server.repository.GenreRepository;
import com.vn.vodka_server.repository.MovieRepository;
import com.vn.vodka_server.repository.SeasonRepository;
import com.vn.vodka_server.repository.TagRepository;
import com.vn.vodka_server.service.MovieAdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieAdminServiceImpl implements MovieAdminService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;
    private final SeasonRepository seasonRepository;

    @Override
    public void createMovie(CreateMovieRequest request) {
        // 1. Seasons không được rỗng
        if (request.getSeasons() == null || request.getSeasons().isEmpty())
            throw new BadRequestException("Phim phải có ít nhất 1 season");

        // 2. Mỗi season phải có ít nhất 1 episode
        for (SeasonRequest sr : request.getSeasons())
            if (sr.getEpisodes() == null || sr.getEpisodes().isEmpty())
                throw new BadRequestException("Season '" + sr.getTitle() + "' phải có ít nhất 1 tập phim");

        // 3. Validate genreId tồn tại trong DB
        if (request.getGenres() != null)
            for (GenreInput gi : request.getGenres())
                if (!genreRepository.existsById(gi.getId()))
                    throw new BadRequestException("Thể loại id=" + gi.getId() + " không tồn tại");

        // 4. Validate tagId tồn tại trong DB
        if (request.getTags() != null)
            for (TagInput ti : request.getTags())
                if (!tagRepository.existsById(ti.getId()))
                    throw new BadRequestException("Tag id=" + ti.getId() + " không tồn tại");

        // 5. Tạo Set Genre theo id
        // Chỉ cần id để Hibernate ghi vào bảng movie_genre
        Set<Genre> genres;
        if (request.getGenres() == null)
            genres = Set.of();
        else
            genres = request.getGenres().stream()
                    .map(g -> genreRepository.getReferenceById(g.getId()))
                    .collect(Collectors.toSet());

        // 6. Tạo Set Tag theo id
        Set<Tag> tags;
        if (request.getTags() == null)
            tags = Set.of();
        else
            tags = request.getTags().stream()
                    .map(t -> tagRepository.getReferenceById(t.getId()))
                    .collect(Collectors.toSet());

        // 7. Tạo Movie
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .releaseYear(request.getReleaseYear())
                .postUrl(request.getPosterUrl())
                .bannerUrl(request.getBannerUrl())
                .movieType(request.getMovieType())
                .genres(genres)
                .tags(tags)
                .viewCount(0L)
                .favorites(0L)
                .rating(0.0)
                .build();

        Movie savedMovie = movieRepository.save(movie);

        // 8. Tạo Seasons + Episodes
        for (SeasonRequest sr : request.getSeasons()) {
            Season season = Season.builder()
                    .seasonNumber(sr.getSeasonNumber())
                    .title(sr.getTitle())
                    .movie(savedMovie)
                    .build();

            List<Episode> episodes = sr.getEpisodes().stream()
                    .map(er -> Episode.builder()
                            .episodeNumber(er.getEpisodeNumber())
                            .title(er.getTitle())
                            .description(er.getDescription())
                            .videoUrl(er.getVideoUrl())
                            .duration(er.getDuration())
                            .season(season)
                            .build())
                    .toList();

            season.setEpisodes(episodes);
            // Save season -> Hibernate tự động save luôn tất cả episodes bên trong
            seasonRepository.save(season);
        }
    }
}
