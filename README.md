# Vodka Server - Hệ quản trị Nội dung Phim Trực tuyến

## 1. Mô tả Dự án
**Vodka Server** là hệ thống máy chủ mạnh mẽ được xây dựng phục vụ cho trang web xem phim Vodka. Hệ thống cung cấp các RESTful API bảo mật và hiệu suất cao cho việc quản lý phim, streaming video, xác thực người dùng và điều phối các hoạt động của hệ thống. 

Máy chủ được tối ưu hóa để xử lý các tác vụ nặng như tải lên video/hình ảnh lên Cloudinary, quản lý cơ sở dữ liệu phim đồ sộ và đảm bảo an toàn thông tin người dùng thông qua cơ chế phân quyền (RBAC) nghiêm ngặt.

## 2. Tính năng chính
- **Xác thực và Bảo mật:** 
    - Đăng nhập/Đăng ký qua JWT (JSON Web Token).
    - Tích hợp Google OAuth2 cho phép đăng nhập nhanh chóng.
    - Quản lý phân quyền người dùng (ADMIN/USER).
    - Khôi phục mật khẩu thông qua mã OTP gửi qua Email.
- **Quản lý Phim (Movie Management):**
    - CRUD phim, mùa phim (season), và tập phim (episode).
    - Tích hợp Cloudinary API để lưu trữ và phân phối video/hình ảnh.
    - Hệ thống phân loại nâng cao theo Thể loại (Genre) và Nhãn (Tag).
- **Tương tác Người dùng:**
    - Hệ thống Đánh giá (Review) và Chấm điểm (Rating) phim.
    - Quản lý danh sách Yêu thích (Favorite) và Lịch sử xem (Watch History).
- **Quản trị hệ thống:**
    - Thống kê dữ liệu thực tế (Dashboard Analytics).
    - Nhật ký hoạt động (Activity Logging) để theo dõi các thay đổi quan trọng.
- **Hệ thống Mail:** Tự động gửi thông báo và mã xác thực cho người dùng.

## 3. Công nghệ sử dụng
- **Ngôn ngữ:** Java 17
- **Framework chính:** Spring Boot 3.5.11
- **Cơ sở dữ liệu:** MySQL 8.x
- **Truy xuất dữ liệu (ORM):** Spring Data JPA / Hibernate
- **Bảo mật:** Spring Security 6 & JJWT
- **Quản lý dependencies:** Maven
- **Dịch vụ bên thứ ba:**
    - **Cloudinary:** Lưu trữ hình ảnh và video.
    - **Google Cloud:** Xác thực Google OAuth2.
    - **Gmail SMTP:** Gửi email thông báo/OTP.
- **Tiện ích:** Lombok, Dotenv, Spring Validation.

## 4. Kiến trúc hệ thống
Dự án được tổ chức theo mô hình kiến trúc phân lớp chuẩn của Spring Boot:
- **Controller:** Tiếp nhận yêu cầu HTTP và điều phối xử lý.
- **Service:** Chứa logic nghiệp vụ (Business Logic) chi tiết.
- **Repository:** Giao tiếp trực tiếp với cơ sở dữ liệu MySQL thông qua JPA.
- **Entity:** Định nghĩa các mô hình dữ liệu trong cơ sở dữ liệu (Movie, User, Review, ...).
- **Security:** Cấu hình bộ lọc bảo mật, JWT và phân quyền truy cập API.

## 5. Hướng dẫn tạo cơ sở dữ liệu
1. Cài đặt và khởi chạy **MySQL Server**.
2. Tạo một database mới với tên: `Vodka_Server`.
   ```sql
   CREATE DATABASE Vodka_Server CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Sử dụng file script có sẵn để khởi tạo cấu trúc bảng và dữ liệu mẫu:
   - File script: `Vodka_Server\testSQL.sql`
   - Bạn có thể chạy script này bằng MySQL Workbench hoặc command line.

## 6. Cách cài đặt và chạy project
1. **Clone project:**
   ```bash
   git clone https://github.com/HauPhan-devJava2103/Vodka_Server.git
   ```
2. **Cấu hình biến môi trường:**
   - Tạo file `.env` tại thư mục gốc của project (Dựa trên template trong `application.properties`).
   - Cung cấp các keys cần thiết: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`.
3. **Cấu hình Database:**
   - Chỉnh sửa `src/main/resources/application.properties` để khớp với thông tin MySQL của bạn (username, password).
4. **Build và Chạy:**
   - Sử dụng Maven trong terminal:
     ```bash
     mvn clean install
     mvn spring-boot:run
     ```
   - Hoặc chạy file `VodkaServerApplication.java` trực tiếp từ IDE (IntelliJ IDEA được khuyến nghị).
5. **Truy cập API:** Server sẽ chạy mặc định tại `http://localhost:8081`.

## 7. Đóng góp
Chúng tôi trân trọng mọi đóng góp xây dựng hệ thống:
1. Fork dự án và tạo nhánh phát triển (`git checkout -b feature/NewFeature`).
2. Thực hiện thay đổi và commit (`git commit -m 'Add some feature'`).
3. Push lên nhánh của bạn (`git push origin feature/NewFeature`).
4. Mở một **Pull Request** để chúng tôi kiểm tra.

## 8. Credits / Author
- **Dự án được thực hiện bởi:** 
    - **Trương Hoài Chương**
    - **Phan Phúc Hậu**
    - **Nguyễn Hữu Văn**
- **Môn học:** Công nghệ phần mềm hướng đối tượng (OOSE).
- **Trường:** Đại học Công nghệ Kỹ thuật TP.HCM (HCMUTE).

---
*Dự án phục vụ mục đích nghiên cứu và giáo dục.*
