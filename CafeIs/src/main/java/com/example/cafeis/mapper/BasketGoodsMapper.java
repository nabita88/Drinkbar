package com.example.cafeis.mapper;

import com.example.cafeis.DTO.BasketGoodsListDTO;
import com.example.cafeis.Domain.BasketGoods;
import org.springframework.stereotype.Component;


@Component
public class BasketGoodsMapper {


    public BasketGoodsListDTO fromBasketGoods(BasketGoods entity) {
        if (entity == null) return null;

        BasketGoodsListDTO dto = BasketGoodsListDTO.builder()
                .basketGoodsNo(entity.getBasketGoodsNo())
                .basketNo(entity.getBasket().getBasketNo())
                .email(entity.getBasket().getRegistrant().getEmail())
                .productNo(entity.getProduct().getProductNo())
                .productName(entity.getProduct().getProductName())
                .sellingPrice(entity.getProduct().getSellingPrice())
                .productDescription(entity.getProduct().getDescription())
                .productGroupType(entity.getProduct().getCategory())
                .quantity(entity.getQuantity())
                .size(entity.getSize())
                .temperature(entity.getTemperature())
                .additionalOptions(entity.getAdditionalOptions())
                .additionalPrice(entity.getAdditionalPrice())
                .servableCount(entity.getProduct().getServableCount())
                .isDiscontinued(entity.getProduct().isDeleted())
                .createdAt(entity.getCreatedAt())
                .build();


        if (entity.getBasket().getBranch() != null) {
            dto.setBranchNo(entity.getBasket().getBranch().getBranchNo());
            dto.setBranchTitle(entity.getBasket().getBranch().getBranchTitle());
        }


        if (entity.getProduct().getGallery() != null &&
                !entity.getProduct().getGallery().isEmpty()) {
            dto.setProductImage(entity.getProduct().getGallery().get(0).getImageFileFullName());
        }

        return dto;
    }
}
