-- ============================================
-- RESET + INSERT DỮ LIỆU TEST (ẢNH THẬT)
-- Database: Vodka_Server
-- ============================================

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE favorite;
TRUNCATE TABLE movie_genre;
TRUNCATE TABLE movie_tag;
TRUNCATE TABLE review_reply;
TRUNCATE TABLE review;
TRUNCATE TABLE episode;
TRUNCATE TABLE season;
TRUNCATE TABLE watch_history;
TRUNCATE TABLE media;
TRUNCATE TABLE movie;
TRUNCATE TABLE genre;
TRUNCATE TABLE tag;
TRUNCATE TABLE user;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Genre
INSERT INTO genre (id, name, slug, created_at, updated_at) VALUES
(1, 'Hành Động', 'hanh-dong', NOW(), NOW()),
(2, 'Kinh Dị', 'kinh-di', NOW(), NOW()),
(3, 'Tình Cảm', 'tinh-cam', NOW(), NOW()),
(4, 'Hoạt Hình', 'hoat-hinh', NOW(), NOW()),
(5, 'Khoa Học Viễn Tưởng', 'khoa-hoc-vien-tuong', NOW(), NOW());

-- 2. Tag
INSERT INTO tag (id, name, slug, created_at, updated_at) VALUES
(1, 'Hot', 'hot', NOW(), NOW()),
(2, 'Mới', 'moi', NOW(), NOW()),
(3, 'Đề Cử', 'de-cu', NOW(), NOW()),
(4, 'Oscar', 'oscar', NOW(), NOW());

-- 3. Movie (ảnh poster + banner thật từ TMDB)
INSERT INTO movie (id, title, post_url, banner_url, release_year, rating, description, view_count, favorites, created_at, updated_at) VALUES
(1, 'Avengers: Endgame',
 'https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg',
 'https://image.tmdb.org/t/p/original/7RyHsO4yDXtBv1zUU3mTpHeQ0d5.jpg',
 2019, 8.4, 'Sau sự kiện tàn khốc của Thanos, biệt đội Avengers còn lại tập hợp lần cuối để hoàn tác mọi thứ.', 120000, 5000, NOW(), NOW()),

(2, 'The Conjuring: The Devil Made Me Do It',
 'https://image.tmdb.org/t/p/w500/xbSuFiJbbBWCkyCCKIMfuDCA4yV.jpg',
 'https://image.tmdb.org/t/p/original/qi6Lx1GkbNs35dwW0fLOqJlIek5.jpg',
 2021, 6.8, 'Ed và Lorraine Warren điều tra vụ án giết người đầu tiên trong lịch sử Mỹ mà bị cáo dùng lý do bị quỷ ám.', 45000, 1800, NOW(), NOW()),

(3, 'Your Name (Kimi no Na wa)',
 'https://image.tmdb.org/t/p/w500/q719jXXEzOoYaps6babgKnONONX.jpg',
 'https://image.tmdb.org/t/p/original/dIWwZW7dJJtqC6CgWzYkNVKIUm8.jpg',
 2016, 8.6, 'Hai thiếu niên ở hai nơi khác nhau bí ẩn hoán đổi thân xác trong giấc ngủ, dần tìm cách gặp nhau ngoài đời thực.', 95000, 7200, NOW(), NOW()),

(4, 'John Wick: Chapter 4',
 'https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg',
 'https://image.tmdb.org/t/p/original/7I6VUdPj6tQECNHdviJkUHD2u89.jpg',
 2023, 7.7, 'John Wick khám phá con đường đánh bại The High Table, đối mặt với kẻ thù mới và liên minh cũ trên toàn cầu.', 88000, 4100, NOW(), NOW()),

(5, 'Parasite (Ký Sinh Trùng)',
 'https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg',
 'https://image.tmdb.org/t/p/original/TU9NIjwzjoKPwQHoHshkFcQUCG8.jpg',
 2019, 8.5, 'Gia đình Kim nghèo khó lần lượt xâm nhập vào gia đình Park giàu có, nhưng mọi thứ nhanh chóng vượt ngoài tầm kiểm soát.', 110000, 8500, NOW(), NOW()),

(6, 'Spider-Man: No Way Home',
 'https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg',
 'https://image.tmdb.org/t/p/original/14QbnygCuTO0vl7CAFmPf1fgZfV.jpg',
 2021, 8.0, 'Peter Parker nhờ Doctor Strange giúp đỡ khiến đa vũ trụ bị phá vỡ, mang theo những kẻ thù từ các thực tại khác.', 150000, 9000, NOW(), NOW()),

(7, 'Spirited Away (Vùng Đất Linh Hồn)',
 'https://image.tmdb.org/t/p/w500/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg',
 'https://image.tmdb.org/t/p/original/bSXfU4dwZyBA1vMmXvejdRXBvuF.jpg',
 2001, 8.5, 'Cô bé Chihiro lạc vào thế giới thần linh và phải tìm cách giải cứu cha mẹ bị biến thành lợn.', 80000, 6000, NOW(), NOW()),

