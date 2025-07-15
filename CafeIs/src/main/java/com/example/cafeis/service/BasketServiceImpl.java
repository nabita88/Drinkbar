package com.example.cafeis.service;


import com.example.cafeis.DTO.AddBasketItemDTO;
import com.example.cafeis.DTO.BasketGoodsDTO;
import com.example.cafeis.DTO.BasketGoodsListDTO;
import com.example.cafeis.Domain.*;
import com.example.cafeis.mapper.BasketGoodsMapper;
import com.example.cafeis.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BasketServiceImpl implements BasketService {

    private final BasketRepository basketRepository;
    private final BasketGoodsRepository basketGoodsRepository;
    private final MemberRepository registrantRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final BasketGoodsMapper basketGoodsMapper;


    @Override
    @Transactional(readOnly = true)
    public List<BasketGoodsListDTO> fetchBasketGoods(String registrantEmail) {

        return basketRepository.findByRegistrantEmail(registrantEmail)
                .map(this::convertBasketToListDTO)
                .orElseGet(() -> {
                    return List.of();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<BasketGoodsListDTO> fetchBasketGoodsByBranch(String registrantEmail, Long branchNo) {

        return basketRepository.findByRegistrantEmailAndBranchNo(registrantEmail, branchNo)
                .map(this::convertBasketToListDTO)
                .orElseGet(() -> {
                    return List.of();
                });
    }

    @Override
    public List<BasketGoodsListDTO> processCartItem(BasketGoodsDTO itemData) {

        return basketGoodsRepository.findById(itemData.getBasketGoodsNo())
                .map(basketGoods -> {
                    basketGoods.changeQuantity(itemData.getQuantity());
                    basketGoodsRepository.save(basketGoods);
                    return fetchBasketGoods(itemData.getEmail());
                })
                .orElseThrow(() -> new RuntimeException("장바구니 상품을 찾을 수 없습니다: " + itemData.getBasketGoodsNo()));
    }

    @Override
    public List<BasketGoodsListDTO> removeCartItem(Long basketGoodsNo) {

        return basketGoodsRepository.findById(basketGoodsNo)
                .map(basketGoods -> {
                    String registrantEmail = basketGoods.getBasket().getRegistrant().getEmail();
                    basketGoodsRepository.delete(basketGoods);
                    return fetchBasketGoods(registrantEmail);
                })
                .orElseThrow(() -> new RuntimeException("장바구니 상품을 찾을 수 없습니다: " + basketGoodsNo));
    }


    @Override
    public void addItemToBasket(String registrantEmail, Long branchNo, AddBasketItemDTO addBasketItemDTO) {

        Registrant registrant = registrantRepository.findById(registrantEmail)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + registrantEmail));

        Branch branch = branchRepository.findById(branchNo)
                .orElseThrow(() -> new RuntimeException("매장을 찾을 수 없습니다: " + branchNo));

        Product product = productRepository.findById(addBasketItemDTO.getProductNo())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + addBasketItemDTO.getProductNo()));

        Basket basket = basketRepository.findByRegistrantEmailAndBranchNo(registrantEmail, branchNo)
                .orElseGet(() -> {
                    Basket newBasket = Basket.builder()
                            .registrant(registrant)
                            .branch(branch)
                            .build();
                    return basketRepository.save(newBasket);
                });

        // 기존 상품이 있는지 확인 (동일한 옵션인지도 체크해야 함)
        Optional<BasketGoods> existingItem = basket.getBasketGoodsPerson().stream()
                .filter(item -> item.getProduct().getProductNo().equals(addBasketItemDTO.getProductNo())
                        && isSameOptions(item, addBasketItemDTO))
                .findFirst();

        if (existingItem.isPresent()) {
            // 동일한 상품과 옵션이면 수량만 증가
            BasketGoods basketGoods = existingItem.get();
            basketGoods.changeQuantity(basketGoods.getQuantity() + addBasketItemDTO.getQuantity());
        } else {
            // 새로운 상품 또는 다른 옵션으로 추가
            BasketGoods basketGoods = BasketGoods.builder()
                    .basket(basket)
                    .product(product)
                    .quantity(addBasketItemDTO.getQuantity())
                    .size(addBasketItemDTO.getSize())
                    .temperature(addBasketItemDTO.getTemperature())
                    .additionalOptions(addBasketItemDTO.getAdditionalOptions())
                    .additionalPrice(addBasketItemDTO.getAdditionalPrice() != null ? addBasketItemDTO.getAdditionalPrice() : 0)
                    .build();
            basket.getBasketGoodsPerson().add(basketGoods);
        }

        basketRepository.save(basket);
    }

    private boolean isSameOptions(BasketGoods item, AddBasketItemDTO dto) {
        return Objects.equals(item.getSize(), dto.getSize())
                && Objects.equals(item.getTemperature(), dto.getTemperature())
                && Objects.equals(item.getAdditionalOptions(), dto.getAdditionalOptions())
                && Objects.equals(item.getAdditionalPrice(), dto.getAdditionalPrice());
    }

    private List<BasketGoodsListDTO> convertBasketToListDTO(Basket basket) {
        return basket.getBasketGoodsPerson().stream()
                .map(basketGoodsMapper::fromBasketGoods)
                .collect(Collectors.toList());
    }

}