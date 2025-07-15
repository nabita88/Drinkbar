package com.example.cafeis.service;

import com.example.cafeis.DTO.PageRequestDTO;
import com.example.cafeis.DTO.PageResponseDTO;
import com.example.cafeis.DTO.ProductDTO;
import com.example.cafeis.Enum.ProductGroupType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ProductService {

    PageResponseDTO<ProductDTO> fetchProductList(PageRequestDTO pageRequestDTO);


    ProductDTO fetchProduct(Long productNo);

    List<ProductDTO> getProductsByGroupType(ProductGroupType groupType);

    List<ProductDTO> getProductsByGroupTypeAndBranch(ProductGroupType groupType, Long branchNo);

}