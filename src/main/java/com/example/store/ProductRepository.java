package com.example.store;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
    private final EntityManager em;


    public void updateById() {
    }

    public void deleteById() {
    }

    public void save() {
    }

    public Product findById(int id) {
        return em.find(Product.class, id);
    }

    public List<Product> findAll() {
        Query query = em.createQuery("SELECT p FROM Product p ORDER BY p.id desc", Product.class);
        return query.getResultList();
    }
}
