package com.example.cafeis.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor


public class ProductVisualContent {

    @Column(name = "image_file_full_name")
    private String imageFileFullName;


}