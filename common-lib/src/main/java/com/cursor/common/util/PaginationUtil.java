package com.cursor.common.util;

import com.cursor.common.dto.PageResponse;
import org.springframework.data.domain.Page;

public final class PaginationUtil {

    private PaginationUtil() {
    }

    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}


