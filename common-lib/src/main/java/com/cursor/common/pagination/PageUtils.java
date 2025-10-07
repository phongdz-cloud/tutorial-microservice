package com.cursor.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtils {

    private PageUtils() {}

    public static Pageable buildPageable(Integer page, Integer size, String sortBy, String direction) {
        int p = (page != null && page >= 0) ? page : 0;
        int s = (size != null && size > 0) ? size : 10;

        if (sortBy == null || sortBy.isBlank()) {
            return PageRequest.of(p - 1, s);
        }

        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(p, s, Sort.by(dir, sortBy));
    }
}
