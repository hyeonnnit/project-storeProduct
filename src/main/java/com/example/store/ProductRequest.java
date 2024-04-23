package com.example.store;

import com.example.store._core.common.PicSaveUtil;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

public class ProductRequest {
    @Data
    public static class UpdateDTO {
        private MultipartFile pic;
        private String name;
        private Integer price;
        private Integer qty;
    }

    @Data
    public static class SaveDTO {
        private String name;
        private int price;
        private int qty;
        private MultipartFile pic;

        public Product toEntity() {
            String picPath = PicSaveUtil.save(pic);
            return Product.builder()
                    .pic(picPath)
                    .name(name)
                    .price(price)
                    .qty(qty)
                    .build();

        }
    }
}
