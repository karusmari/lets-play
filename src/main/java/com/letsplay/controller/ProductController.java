package com.letsplay.controller;

import com.letsplay.model.Product;
import com.letsplay.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.letsplay.dto.ProductResponse;
import com.letsplay.service.UserService;


@RestController // indicates that this class is a REST controller and handles HTTP requests
@RequestMapping("/products") // base URL for all endpoints in this controller
public class ProductController {

    private final ProductService productService;
    private final UserService userService;// service that does the business logic

    public ProductController(ProductService productService, UserService userService) {
        this.userService = userService;
        this.productService = productService;
    }

    // add new product
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(p -> {
                    String sellerName = userService.findByIdOrThrow(p.getUserId()).getName();
                    return new ProductResponse(
                            p.getName(),
                            p.getDescription(),
                            p.getPrice(),
                            sellerName
                    );
                })
                .toList();
    }

    @GetMapping("/{productId}")
    public ProductResponse getProductById(@PathVariable String productId) {
        Product p = productService.getProductById(productId);
        String sellerName = userService.findByIdOrThrow(p.getUserId()).getName();
        return new ProductResponse(
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                sellerName);
    }


    // renew a specific product by ID
    @PutMapping("/{productId}")
    public Product updateProduct(@PathVariable String productId, @RequestBody Product product) {
        return productService.updateProduct(productId, product);
    }

    // delete a specific product by ID
    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
    }
}
