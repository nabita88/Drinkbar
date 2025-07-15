package com.example.cafeis.service;


import com.example.cafeis.DTO.PageRequestDTO;
import com.example.cafeis.DTO.PageResponseDTO;
import com.example.cafeis.DTO.ProductDTO;
import com.example.cafeis.Domain.Product;
import com.example.cafeis.Domain.ProductVisualContent;
import com.example.cafeis.Enum.ProductGroupType;
import com.example.cafeis.exception.BusinessException;
import com.example.cafeis.mapper.ProductMapper;
import com.example.cafeis.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public PageResponseDTO<ProductDTO> fetchProductList(PageRequestDTO pageRequestDTO) {

        Pageable pageConfiguration = Optional.ofNullable(pageRequestDTO)
                .map(this::buildPageSpecification)
                .orElseThrow(() -> new BusinessException("INVALID_REQUEST", "Page configuration is required"));

        Page<Object[]> queryResults = productRepository.findAllWithImages(pageConfiguration);
        return assembleCatalogResponse(queryResults, pageRequestDTO);
    }

    private Pageable buildPageSpecification(PageRequestDTO request) {
        return PageRequest.of(
                Math.max(0, request.getPage() - 1),
                request.getSize(),
                Sort.by("productNo").descending()
        );
    }

    private PageResponseDTO<ProductDTO> assembleCatalogResponse(Page<Object[]> queryResults, PageRequestDTO requestInfo) {
        List<ProductDTO> merchandiseCollection = queryResults.stream()
                .map(this::assembleMerchandiseRepresentation)
                .collect(Collectors.toList());

        return PageResponseDTO.<ProductDTO>withAll()
                .dtoList(merchandiseCollection)
                .totalCount(queryResults.getTotalElements())
                .pageRequestDTO(requestInfo)
                .build();
    }

    private ProductDTO assembleMerchandiseRepresentation(Object[] queryResult) {
        return Optional.ofNullable(queryResult)
                .filter(arr -> arr.length >= 2)
                .map(this::extractMerchandiseComponents)
                .map(this::enrichWithVisualAssets)
                .orElseThrow(() -> new BusinessException("데이타 트랜스폼 데이타 에러:", "상품 정보를 생성할 수 없습니다"));
    }

    private MerchandiseComponents extractMerchandiseComponents(Object[] dataArray) {
        Product merchandiseEntity = (Product) dataArray[0];
        ProductVisualContent visualAsset = (ProductVisualContent) dataArray[1];
        return new MerchandiseComponents(merchandiseEntity, visualAsset);
    }

    private ProductDTO enrichWithVisualAssets(MerchandiseComponents components) {
        ProductDTO representation = productMapper.toDto(components.merchandise());

        Optional.ofNullable(components.visualAsset())
                .map(ProductVisualContent::getImageFileFullName)
                .map(List::of)
                .ifPresent(representation::setUploaded_visual_identifiers);

        return representation;
    }

    private record MerchandiseComponents(Product merchandise, ProductVisualContent visualAsset) {}






    @Override
    public ProductDTO fetchProduct(Long productId) {

        // log.info("상품 상세 조회 - 식별자: {}", productId);

        return productRepository.selectOne(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND",
                        "상품을 찾을 수 없습니다. ID: " + productId));
    }





    @Override
    public List<ProductDTO> getProductsByGroupType(ProductGroupType groupType) {
        // log.info("카테고리별 상품 조회: {}", groupType);

        List<Object[]> results = productRepository.findByCategory(groupType);

        return results.stream()
                .map(this::assembleMerchandiseRepresentation)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByGroupTypeAndBranch(ProductGroupType groupType, Long branchNo) {
        // log.info("매장 및 카테고리별 상품 조회: 매장번호={}, 카테고리={}", branchNo, groupType);

        List<Object[]> results = productRepository.findByCategoryAndBranch(groupType, branchNo);

        return results.stream()
                .map(this::assembleMerchandiseRepresentation)
                .collect(Collectors.toList());
    }

}
