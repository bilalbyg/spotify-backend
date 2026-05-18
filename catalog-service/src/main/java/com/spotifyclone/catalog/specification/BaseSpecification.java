package com.spotifyclone.catalog.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class BaseSpecification {

    public static <T> Specification<T> attributeEquals(String attribute, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.equal(root.get(attribute), value);
        };
    }

    public static <T> Specification<T> attributeContains(String attribute, String value) {
        return (root, query, cb) -> {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get(attribute)), "%" + value.toLowerCase() + "%");
        };
    }

    public static <T> Specification<T> attributeIn(String attribute, Collection<?> values) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return null;
            }
            return root.get(attribute).in(values);
        };
    }

    public static <T> Specification<T> isTrue(String attribute) {
        return (root, query, cb) -> cb.isTrue(root.get(attribute));
    }

    public static <T> Specification<T> isFalse(String attribute) {
        return (root, query, cb) -> cb.isFalse(root.get(attribute));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThan(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.greaterThan(root.get(attribute), value);
        };
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThan(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.lessThan(root.get(attribute), value);
        };
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> between(String attribute, Y min, Y max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            } else if (min == null) {
                return cb.lessThanOrEqualTo(root.get(attribute), max);
            } else if (max == null) {
                return cb.greaterThanOrEqualTo(root.get(attribute), min);
            } else {
                return cb.between(root.get(attribute), min, max);
            }
        };
    }
}
