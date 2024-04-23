package com.example.store;


import lombok.Data;

public class ProductResponse {
    @Data
    public static class SaveDTO{
        private int id;
        private String name;
        private int price ;
        private int qty;
        private String pic;

        public SaveDTO(Product product){
            this.id = product.getId();
            this.name = product.getName();
            this.price = product.getPrice();
            this.qty = product.getQty();
            this.pic = product.getPic();
        }
    }

    @Data
    public static class DetailDTO{
        private int id;
        private String name;
        private int price ;
        private int qty;
        private String pic;

        public DetailDTO(Product product){
            this.id = product.getId();
            this.name = product.getName();
            this.price = product.getPrice();
            this.qty = product.getQty();
            this.pic = product.getPic();
        }
    }

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