(8, 'Interstellar',
 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg',
 'https://image.tmdb.org/t/p/original/xJHokMbljvjADYdit5fK1DVfjko.jpg',
 2014, 8.6, 'Một nhóm phi hành gia du hành qua lỗ sâu gần Sao Thổ để tìm ngôi nhà mới cho nhân loại.', 130000, 7800, NOW(), NOW());

-- 4. movie_genre
INSERT INTO movie_genre (movie_id, genre_id) VALUES
(1, 1), (1, 5),   -- Avengers: Hành Động, Sci-Fi
(2, 2),            -- Conjuring: Kinh Dị
(3, 3), (3, 4),   -- Your Name: Tình Cảm, Hoạt Hình
(4, 1),            -- John Wick: Hành Động
(5, 3),            -- Parasite: Tình Cảm
(6, 1), (6, 5),   -- Spider-Man: Hành Động, Sci-Fi
(7, 4),            -- Spirited Away: Hoạt Hình
(8, 5);            -- Interstellar: Sci-Fi

-- 5. movie_tag
INSERT INTO movie_tag (movie_id, tag_id) VALUES
(1, 1),            -- Avengers: Hot
(2, 2),            -- Conjuring: Mới
(3, 3), (3, 4),   -- Your Name: Đề Cử, Oscar
(4, 1), (4, 2),   -- John Wick: Hot, Mới
(5, 4), (5, 1),   -- Parasite: Oscar, Hot
(6, 1),            -- Spider-Man: Hot
(7, 4), (7, 3),   -- Spirited Away: Oscar, Đề Cử
(8, 4);            -- Interstellar: Oscar

-- 6. User (dùng API register để tạo user thật, hoặc INSERT trực tiếp)
-- Password "Test@123" → BCrypt hash
INSERT INTO user (id, full_name, email, password, role, status, provider, created_at, updated_at) VALUES
(1, 'Nguyễn Minh Trung', 'test@gmail.com', '$2a$10$8KxKBEpMIwO.6VCxq4LKjePqBf8VLSGx/5PprKVXH8soMG0ZQ0vA6', 'USER', 'ACTIVE', 'LOCAL', NOW(), NOW());

-- 7. ⭐ Favorite (user 1 yêu thích 5 phim)
INSERT INTO favorite (id, user_id, movie_id, created_at, updated_at) VALUES
(1, 1, 3, '2026-03-18 10:00:00', NOW()),  -- Your Name
(2, 1, 5, '2026-03-19 14:30:00', NOW()),  -- Parasite
(3, 1, 7, '2026-03-20 09:00:00', NOW()),  -- Spirited Away
(4, 1, 8, '2026-03-21 18:00:00', NOW()),  -- Interstellar
(5, 1, 1, '2026-03-22 15:00:00', NOW());  -- Avengers (mới nhất)

-- 8. ⭐ Watch History (user 1 đã xem 6 phim)
INSERT INTO watch_history (id, user_id, movie_id, watched_at, created_at, updated_at) VALUES
(1, 1, 1, '2026-03-15 20:00:00', NOW(), NOW()),  -- Avengers
(2, 1, 3, '2026-03-16 21:30:00', NOW(), NOW()),  -- Your Name
(3, 1, 5, '2026-03-17 19:00:00', NOW(), NOW()),  -- Parasite
(4, 1, 6, '2026-03-19 22:00:00', NOW(), NOW()),  -- Spider-Man
(5, 1, 8, '2026-03-20 20:30:00', NOW(), NOW()),  -- Interstellar
(6, 1, 7, '2026-03-22 14:00:00', NOW(), NOW());  -- Spirited Away (xem gần nhất)

-- 9. ⭐ Review (user đánh giá phim)
INSERT INTO review (id, user_id, movie_id, rating, content, created_at, updated_at) VALUES
(1, 1, 1, 9.0, 'Phim hay tuyệt vời! Cảnh chiến đấu cuối cùng quá hoành tráng.', '2026-03-15 21:00:00', NOW()),
(2, 1, 3, 10.0, 'Your Name là phim hoạt hình hay nhất mình từng xem. Cốt truyện xúc động.', '2026-03-16 22:00:00', NOW()),
(3, 1, 5, 9.5, 'Ký Sinh Trùng xứng đáng Oscar. Kịch bản quá thông minh!', '2026-03-17 20:00:00', NOW()),
(4, 1, 8, 8.5, 'Interstellar đẹp mắt nhưng hơi khó hiểu phần cuối.', '2026-03-19 23:00:00', NOW()),
(5, 1, 6, 8.0, 'Spider-Man No Way Home fan service tuyệt vời. Ba Spider-Man cùng xuất hiện!', '2026-03-20 21:30:00', NOW()),
(6, 1, 4, 7.5, 'John Wick 4 hành động mãn nhãn, cảnh cầu thang dài quá đỉnh.', '2026-03-21 22:00:00', NOW()),
(7, 1, 7, 9.0, 'Spirited Away kinh điển, xem đi xem lại vẫn thấy hay.', '2026-03-22 15:00:00', NOW());
