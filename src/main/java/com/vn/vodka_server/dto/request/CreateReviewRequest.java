package com.vn.vodka_server.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

// DTO nhận dữ liệu tạo review hoặc reply từ FE
@Data
@Builder
public class CreateReviewRequest {

    // ID phim cần đánh giá (bắt buộc)
    @NotNull(message = "movieId không được để trống")
    private Long movieId;

    // Nội dung bình luận (bắt buộc)
    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    // Điểm đánh giá (1-10). Chỉ áp dụng khi tạo review gốc (replyToId == null)
    @Min(value = 1, message = "Rating tối thiểu là 1")
    @Max(value = 10, message = "Rating tối đa là 10")
    private Integer rating;

    // ID của review cha nếu đây là reply. Null = review gốc, có giá trị = reply
    private Long replyToId;
}
