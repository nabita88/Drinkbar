package com.example.cafeis.mapper;


import com.example.cafeis.DTO.BasketDTO;
import com.example.cafeis.Domain.Basket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BasketMapper implements EntityMapper<BasketDTO, Basket> {

    private final BasketGoodsMapper basketGoodsMapper;


    @Override
    public BasketDTO toDto(Basket entity) {
        if (entity == null) return null;

        BasketDTO dto = BasketDTO.builder()
                .basketNo(entity.getBasketNo())
                .registrantEmail(entity.getRegistrant().getEmail())
                .registrantName(entity.getRegistrant().getNickname())
                .branchNo(entity.getBranch() != null ? entity.getBranch().getBranchNo() : null)
                .branchTitle(entity.getBranch() != null ? entity.getBranch().getBranchTitle() : null)
                .storeAddress(entity.getBranch() != null ? entity.getBranch().getLocationText() : null)
                .items(entity.getBasketGoodsPerson().stream()
                        .map(basketGoodsMapper::fromBasketGoods)
                        .collect(Collectors.toList()))
                .build();


        dto.calculateTotals();

        return dto;
    }
}
