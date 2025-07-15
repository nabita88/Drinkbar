package com.example.cafeis.Controller;

import com.example.cafeis.DTO.AddBasketItemDTO;
import com.example.cafeis.DTO.ApiResponseDTO;
import com.example.cafeis.DTO.BasketGoodsDTO;
import com.example.cafeis.DTO.BasketGoodsListDTO;
import com.example.cafeis.service.BasketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/basket")
@RequiredArgsConstructor
@Log4j2
@CrossOrigin
@Tag(name = "Basket", description = "장바구니 관리 API")
public class BasketController {

    private final BasketService basketService;

    @Operation(summary = "장바구니 항목 조회", description = "회원의 장바구니 항목들을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "장바구니를 찾을 수 없음")
    })
    @GetMapping("/items")
    public ResponseEntity<List<BasketGoodsListDTO>> retrieveBasketGoods(
            @RequestParam(value = "email", defaultValue = "user0@example.com") String email,
            @RequestParam(value = "branchNo", required = false) Long branchNo) {

        log.info("장바구니 조회 - 이메일: {}, 매장번호: {}", email, branchNo);

        List<BasketGoodsListDTO> items;

        if (branchNo != null) {
            items = basketService.fetchBasketGoodsByBranch(email, branchNo);
        } else {
            items = basketService.fetchBasketGoods(email);
        }

        return ResponseEntity.ok(items);
    }

    @Operation(summary = "장바구니 항목 수정", description = "장바구니의 상품 수량을 수정합니다. 수량이 0 이하면 삭제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @PostMapping("/change")
    public ResponseEntity<List<BasketGoodsListDTO>> modifyCartItem(
            @RequestParam(value = "email", defaultValue = "user0@example.com") String email,
            @Valid @RequestBody BasketGoodsDTO itemData) {

        log.info("장바구니 수정 - 아이템: {}, 이메일: {}", itemData, email);

        itemData.setEmail(email);

        List<BasketGoodsListDTO> result;
        if (itemData.getQuantity() <= 0) {
            result = basketService.removeCartItem(itemData.getBasketGoodsNo());
        } else {
            result = basketService.processCartItem(itemData);
        }

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "장바구니 항목 삭제", description = "장바구니에서 특정 상품을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "항목을 찾을 수 없음")
    })
    @DeleteMapping("/{basketNo}")
    public ResponseEntity<List<BasketGoodsListDTO>> deleteCartItem(
            @PathVariable("basketNo") Long itemId,
            @RequestParam(value = "email", defaultValue = "user0@example.com") String email) {

        log.info("장바구니 삭제 - 아이템 번호: {}, 이메일: {}", itemId, email);

        List<BasketGoodsListDTO> result = basketService.removeCartItem(itemId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "장바구니에 상품 추가", description = "매장의 장바구니에 상품을 추가합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품 또는 매장을 찾을 수 없음")
    })
    @PostMapping("/branch/{branchNo}/add")
    public ResponseEntity<ApiResponseDTO<String>> addItemToBasket(
            @PathVariable("branchNo") Long branchNo,
            @RequestParam(value = "email", defaultValue = "user0@example.com") String email,
            @Valid @RequestBody AddBasketItemDTO addBasketItemDTO) {



        basketService.addItemToBasket(email, branchNo, addBasketItemDTO);

        return ResponseEntity.ok(
                ApiResponseDTO.<String>builder()
                        .success(true)
                        .message("상품이 장바구니에 추가되었습니다.")
                        .data("SUCCESS")
                        .build()
        );
    }

    @Operation(summary = "장바구니 요약 정보 조회", description = "장바구니의 총 수량과 금액을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "장바구니를 찾을 수 없음")
    })
    @GetMapping("/summary")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getBasketSummary(
            @RequestParam(value = "email", defaultValue = "user0@example.com") String email) {

        log.info("장바구니 요약 조회 - 이메일: {}", email);

        List<BasketGoodsListDTO> items = basketService.fetchBasketGoods(email);

        int totalQuantity = items.stream()
                .mapToInt(BasketGoodsListDTO::getQuantity)
                .sum();

        int totalAmount = items.stream()
                .mapToInt(item -> item.getSellingPrice() * item.getQuantity())
                .sum();

        Map<String, Object> summary = Map.of(
                "totalQuantity", totalQuantity,
                "totalAmount", totalAmount,
                "itemCount", items.size()
        );

        return ResponseEntity.ok(
                ApiResponseDTO.<Map<String, Object>>builder()
                        .success(true)
                        .message("장바구니 요약 정보")
                        .data(summary)
                        .build()
        );
    }

    @Operation(summary = "장바구니 비우기", description = "회원의 장바구니를 모두 비웁니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비우기 성공"),
            @ApiResponse(responseCode = "404", description = "장바구니를 찾을 수 없음")
    })
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponseDTO<String>> clearBasket(
            @RequestParam(value = "email", defaultValue = "user0@example.com") String email) {

        log.info("장바구니 비우기 - 이메일: {}", email);

        List<BasketGoodsListDTO> items = basketService.fetchBasketGoods(email);
        for (BasketGoodsListDTO item : items) {
            basketService.removeCartItem(item.getBasketGoodsNo());
        }

        return ResponseEntity.ok(
                ApiResponseDTO.<String>builder()
                        .success(true)
                        .message("장바구니가 비워졌습니다.")
                        .data("SUCCESS")
                        .build()
        );
    }
}

