package com.vn.vodka_server.util;

import com.vn.vodka_server.dto.response.PaginationMeta;
import org.springframework.data.domain.Page;

public class PaginationUtils {
    public static PaginationMeta buildPaginationMeta(Page<?> page, int currentPage) {
        return PaginationMeta.builder()
                .currentPage(currentPage)
                .pageSize(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}