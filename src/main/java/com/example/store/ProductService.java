package com.example.store;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
    public ProductResponse.DetailDTO getProductDetail(int id){
        Product product = productRepository.findById(id);
        return new ProductResponse.DetailDTO(product);
    }

    //상품 목록보기
    public List<ProductResponse.ListDTO> getProductList() {
        List<Product> productList = productRepository.findAll();

        //엔티티 받아온걸 dto로 변경
        return productList.stream().map(ProductResponse.ListDTO::new).collect(Collectors.toList());
    }

    //상품 등록
    @Transactional
    public ProductResponse.SaveDTO addProduct(ProductRequest.SaveDTO reqDTO) {
        Product product = productRepository.save(reqDTO.toEntity());
        return new ProductResponse.SaveDTO(product);
    }

    //상품 업데이트
    @Transactional
    public ProductResponse.UpdateDTO updateProduct(int id, ProductRequest.UpdateDTO reqDTO){
        Product product = productRepository.updateById(id, reqDTO);
        return new ProductResponse.UpdateDTO(product);
    }

    //상품 삭제
}
