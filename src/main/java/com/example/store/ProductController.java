package com.example.store;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@RequiredArgsConstructor
@Controller
public class ProductController {
    private final ProductService productService;


    @GetMapping("/product")
    public String list(HttpServletRequest request) {
        List<ProductResponse.ListDTO> productList = productService.getProductList();
        request.setAttribute("productList", productList);
        return "product/list";
    }

    @GetMapping("/product/{id}")
    public String detail(@PathVariable Integer id, HttpServletRequest request) {
        ProductResponse.DetailDTO product = productService.getProductDetail(id);
        request.setAttribute("product", product);
        return "product/detail";
    }

    @GetMapping("/product/save-form")
    public String saveForm() {
        return "product/save-form";
    }

    @PostMapping("/product/save")
    public String save(ProductRequest.SaveDTO reqDTO) {
        productService.addProduct(reqDTO);
        return "redirect:/product";
    }

    @GetMapping("/product/{id}/update-form")
    public String updateForm(@PathVariable Integer id, HttpServletRequest request) {
        ProductResponse.DetailDTO product = productService.getProductDetail(id);
        request.setAttribute("product", product);
        return "product/update-form";
    }

    @PostMapping("/product/{id}/update")
    public String update() {
        return "redirect:/product/" + 1;
    }

    @PostMapping("/product/{id}/delete")
    public String delete() {
        return "redirect:/product";
    }
}
