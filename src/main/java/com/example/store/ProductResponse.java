package com.example.store;


import lombok.Data;

public class ProductResponse {
    @Data
    public static class ListDTO{
        private int id;
        private String name;
        private int price;
        private int qty;

        public ListDTO(Product product){
            this.id = product.getId();
            this.name = product.getName();
            this.price = product.getPrice();
            this.qty = product.getQty();
        }
    }
}
