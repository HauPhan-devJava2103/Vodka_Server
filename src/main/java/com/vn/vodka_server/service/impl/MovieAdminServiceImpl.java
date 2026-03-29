package com.vn.vodka_server.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.vodka_server.dto.request.CreateMovieRequest;
import com.vn.vodka_server.dto.request.CreateMovieRequest.GenreInput;
import com.vn.vodka_server.dto.request.CreateMovieRequest.SeasonRequest;
import com.vn.vodka_server.dto.request.CreateMovieRequest.TagInput;
import com.vn.vodka_server.dto.request.UpdateMovieRequest;
import com.vn.vodka_server.dto.response.AdminEpisodeResponse;
import com.vn.vodka_server.dto.response.AdminMovieDetailResponse;
import com.vn.vodka_server.dto.response.AdminSeasonResponse;
import com.vn.vodka_server.dto.response.GenreResponse;
import com.vn.vodka_server.dto.response.TagResponse;
import com.vn.vodka_server.exception.BadRequestException;
import com.vn.vodka_server.exception.ResourceNotFoundException;
import com.vn.vodka_server.model.Episode;
import com.vn.vodka_server.model.Genre;
import com.vn.vodka_server.model.Movie;
import com.vn.vodka_server.model.Season;
import com.vn.vodka_server.model.Tag;
import com.vn.vodka_server.repository.EpisodeRepository;
import com.vn.vodka_server.repository.GenreRepository;
import com.vn.vodka_server.repository.MovieRepository;
import com.vn.vodka_server.repository.SeasonRepository;
import com.vn.vodka_server.repository.TagRepository;
import com.vn.vodka_server.service.MovieAdminService;

import lombok.RequiredArgsConstructor;

