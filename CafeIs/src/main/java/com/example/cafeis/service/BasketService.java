package com.example.cafeis.service;

import com.example.cafeis.DTO.AddBasketItemDTO;
import com.example.cafeis.DTO.BasketGoodsDTO;
import com.example.cafeis.DTO.BasketGoodsListDTO;

import java.util.List;

public interface BasketService {


    void addItemToBasket(String email, Long branchNo, AddBasketItemDTO addBasketItemDTO);

    List<BasketGoodsListDTO> processCartItem(BasketGoodsDTO itemData);

    List<BasketGoodsListDTO> removeCartItem(Long basketNo);

    List<BasketGoodsListDTO> fetchBasketGoods(String email);

    List<BasketGoodsListDTO> fetchBasketGoodsByBranch(String email, Long branchNo);
}

