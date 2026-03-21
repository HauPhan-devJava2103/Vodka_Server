-- ==========================================================
-- SQL DATASET FOR VODKA_SERVER (MYSQL)
-- ==========================================================

-- 1. Xóa dữ liệu cũ (Xóa theo thứ tự để tránh lỗi ràng buộc khóa ngoại)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE watch_history;
TRUNCATE TABLE review;
TRUNCATE TABLE episode;
TRUNCATE TABLE season;
TRUNCATE TABLE movie_tag;
TRUNCATE TABLE movie_genre;
TRUNCATE TABLE movie;
TRUNCATE TABLE tag;
TRUNCATE TABLE genre;
TRUNCATE TABLE user;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. Dữ liệu bảng Genre (Thể loại)
INSERT INTO genre (id, name, slug, created_at, updated_at) VALUES 
(1, 'Hành động', 'hanh-dong', NOW(), NOW()),
(2, 'Tâm lý', 'tam-ly', NOW(), NOW()),
(3, 'Viễn tưởng', 'vien-tuong', NOW(), NOW()),
(4, 'Kinh dị', 'kinh-di', NOW(), NOW()),
(5, 'Hoạt hình', 'hoat-hinh', NOW(), NOW());

-- 3. Dữ liệu bảng Tag (Nhãn)
INSERT INTO tag (id, name, slug, created_at, updated_at) VALUES 
(1, 'Phim Lẻ', 'phim-le', NOW(), NOW()),
(2, 'Phim Bộ', 'phim-bo', NOW(), NOW()),
(3, '4K', '4k', NOW(), NOW()),
(4, 'Chiếu rạp', 'chieu-rap', NOW(), NOW());

-- 4. Dữ liệu bảng User (Người dùng) - Để test Review và History
INSERT INTO user (id, fullName, email, password, avatar_url, role, status, created_at, updated_at) VALUES 
(1, 'Nguyen Van A', 'user@example.com', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LqwNVLiyv97P1QX6m3y2292Lryq', 'https://i.pravatar.cc/150?u=a', 'USER', 'ACTIVE', NOW(), NOW()),
(2, 'Admin Vodka', 'admin@example.com', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LqwNVLiyv97P1QX6m3y2292Lryq', 'https://i.pravatar.cc/150?u=admin', 'ADMIN', 'ACTIVE', NOW(), NOW());

-- 5. Dữ liệu bảng Movie (Phim)
INSERT INTO movie (id, title, post_url, banner_url, release_year, rating, description, view_count, favorites, created_at, updated_at) VALUES 
(1, 'Lật Mặt 7: Một Điều Ước', 'https://example.com/poster1.jpg', 'https://example.com/banner1.jpg', 2024, 9.0, 'Phim điện ảnh Việt Nam đề tài gia đình...', 500000, 12000, NOW(), NOW()),
(2, 'Người Nhện: Không còn nhà', 'https://example.com/poster2.jpg', 'https://example.com/banner2.jpg', 2021, 8.5, 'Peter Parker tìm đến Doctor Strange...', 1200000, 50000, NOW(), NOW()),
(3, 'Mai', 'https://example.com/poster3.jpg', 'https://example.com/banner3.jpg', 2024, 8.2, 'Câu chuyện về Mai...', 300000, 8000, NOW(), NOW()),
(4, 'Dune: Hành Tinh Cát 2', 'https://example.com/poster4.jpg', 'https://example.com/banner4.jpg', 2024, 8.8, 'Hành trình của Paul Atreides...', 450000, 15000, NOW(), NOW()),
(5, 'Gặp Lại Chị Bầu', 'https://example.com/poster5.jpg', 'https://example.com/banner5.jpg', 2024, 7.5, 'Hài tình cảm Tết...', 200000, 5000, NOW(), NOW());

-- 6. Liên kết Movie - Genre
INSERT INTO movie_genre (movie_id, genre_id) VALUES 
(1, 2), (2, 1), (2, 3), (3, 2), (4, 1), (4, 3), (5, 2);

-- 7. Liên kết Movie - Tag
INSERT INTO movie_tag (movie_id, tag_id) VALUES 
(1, 1), (1, 4), (2, 1), (2, 3), (3, 1), (4, 1), (5, 1);

-- 8. Dữ liệu bảng Season (Mùa phim)
INSERT INTO season (id, title, thumbnail_url, season_number, movie_id, created_at, updated_at) VALUES 
(1, 'Phần 1', 'https://example.com/season1.jpg', 1, 1, NOW(), NOW()),
(2, 'Phần 1', 'https://example.com/season2.jpg', 1, 2, NOW(), NOW());

-- 9. Dữ liệu bảng Episode (Tập phim)
INSERT INTO episode (id, title, duration, episode_number, video_url, description, season_id, created_at, updated_at) VALUES 
(1, 'Tập 1', 120.0, 1, 'https://example.com/video1.mp4', 'Nội dung khởi đầu của Lật Mặt 7', 1, NOW(), NOW()),
(2, 'Full Movie', 148.0, 1, 'https://example.com/video2.mp4', 'Toàn bộ phim Người Nhện', 2, NOW(), NOW());

-- 10. Dữ liệu bảng Review (Đánh giá)
INSERT INTO review (id, rating, content, movie_id, user_id, created_at, updated_at) VALUES 
(1, 9.5, 'Phim cực kỳ cảm động!', 1, 1, NOW(), NOW()),
(2, 8.0, 'Kỹ xảo Spider-man đỉnh cao.', 2, 1, NOW(), NOW());

-- 11. Dữ liệu bảng Watch History (Lịch sử xem)
INSERT INTO watch_history (id, user_id, movie_id, watched_at, created_at, updated_at) VALUES 
(1, 1, 2, NOW(), NOW(), NOW());
