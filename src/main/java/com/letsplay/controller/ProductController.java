package com.letsplay.controller;

import com.letsplay.model.Product;
import com.letsplay.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // indicates that this class is a REST controller and handles HTTP requests
@RequestMapping("/products") // base URL for all endpoints in this controller
public class ProductController {

    private final ProductService productService;  // service that does the business logic

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE: add new product
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    // READ: get all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // READ: get a specific product by ID
    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable String productId) {
        return productService.getProductById(productId); //returning mongoDb id
    }

    // UPDATE: renew a specific product by ID
    @PutMapping("/{productId}")
    public Product updateProduct(@PathVariable String productId, @RequestBody Product product) {
        return productService.updateProduct(productId, product);
    }

    // DELETE: delete a specific product by ID
    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
    }
}
