# final project 1단계 - 상품 판매 사이트

## 1. MySQL 설정 방법
+ username: root / password: ex)1234
  
![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/19406ee0-f95c-4672-8ebb-44b05bce2de6)

+ 사용자 권한 부여 및 database 생성

![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/b49c795c-2078-4626-9d67-c61e7dbf69d1)

## 2. 프로젝트 생성
+ 프로젝트 생성 설정
  
![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/be011ba4-e67b-471d-a9b5-97ed4f8d1250)

+ 프로젝트 의존성 설정

![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/18efae17-2ea9-42ca-bbbe-e916fa9f8489)

## 3. 기본 환경 설정
+ _core - 이미지 상대경로를 위한 생성
+ 필요 자바 클래스 생성
  
![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/2a9feb9c-ae2e-4bdb-9691-9bcbb5701fa9)

+ application.yml 설정

![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/b8358c08-9513-45a1-937c-1281a58d93f9)

+ build.gradle dekpendencies 설정

![image](https://github.com/hyeonnnit/project-storeProduct/assets/153695703/3c21f3c7-a8fc-401f-b358-13b9369d234f)

## 4. Product 판매자 기능 구현 시작
+ Query : JPQL
+ 핵심 기능 로직: 판매 상품 목록보기, 상세보기, 등록, 수정, 삭제
+ 기능 구현 순서: ProductRepository -> ProductRequest -> ProductResponse -> ProductService -> ProductController -> mustache 수정
+ 데이터는 DTO로 변환해서 화면에 뿌리기위해 ProductResponse를 생성 
### 4-1. Product(entity)
```
package com.example.store;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@NoArgsConstructor
@Data
@Table(name = "product_tb")
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 20, nullable = false)
    private String name; //상품명

    @Column(nullable = false)
    private Integer price; //가격

    @Column(nullable = false)
    private Integer qty; //수량

    @Column
    private String pic;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Product(String pic, Integer id, String name, Integer price, Integer qty, LocalDateTime createdAt) {
        this.id = id;
        this.pic = pic;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.createdAt = createdAt;
    }
}
```
### 4-2. 상품 목록보기
+ ProductRepository findAll
```
@Repository
@RequiredArgsConstructor
public class ProductRepository {
    private final EntityManager em;

    public List<Product> findAll() {
        Query query = em.createQuery("SELECT p FROM Product p ORDER BY p.id desc", Product.class);
        return query.getResultList();
    }
}
```

+ ProductResponse ListDTO
```
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
```

+ ProductServise getProductList
```
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final EntityManager em;

    //상품 목록보기
    public List<ProductResponse.ListDTO> getProductList() {
        List<Product> productList = productRepository.findAll();

        //엔티티 받아온걸 dto로 변경
        return productList.stream().map(ProductResponse.ListDTO::new).collect(Collectors.toList());
    }


}
```

+ ProductController list
```
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

}
```

+ product/list.mustache
```
{{> layout/header}}

<div class="container">
    <table class="table table-hover offer-table scroll" style="text-align: center; border-top:2px solid #ddd">
        <br>
        <h2>상품 목록</h2>
        <br>

        <thead>
        <tr>
            <th class="center-align col-1">No</th>
            <th class="center-align col-5">상품명</th>
            <th class="center-align col-2">상품가격</th>
            <th class="center-align col-2">상품수량</th>
            <th class="center-align col-2">상세보기</th>
        </tr>
        </thead>
        {{#productList}}
            <tbody>
            <tr class="offer-table">
                <th scope="row">{{id}}</th>
                <td>{{name}}</td>
                <td>{{price}}</td>
                <td>{{qty}}</td>
                <td>
                    <div class="new-create-button">
                        <form action="/product/{{id}}">
                            <button type="submit" class="btn btn-outline-primary">상세보기</button>
                        </form>
                    </div>
                </td>
            </tr>
            </tbody>
        {{/productList}}

    </table>
</div>

<div style="margin-bottom: 25%"></div>
{{> layout/footer}}
```

### 4-3. 전체 코드
+ ProductRepository - 쿼리를 이용해 데이터를 변환시키거나 가져올 수 있게 해준다.
```
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

    public void deleteById(int id) {
        Query query =
                em.createQuery("delete from Product p where p.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
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
```

+ ProductRequest - 데이터를 새롭게 저장하거나 기존 데이터를 수정해서 저장할 때 데이터를 받아준다.
```
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
```

+ ProductResponse - 받아서 저장한 데이터를 화면에 전송하기 위해 사용한다.
```
public class ProductResponse {
    @Data
    public static class UpdateDTO{
        private int id;
        private String name;
        private int price ;
        private int qty;
        private String pic;

        public UpdateDTO(Product product){
            this.id = product.getId();
            this.name = product.getName();
            this.price = product.getPrice();
            this.qty = product.getQty();
            this.pic = product.getPic();
        }
    }

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
```

+ ProductService - 쿼리를 작성한 Repository를 가져와 DTO에 넣어서 변환시킨다.
```
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
    @Transactional
    public void deleteProduct(int id){
        productRepository.deleteById(id);
    }
}
```

+ ProductController - 작성한 코드들을 지정한 url과 view에 적용시키기 위해 Controller로 가져와서 전송한다.
```
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
    public String update(@PathVariable Integer id, ProductRequest.UpdateDTO reqDTO) {
        productService.updateProduct(id, reqDTO);
        return "redirect:/product/" + id;
    }

    @PostMapping("/product/{id}/delete")
    public String delete(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return "redirect:/product";
    }
}
```

## 5. View - mustache 사용
+ Controller에서 지정한 변수를 적용하여 화면에 전송시켜준다.

### 5-1. layout
+ layout/header.mustache
```
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Product</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
            rel="stylesheet" />
    <script
            src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script
            src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <link
            href="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.css"
            rel="stylesheet" />
    <script
            src="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.js"></script>
    <link
            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css"
            rel="stylesheet" />
    <link href="/css/style.css" rel="stylesheet" />
</head>
<body>
<nav class="navbar navbar-expand-sm bg-dark navbar-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="/product">Product</a>
        <button class="navbar-toggler" type="button"
                data-bs-toggle="collapse" data-bs-target="#collapsibleNavbar">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="collapsibleNavbar">
            <ul class="navbar-nav">
                <li class="nav-item"><a class="nav-link" href="/product/save-form">상품등록</a>
                </li>
                <li class="nav-item"><a class="nav-link" href="/product">상품목록</a>
                </li>

            </ul>
        </div>
    </div>
</nav>
```

+ layout/footer.mustache
```
<div class="ft-in pt-40 pb-40 ft-16 bg-dark navbar-dark">
    <div class="d-flex">
        <div class="ft-info-w" style="margin-top: 30px; color: darkgrey;">
            <ul class="ft-info d-flex" style="list-style-type: none;">
                <li class="mr-20" style="margin-right: 10px;">(주)스토어</li>
                <li class="mr-20" style="margin-right: 10px;">대표 : 한종희</li>
                <li class="mr-20" style="margin-right: 10px;">개인정보보호책임자 : 이준희</li>
                <li class="mr-20" style="margin-right: 10px;">사업자등록번호 : 124-81-00998</li>
                <li class="mr-20" style="margin-right: 10px;">직업정보제공사업 신고번호 : 02-2255-0114</li>
            </ul>
            <ul style="list-style-type: none;">
                <li style="margin-bottom: 10px">경기도 수원시 영통구 삼성로129(메탄동)</li>
                <li style="color:grey;">본 사이트는 상업적으로 사용하지 않습니다.</li>
            </ul>
        </div>
    </div>
</div>
```

### 5-2. product
+ product/detail.mustache
```
{{> layout/header}}
<div class="d-flex justify-content-center" style="margin-top: 100px; margin-bottom: 200px;">
    <div class="p-3 m-3" style="width: 300px;">
        <img src="/images/{{product.pic}}" width="300" height="300">
    </div>
    {{#product}}
        <div class="p-3 m-3" style="width: 300px;">
            <div class="mb-3 mt-3">
                상 품 명 : <input name="name" type="text" class="form-control" value="{{name}}">
            </div>
            <div class="mb-3 mt-3">
                상품가격 : <input name="price" type="number" class="form-control" value="{{price}}">
            </div>
            <div class="mb-3 mt-3">
                상품수량 : <input name="qty" type="number" class="form-control" value="{{qty}}">
            </div>
            <div class="d-flex justify-content-center">
                <span><a href="/product/{{id}}/update-form" class="btn btn-primary mt-3">수정하기</a></span>
                <span><form action="/product/{{id}}/delete" method="post">
                    <button class="btn btn-danger mt-3">삭제하기</button>
                </form>
                </span>
            </div>
        </div>


    {{/product}}

</div>
{{> layout/footer}}
```

+ product/list.mustache
```
{{> layout/header}}

<div class="container">
    <table class="table table-hover offer-table scroll" style="text-align: center; border-top:2px solid #ddd">
        <br>
        <h2>상품 목록</h2>
        <br>

        <thead>
        <tr>
            <th class="center-align col-1">No</th>
            <th class="center-align col-5">상품명</th>
            <th class="center-align col-2">상품가격</th>
            <th class="center-align col-2">상품수량</th>
            <th class="center-align col-2">상세보기</th>
        </tr>
        </thead>
        {{#productList}}
            <tbody>
            <tr class="offer-table">
                <th scope="row">{{id}}</th>
                <td>{{name}}</td>
                <td>{{price}}</td>
                <td>{{qty}}</td>
                <td>
                    <div class="new-create-button">
                        <form action="/product/{{id}}">
                            <button type="submit" class="btn btn-outline-primary">상세보기</button>
                        </form>
                    </div>
                </td>
            </tr>
            </tbody>
        {{/productList}}

    </table>
</div>

<div style="margin-bottom: 25%"></div>
{{> layout/footer}}
```

+ product/save-form.mustache
```
{{> layout/header}}
<div class="d-flex justify-content-center" style="margin-top: 100px; margin-bottom: 100px;">
    <form id="productForm" action="/product/save" method="post" enctype="multipart/form-data">
        <div class="row">
            <!-- 이미지 업로드 섹션 -->
            <div class="p-3 m-3" style="width: 300px;">
                <div class="row">
                    <div class="d-flex justify-content-center align-items-center"
                         style="width: 300px; height: 300px; border: 1px solid #E6E6E6; color: #E6E6E6;"
                         id="profilePreview">
                        <b>사진을 등록해 주세요.</b>
                    </div>
                    <input type="file" id="imageUpload" name="pic"
                           class="real-upload mx-auto d-block mt-2 w-50 h-50" accept="image/*" required
                           multiple>
                </div>
            </div>
            <script>
                $(document).ready(function () {
                    $('#imageUpload').change(function (event) {
                        if (this.files && this.files[0]) {
                            var reader = new FileReader();
                            reader.onload = function (e) {
                                $('#profilePreview').html('<img src="' + e.target.result + '" style="max-width: 100%;">');
                            };
                            reader.readAsDataURL(this.files[0]);
                            let fileName = this.files[0].name; // 'this'를 사용해서 파일 이름을 가져옵니다.
                            console.log(fileName);
                        }
                    });
                });
            </script>

            <!-- 상품 정보 입력 섹션 -->
            <div class="p-3 m-3" style="width: 300px;">
                <div class="mb-3 mt-3">
                    상 품 명 : <input name="name" type="text" class="form-control" placeholder="상품명을 입력하세요" required>
                </div>
                <div class="mb-3 mt-3">
                    상품가격 : <input name="price" type="number" class="form-control" placeholder="상품가격을 입력하세요" required>
                </div>
                <div class="mb-3 mt-3">
                    상품수량 : <input name="qty" type="number" class="form-control" placeholder="상품수량을 입력하세요" required>
                </div>
                <div class="d-flex justify-content-center">
                    <button type="submit" class="btn btn-primary mt-3">상품등록완료</button>
                </div>
            </div>

        </div>
    </form>
</div>

{{> layout/footer}}
```

+ product/update-form.mustache
```
{{> layout/header}} 
<div class="d-flex justify-content-center" style="margin-top: 100px; margin-bottom: 200px;">
    <form id="productForm" action="/product/{{id}}/update" method="post" enctype="multipart/form-data">
        <div class="row">

            <!-- 이미지 업로드 섹션 -->

            <div class="p-3 m-3" style="width: 300px;">
                <div class="row">
                    <div class="d-flex justify-content-center align-items-center"
                         style="width: 300px; height: 300px; border: 1px solid #E6E6E6; color: #E6E6E6;"
                         id="profilePreview">
                        <img src="/images/{{product.pic}}" width="300" height="300">
                    </div>
                    <input type="file" id="imageUpload" name="pic"
                           class="real-upload mx-auto d-block mt-2 w-50 h-50" accept="image/*" required
                           multiple>
                </div>
            </div>
            <script>
                $(document).ready(function () {
                    $('#imageUpload').change(function (event) {
                        if (this.files && this.files[0]) {
                            var reader = new FileReader();
                            reader.onload = function (e) {
                                $('#profilePreview').html('<img src="' + e.target.result + '" style="max-width: 100%;">');
                            };
                            reader.readAsDataURL(this.files[0]);
                            let fileName = this.files[0].name; // 'this'를 사용해서 파일 이름을 가져옵니다.
                            console.log(fileName);
                        }
                    });
                });
            </script>

            <!-- 상품 정보 입력 섹션 -->

            <div class="p-3 m-3" style="width: 300px;">
                <div class="mb-3 mt-3">
                    상 품 명 : <input name="name" type="text" class="form-control" value="{{product.name}}">
                </div>
                <div class="mb-3 mt-3">
                    상품가격 : <input name="price" type="number" class="form-control" value="{{product.price}}">
                </div>
                <div class="mb-3 mt-3">
                    상품수량 : <input name="qty" type="number" class="form-control" value="{{product.qty}}">
                </div>
                <div class="d-flex justify-content-center">
                    <button type="submit" class="btn btn-primary mt-3">상품수정완료</button>
                </div>
            </div>

        </div>
    </form>
</div>

{{> layout/footer}}
```
