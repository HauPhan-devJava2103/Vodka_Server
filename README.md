<div align="center">

# 🎬 Vodka Server
**Hệ quản trị Nội dung Phim Trực tuyến (RESTful API)**

[![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)](https://jwt.io/)
[![Cloudinary](https://img.shields.io/badge/Cloudinary-3448C5?style=for-the-badge&logo=Cloudinary&logoColor=white)](https://cloudinary.com/)

> Hệ thống máy chủ mạnh mẽ cung cấp API toàn diện cho nền tảng xem phim Vodka, tích hợp bảo mật JWT và lưu trữ đám mây.

</div>

---

## 🌟 1. Tính năng chính
**Vodka Server** cung cấp các RESTful API bảo mật và hiệu suất cao cho việc quản lý và vận hành hệ thống phim:

### 🔐 Xác thực và Bảo mật
* 🔑 **JWT Authentication:** Hệ thống đăng nhập/đăng ký bảo mật với JSON Web Token.
* 🌐 **Google OAuth2:** Hỗ trợ đăng nhập nhanh thông qua tài khoản Google.
* 🛡️ **Phân quyền (RBAC):** Quản lý quyền hạn chi tiết giữa ADMIN và USER.
* 📧 **Mã OTP:** Khôi phục mật khẩu bảo mật qua hệ thống Gmail SMTP.

### 🎬 Quản lý Phim & Nội dung
* 📄 **Cấu trúc linh hoạt:** Quản lý Phim, Mùa phim (Season) và Tập phim (Episode).
* 📁 **Phân loại nâng cao:** Hệ thống Thể loại (Genre) và Nhãn (Tag) đa dạng.
* ☁️ **Media Cloud:** Tích hợp Cloudinary API để lưu trữ, quản lý video và hình ảnh poster/banner.
* ❤️ **Tương tác:** API cho danh sách yêu thích, lịch sử xem và đánh giá/bình luận phim.

### 📊 Quản trị & Thống kê
* 📈 **Analytics:** Cung cấp dữ liệu thống kê lượt xem, người dùng và đánh giá phim.

---

## ⚙️ 2. Công nghệ sử dụng
* **Ngôn ngữ:** Java 17
* **Framework:** Spring Boot 3.5.11
* **ORM:** Spring Data JPA / Hibernate
* **Database:** MySQL 8.x
* **Security:** Spring Security 6 & JJWT 0.12.6
* **Build tool:** Maven
* **Services:** Cloudinary, Google OAuth2, Gmail SMTP

---

## 🏛️ 3. Kiến trúc hệ thống
Dự án được xây dựng theo mô hình **MVC + Service + Repository**:
* **Controller:** Xử lý REST Endpoints.
* **Service:** Chứa Logic nghiệp vụ.
* **Repository:** Giao tiếp MySQL qua JPA.
* **Entity:** Định nghĩa Database Schema (JPA Entities).

---

## 🚀 4. Cách cài đặt và chạy Project
Theo dõi các bước sau để khởi chạy **Vodka Server** trên máy của bạn:

### 📋 Yêu cầu hệ thống
* Đảm bảo máy đã cài đặt [JDK 17+](https://www.oracle.com/java/technologies/downloads/) và [MySQL Server](https://dev.mysql.com/downloads/installer/).

### 💻 Các bước thực hiện

**Bước 1: Clone project về máy**
```bash
git clone https://github.com/HauPhan-devJava2103/Vodka_Server.git
```

**Bước 2: Cấu hình Cơ sở dữ liệu**
1. Tạo database: `CREATE DATABASE Vodka_Server;`
2. Chạy file script khởi tạo: `Vodka_Server\testSQL.sql`

**Bước 3: Cấu hình Biến môi trường (.env)**
Tạo file `.env` tại thư mục gốc và cung cấp các thông tin:
```properties
CLOUDINARY_CLOUD_NAME=your_name
CLOUDINARY_API_KEY=your_key
CLOUDINARY_API_SECRET=your_secret
ADMIN_EMAIL=your_admin_email
ADMIN_PASSWORD=your_admin_password
```

**Bước 4: Chạy ứng dụng**
```bash
mvn clean install
mvn spring-boot:run
```

---

## 🤝 5. Đóng góp (Contributing)
1. 🍴 **Fork** dự án.
2. 🌿 Nhánh tính năng: `git checkout -b feature/NewAPI`
3. 💾 **Commit**: `git commit -m 'Add New API'`
4. 🚀 **Push**: `git push origin feature/NewAPI`
5. 🔄 Mở **Pull Request**.

---

## 👤 6. Credits / Author
* **Nhóm thực hiện:**
    * **Trương Hoài Chương**
    * **Phan Phúc Hậu**
    * **Lê Hữu Văn**
* **Môn học:** Công nghệ phần mềm hướng đối tượng (OOSE)
* **Trường:** Đại học Công nghệ Kỹ thuật TP.HCM (HCMUTE)

---
<div align="center">
<i>Dự án này là mã nguồn mở và được tạo ra với mục đích học tập.</i>
</div>
