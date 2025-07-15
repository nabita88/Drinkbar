package com.example.cafeis.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface EntityMapper<D, E> {

    D toDto(E entity);


    default List<D> toDto(List<E> entityList) {
        if (entityList == null) return null;
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
