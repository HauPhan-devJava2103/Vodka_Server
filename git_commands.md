# 🚀 Kế Hoạch Quản Lý Git & GitHub Hàng Ngày (Daily Action Plan)

Dưới đây là một "Plan" với cấu trúc dạng checklist (danh sách kiểm tra). Bạn có thể bám sát từng bước này để đảm bảo quy trình làm việc mỗi ngày không bao giờ bị lỗi hay mất code.

## 🌅 Lên Kế Hoạch Đầu Ngày (Phải làm trước khi code)
- [ ] **Bước 1: Đồng bộ code với Server**
  - **Lệnh:** `git pull origin <tên_nhánh_chính>`
  - **Mục đích:** Tải những đoạn code mới nhất mà team bạn vừa làm hôm qua về máy, để đảm bảo bạn luôn có bản code mới nhất trước khi bắt đầu.
  - **Ví dụ:** `git pull origin develop`

## 🌿 Kế Hoạch Bắt Đầu Work Task Mới
- [ ] **Bước 2: Tạo nhánh (Branch) riêng cho mình**
  - **Lệnh:** `git switch -c <tên_nhánh_mới>`
  - **Mục đích:** Tạo ra một "nhánh cây" mới từ thân cây chính để bạn tự do vọc vạch tính năng mới mà không sợ làm hỏng code chung.
  - **Ví dụ:** `git switch -c feature/chuc-nang-thanh-toan`

## 💻 Kế Hoạch Giao Dịch & Xử Lý Code
- [ ] **Bước 3: Code và kiểm kê hiện trường**
  - **Lệnh:** `git status`
  - **Mục đích:** Git sẽ báo cáo cho bạn biết file nào đã bị thay đổi, file nào mới tạo, và những ai đã sẵn sàng lên xe đi lưu trữ.

- [ ] **Bước 4: Gom các thay đổi vào phòng chờ**
  - **Lệnh:** `git add .` (Gom toàn bộ) hoặc `git add <tên_file>` (Gom cụ thể từng file)
  - **Mục đích:** Xác nhận những thay đổi này là chính xác và đẩy nó vào khu vực chờ.

- [ ] **Bước 5: Chốt sổ (Lưu Bookmark lịch sử)**
  - **Lệnh:** `git commit -m "Mô tả thật rõ ràng những gì bạn đã làm"`
  - **Mục đích:** Cất đi phần code hoàn chỉnh thành 1 mốc lịch sử vĩnh viễn ở máy (sẽ không bao giờ bị mất nữa).
  - **Ví dụ:** `git commit -m "Hoàn thành giao diện trang thanh toán lỗi"`

## 🚀 Kế Hoạch Nộp Bài (Kết thúc Task)
- [ ] **Bước 6: Gửi bản lưu lên Mạng (GitHub/GitLab)**
  - **Trường hợp 1 (Nhánh mới tạo, đẩy lần đầu):**
    - **Lệnh:** `git push -u origin <tên_nhánh_của_bạn>`
  - **Trường hợp 2 (Đã đẩy ít nhất 1 lần rồi):**
    - **Lệnh:** `git push`
  - **Mục đích:** Nộp bản local vĩnh viễn đó lên server tổng.

---

## 🚑 Sự Cố Ngoại Lệ (Emergency Contingency Plan)

> [!CAUTION]
> Dùng khi mọi thứ không đi theo kế hoạch chính xác phía trên!

- [ ] **Trường hợp khẩn 1: Gõ lộn xộn, muốn nhấn nút "Undo" xóa sạnh mọi thứ thay đổi vừa làm (Quay lại từ đầu):**
  - **Lệnh:** `git restore .`

- [ ] **Trường hợp khẩn 2: Đang code dở dang chưa xong để gộp vào Bước 5, nhưng sếp gọi bắt nhảy sang nhánh khác fix bug:**
  - **Bước A (Cất đồ vào rương):** `git stash`
  - *(Lúc này máy ảo sạch sẽ, bạn thoải mái dùng lệnh `git switch` sang nhánh theo yêu cầu sếp để làm việc khác...)*
  - **Bước B (Lấy đồ ra code tiếp sau khi giải quyết xong việc của sếp):** `git stash pop`
