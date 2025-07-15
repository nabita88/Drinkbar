package com.example.cafeis.Controller;

import com.example.cafeis.DTO.ApiResponseDTO;
import com.example.cafeis.DTO.PageRequestDTO;
import com.example.cafeis.DTO.PageResponseDTO;
import com.example.cafeis.DTO.ProductDTO;
import com.example.cafeis.Enum.ProductGroupType;
import com.example.cafeis.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/products")
@CrossOrigin
@Builder
@Tag(name = "Product", description = "상품 관련 API")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "전체 또는 카테고리별 상품 목록을 조회합니다")
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<ProductDTO>> getProductList(
            @ModelAttribute PageRequestDTO pageRequestDTO,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long branchNo) {

        log.info("상품 목록 조회 - 페이지: {}, 사이즈: {}, 카테고리: {}, 매장번호: {}",
                pageRequestDTO.getPage(), pageRequestDTO.getSize(), category, branchNo);

        PageResponseDTO<ProductDTO> response;

        if (category != null && !category.isEmpty()) {
            try {
                ProductGroupType groupType = ProductGroupType.valueOf(category.toUpperCase());
                List<ProductDTO> products;

                if (branchNo != null) {
                    products = productService.getProductsByGroupTypeAndBranch(groupType, branchNo);
                } else {
                    products = productService.getProductsByGroupType(groupType);
                }

                response = PageResponseDTO.<ProductDTO>withAll()
                        .dtoList(products)
                        .totalCount((long) products.size())
                        .pageRequestDTO(pageRequestDTO)
                        .build();
            } catch (IllegalArgumentException e) {
                response = PageResponseDTO.<ProductDTO>withAll()
                        .dtoList(List.of())
                        .totalCount(0L)
                        .pageRequestDTO(pageRequestDTO)
                        .build();
            }
        } else {
            response = productService.fetchProductList(pageRequestDTO);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 카테고리 목록 조회", description = "사용 가능한 모든 카테고리 목록을 조회합니다")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponseDTO<List<Map<String, String>>>> getAllCategories() {
        log.info("전체 카테고리 목록 조회");

        List<Map<String, String>> categories = Arrays.stream(ProductGroupType.values())
                .map(type -> Map.of(
                        "code", type.name(),
                        "name", type.getDescription()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponseDTO.<List<Map<String, String>>>builder()
                        .success(true)
                        .message("카테고리 목록 조회 성공")
                        .data(categories)
                        .build()
        );
    }

    @Operation(summary = "상품 상세 조회", description = "상품 번호로 개별 상품의 상세 정보를 조회합니다")
    @GetMapping("/{productNo}")
    public ResponseEntity<ProductDTO> getProductDetail(@PathVariable Long productNo) {
        log.info("상품 상세 조회 - 상품번호: {}", productNo);

        ProductDTO product = productService.fetchProduct(productNo);
        return ResponseEntity.ok(product);
    }
}
