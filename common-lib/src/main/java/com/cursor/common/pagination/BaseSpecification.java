package com.cursor.common.pagination;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

public class BaseSpecification {
    private BaseSpecification() {}

    public static <T> Specification<T> hasKeyword(String keyword, String... fields) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            Predicate[] predicates = Arrays.stream(fields)
                    .map(f -> cb.like(cb.lower(root.get(f)), "%" + keyword.toLowerCase() + "%"))
                    .toArray(Predicate[]::new);
            return cb.or(predicates);
        };
    }

    public static <T> Specification<T> equalsField(String field, Object value) {
        return (root, query, cb) -> {
            if (value == null) return null;
            return cb.equal(root.get(field), value);
        };
    }

}
