package com.example.store;

import com.example.store._core.common.PicSaveUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
    private final EntityManager em;


    public Product updateById(int id, ProductRequest.UpdateDTO reqDTO) {
        Product product = em.find(Product.class, id);
        product.setName(reqDTO.getName());
        product.setPrice(reqDTO.getPrice());
        product.setQty(product.getQty());
        product.setPic(PicSaveUtil.save(reqDTO.getPic()));
        return product;
    }

    public void deleteById() {
    }

    public Product save(Product product) {
        em.persist(product);
        return product;
    }

    public Product findById(int id) {
        return em.find(Product.class, id);
    }

    public List<Product> findAll() {
        Query query = em.createQuery("SELECT p FROM Product p ORDER BY p.id desc", Product.class);
        return query.getResultList();
    }
}