// @Transactional ở cấp class:
// - Mọi method public trong class này đều chạy trong 1 transaction
// - Nếu bất kỳ bước nào lỗi -> toàn bộ rollback
// - Hibernate "dirty checking": entity lấy từ DB (findById) nếu bị sửa field
//   -> Hibernate tự động bắn UPDATE SQL khi transaction commit
//   -> Không cần gọi .save() thủ công cho entity đã tồn tại
@Service
@RequiredArgsConstructor
@Transactional
public class MovieAdminServiceImpl implements MovieAdminService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;
    private final SeasonRepository seasonRepository;
    private final EpisodeRepository episodeRepository;

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

    // update movie

    // Toàn bộ method này chạy trong @Transactional (khai báo ở class level)
    // Nghĩa là:
    // - Bắt đầu method -> Hibernate mở transaction
    // - Kết thúc method -> Hibernate commit transaction
    // - Trong quá trình commit, Hibernate so sánh trạng thái hiện tại
    // của các entity với "ảnh chụp" lúc lấy từ DB (dirty checking)
    // - Field nào KHÁC -> bắn UPDATE SQL
    // - Field nào GIỐNG -> bỏ qua, KHÔNG bắn SQL
    // ==> chỉ cần set field khi nó thay đổi, KHÔNG cần gọi .save() cho entity đã
    // tồn tại (entity lấy từ findById)
    @Override
    public void updateMovie(Long id, UpdateMovieRequest request) {

        // Kiểm tra dữ liệu đầu vào

        // 1. Tìm movie theo id -> ném 404 nếu không có
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phim id=" + id + " không tồn tại"));

        // 2. Seasons không được rỗng
        if (request.getSeasons() == null || request.getSeasons().isEmpty())
            throw new BadRequestException("Phim phải có ít nhất 1 season");

        // 3. Mỗi season phải có ít nhất 1 episode
        for (UpdateMovieRequest.SeasonRequest sr : request.getSeasons())
            if (sr.getEpisodes() == null || sr.getEpisodes().isEmpty())
                throw new BadRequestException("Season '" + sr.getTitle() + "' phải có ít nhất 1 tập phim");

        // 4. Validate genreId tồn tại trong DB
        if (request.getGenres() != null)
            for (UpdateMovieRequest.GenreInput gi : request.getGenres())
                if (!genreRepository.existsById(gi.getId()))
                    throw new BadRequestException("Thể loại id=" + gi.getId() + " không tồn tại");

        // 5. Validate tagId tồn tại trong DB
        if (request.getTags() != null)
            for (UpdateMovieRequest.TagInput ti : request.getTags())
                if (!tagRepository.existsById(ti.getId()))
                    throw new BadRequestException("Tag id=" + ti.getId() + " không tồn tại");

        // Cập nhật thông tin movie
        // Chỉ set field khi giá trị KHÁC với DB
        // Hibernate dirty checking sẽ tự phát hiện field nào bị thay đổi
        // và CHỈ bắn UPDATE SQL cho những field đó khi transaction commit

        if (!Objects.equals(movie.getTitle(), request.getTitle()))
            movie.setTitle(request.getTitle());

        if (!Objects.equals(movie.getDescription(), request.getDescription()))
            movie.setDescription(request.getDescription());

        if (!Objects.equals(movie.getReleaseYear(), request.getReleaseYear()))
            movie.setReleaseYear(request.getReleaseYear());

        if (!Objects.equals(movie.getPostUrl(), request.getPosterUrl()))
            movie.setPostUrl(request.getPosterUrl());

        if (!Objects.equals(movie.getBannerUrl(), request.getBannerUrl()))
            movie.setBannerUrl(request.getBannerUrl());

        if (!Objects.equals(movie.getMovieType(), request.getMovieType()))
            movie.setMovieType(request.getMovieType());

        // Genres thì luôn thay thế toàn bộ (không so sánh từng cái)
        // vì Hibernate quản lý @ManyToMany tự xoá movie_genre cũ + insert mới
        Set<Genre> newGenres;
        if (request.getGenres() == null)
            newGenres = Set.of();
        else
            newGenres = request.getGenres().stream()
                    .map(g -> genreRepository.getReferenceById(g.getId()))
                    .collect(Collectors.toSet());
        movie.setGenres(newGenres);

        // Tags tg tự Genres
        Set<Tag> newTags;
        if (request.getTags() == null)
            newTags = Set.of();
        else
            newTags = request.getTags().stream()
                    .map(t -> tagRepository.getReferenceById(t.getId()))
                    .collect(Collectors.toSet());
        movie.setTags(newTags);
        // KHÔNG cần gọi movieRepository.save(movie)
        // Vì movie được lấy từ findById -> Hibernate đang "theo dõi" entity này
        // Khi transaction commit -> dirty checking tự bắn UPDATE SQL

        // Merge seasons
        // 6. Load tất cả seasons hiện có trong DB
        List<Season> dbSeasons = seasonRepository.findByMovieIdOrderBySeasonNumberAsc(id);
        Set<Long> dbSeasonIds = dbSeasons.stream()
                .map(Season::getId)
                .collect(Collectors.toSet());

        // 7. Lấy season ids từ payload (chỉ những cái có id != null)
        Set<Long> payloadSeasonIds = request.getSeasons().stream()
                .filter(s -> s.getId() != null)
                .map(UpdateMovieRequest.SeasonRequest::getId)
                .collect(Collectors.toSet());

        // 8. delete seasons có trong DB nhưng không có trong payload
        // cascade ALL -> tự xoá luôn tất cả episodes bên trong
        dbSeasonIds.stream()
                .filter(seasonId -> !payloadSeasonIds.contains(seasonId))
                .forEach(seasonRepository::deleteById);

        // 9. Loop từng season trong payload
        for (UpdateMovieRequest.SeasonRequest sr : request.getSeasons()) {

            if (sr.getId() != null) {
                // TH A: Season đã tồn tại -> update
                Season season = seasonRepository.findById(sr.getId())
                        .orElseThrow(() -> new BadRequestException(
                                "Season id=" + sr.getId() + " không tồn tại"));

                // Chỉ set field khi KHÁC với DB
                if (!Objects.equals(season.getTitle(), sr.getTitle()))
                    season.setTitle(sr.getTitle());
                if (!Objects.equals(season.getSeasonNumber(), sr.getSeasonNumber()))
                    season.setSeasonNumber(sr.getSeasonNumber());

                // Merge episodes bên trong season này
                mergeEpisodes(season, sr.getEpisodes());
                // không gọi .save() — dirty checking tự lo

            } else {
                // TH B: Season mới -> insert
                Season season = Season.builder()
                        .seasonNumber(sr.getSeasonNumber())
                        .title(sr.getTitle())
                        .movie(movie)
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
                // Entity mới -> bắt buộc phải gọi save() vì Hibernate chưa biết entity này
                seasonRepository.save(season);
            }
        }
    }

    // merge episodes - private helper
    // So sánh episodes trong DB với episodes trong payload:
    // - Có trong DB + có trong payload -> update (chỉ set field khác)
    // - Có trong DB + không có trong payload -> delete
    // - Không có id trong payload -> insert mới
    private void mergeEpisodes(Season season,
            List<UpdateMovieRequest.EpisodeRequest> episodeRequests) {

        // 1. Load tất cả episodes hiện có của season này từ DB
        List<Episode> dbEpisodes = episodeRepository.findBySeasonId(season.getId());
        Set<Long> dbEpIds = dbEpisodes.stream()
                .map(Episode::getId)
                .collect(Collectors.toSet());

        // 2. Episode ids từ payload (chỉ những cái có id != null)
        Set<Long> payloadEpIds = episodeRequests.stream()
                .filter(e -> e.getId() != null)
                .map(UpdateMovieRequest.EpisodeRequest::getId)
                .collect(Collectors.toSet());

        // 3. DELETE episodes có trong DB nhưng KHÔNG có trong payload
        dbEpIds.stream()
                .filter(epId -> !payloadEpIds.contains(epId))
                .forEach(episodeRepository::deleteById);

        // 4. Loop từng episode trong payload
        for (UpdateMovieRequest.EpisodeRequest er : episodeRequests) {

            if (er.getId() != null) {
                // Episode đã tồn tại -> update chỉ khi khác
                Episode ep = episodeRepository.findById(er.getId())
                        .orElseThrow(() -> new BadRequestException(
                                "Episode id=" + er.getId() + " không tồn tại"));

                // Nếu tất cả field giống -> Hibernate không bắn SQL UPDATE
                if (!Objects.equals(ep.getTitle(), er.getTitle()))
                    ep.setTitle(er.getTitle());
                if (!Objects.equals(ep.getEpisodeNumber(), er.getEpisodeNumber()))
                    ep.setEpisodeNumber(er.getEpisodeNumber());
                if (!Objects.equals(ep.getDescription(), er.getDescription()))
                    ep.setDescription(er.getDescription());
                if (!Objects.equals(ep.getVideoUrl(), er.getVideoUrl()))
                    ep.setVideoUrl(er.getVideoUrl());
                if (!Objects.equals(ep.getDuration(), er.getDuration()))
                    ep.setDuration(er.getDuration());

                // không gọi .save() — dirty checking tự lo

            } else {
                // Episode mới -> insert (bắt buộc save)
                Episode ep = Episode.builder()
                        .episodeNumber(er.getEpisodeNumber())
                        .title(er.getTitle())
                        .description(er.getDescription())
                        .videoUrl(er.getVideoUrl())
                        .duration(er.getDuration())
                        .season(season)
                        .build();
                episodeRepository.save(ep);
            }
        }
    }

    // Lấy chi tiết phim cho Admin
    @Override
    @Transactional(readOnly = true)
    public AdminMovieDetailResponse getMovieDetail(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phim với id = " + id + " không tồn tại"));

        return AdminMovieDetailResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .releaseYear(movie.getReleaseYear())
                .posterUrl(movie.getPostUrl())
                .bannerUrl(movie.getBannerUrl())
                .movieType(movie.getMovieType())
                .genres(movie.getGenres().stream()
                        .map(g -> GenreResponse.builder()
                                .id(g.getId())
                                .name(g.getName())
                                .slug(g.getSlug())
                                .build())
                        .toList())
                .tags(movie.getTags().stream()
                        .map(t -> TagResponse.builder()
                                .id(t.getId())
                                .name(t.getName())
                                .slug(t.getSlug())
                                .build())
                        .toList())
                .seasons(movie.getSeasons().stream()
                        .map(s -> AdminSeasonResponse.builder()
                                .id(s.getId())
                                .seasonNumber(s.getSeasonNumber())
                                .title(s.getTitle())
                                .episodes(s.getEpisodes().stream()
                                        .map(e -> AdminEpisodeResponse.builder()
                                                .id(e.getId())
                                                .episodeNumber(e.getEpisodeNumber())
                                                .title(e.getTitle())
                                                .description(e.getDescription())
                                                .videoUrl(e.getVideoUrl())
                                                .duration(e.getDuration())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }
}
