package com.example.store;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final EntityManager em;

    //상품 상세 보기

    //상품 목록보기
    public List<ProductResponse.ListDTO> getProductList() {
        List<Product> productList = productRepository.findAll();

        //엔티티 받아온걸 dto로 변경
        return productList.stream().map(ProductResponse.ListDTO::new).collect(Collectors.toList());
    }

    //상품 등록

    //상품 업데이트

    //상품 삭제
}
