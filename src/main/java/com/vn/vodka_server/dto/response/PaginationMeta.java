package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationMeta {
    private long totalItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
