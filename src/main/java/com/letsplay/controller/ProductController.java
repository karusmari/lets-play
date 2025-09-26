package com.letsplay.controller;

import com.letsplay.model.Product;
import com.letsplay.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.letsplay.dto.ProductResponse;
import com.letsplay.service.UserService;
import com.letsplay.security.SecurityUtils;
import com.letsplay.dto.AdminProductResponse;
import com.letsplay.dto.UpdateProductRequest;
import com.letsplay.dto.CreateProductRequest;
import jakarta.validation.Valid;

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
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product saved = productService.createProduct(request);
        String sellerName = userService.findByIdOrThrow(saved.getUserId()).getName();
        return new ProductResponse(
                saved.getName(),
                saved.getDescription(),
                saved.getPrice(),
                sellerName
        );
    }

    // get all products (if admin, return productId also)
    @GetMapping
    public List<?> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(p -> {
                    String sellerName = userService.findByIdOrThrow(p.getUserId()).getName();
                    if (SecurityUtils.isAdmin()) {
                        return new AdminProductResponse(
                                p.getProductId(),
                                p.getName(),
                                p.getDescription(),
                                p.getPrice(),
                                sellerName
                        );
                    } else {
                        return new ProductResponse(
                                p.getName(),
                                p.getDescription(),
                                p.getPrice(),
                                sellerName
                        );
                    }
                })
                .toList();
    }

    // get a specific product by ID
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

    // get all products of the current logged-in user
    @GetMapping("/my-products")
    public List<AdminProductResponse> getMyProducts() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        return productService.getAllProducts().stream()
                .filter(p -> p.getUserId().equals(currentUserId))
                .map(p -> new AdminProductResponse(
                        p.getProductId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        userService.findByIdOrThrow(p.getUserId()).getName()
                ))
                .toList();
    }

    // renew a specific product by ID
    @PutMapping("/{productId}")
    public Object updateProduct(@PathVariable String productId,
                                @RequestBody UpdateProductRequest request) {
        Product updated = productService.updateProduct(productId, request);
        String sellerName = userService.findByIdOrThrow(updated.getUserId()).getName();

        if (SecurityUtils.isAdmin()) {
            return new AdminProductResponse(
                    updated.getUserId(),
                    updated.getName(),
                    updated.getDescription(),
                    updated.getPrice(),
                    sellerName
            );
        } else {
            return new ProductResponse(
                    updated.getName(),
                    updated.getDescription(),
                    updated.getPrice(),
                    sellerName
            );
        }
    }


    // delete a specific product by ID
    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
    }
}
