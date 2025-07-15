package com.example.cafeis.mapper;


import com.example.cafeis.DTO.ProductDTO;
import com.example.cafeis.Domain.Product;
import com.example.cafeis.Domain.ProductVisualContent;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper implements EntityMapper<ProductDTO, Product> {


    @Override
    public ProductDTO toDto(Product entity) {
        if (entity == null) return null;

        ProductDTO dto = ProductDTO.builder()
                .productNo(entity.getProductNo())
                .productName(entity.getProductName())
                .sellingPrice(entity.getSellingPrice())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .servableCount(entity.getServableCount())
                .isDeleted(entity.isDeleted())
                .branchNo(entity.getBranch() != null ? entity.getBranch().getBranchNo() : null)
                .build();

        if (entity.getOptions() != null) {
            ProductDTO.ProductOptionDTO optionDTO = ProductDTO.ProductOptionDTO.builder()
                    .sizes(extractAvailableSizes(entity))
                    .temperatures(extractAvailableTemperatures(entity))
                    .build();
            dto.setOptions(optionDTO);
        }

        if (entity.getGallery() != null && !entity.getGallery().isEmpty()) {
            List<String> fileNames = entity.getGallery().stream()
                    .map(ProductVisualContent::getImageFileFullName)
                    .collect(Collectors.toList());
            dto.setUploaded_visual_identifiers(fileNames);
            dto.setGallery(entity.getGallery());
        }

        return dto;
    }

    private Set<String> extractAvailableSizes(Product entity) {
        Set<String> sizes = new HashSet<>();
        if (entity.getOptions() != null) {
            if (Boolean.TRUE.equals(entity.getOptions().getDimensionLarge())) {
                sizes.add("LARGE");
            }
            if (Boolean.TRUE.equals(entity.getOptions().getDimensionMega())) {
                sizes.add("MEGA");
            }
        }
        return sizes;
    }

    private Set<String> extractAvailableTemperatures(Product entity) {
        Set<String> temperatures = new HashSet<>();
        if (entity.getOptions() != null) {
            if (Boolean.TRUE.equals(entity.getOptions().getThermalCold())) {
                temperatures.add("COLD");
            }
            if (Boolean.TRUE.equals(entity.getOptions().getThermalHot())) {
                temperatures.add("HOT");
            }
        }
        return temperatures;
    }
}

