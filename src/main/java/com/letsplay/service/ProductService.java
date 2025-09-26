package com.letsplay.service;

import com.letsplay.model.Product;
import com.letsplay.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.security.PermitAll;
import static com.letsplay.security.SecurityUtils.getCurrentUserId;
import static com.letsplay.security.SecurityUtils.isAdmin;
import com.letsplay.dto.UpdateProductRequest;
import com.letsplay.dto.CreateProductRequest;
import com.letsplay.security.SecurityUtils;


// service is responsible for business logic and data manipulation. It chooses how to handle data and interacts with the repository layer.
// it doesn't handle HTTP requests directly, that's the controller's job.
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Product createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        // adding current logged-in user
        product.setUserId(SecurityUtils.getCurrentUserId());

        return productRepository.save(product);
    }


    @PermitAll
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN') or @productService.isOwner(#productId)")
    public Product getProductById(String productId) {
        return findProductOrThrow(productId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN') or @productService.isOwner(#productId)")
    public Product updateProduct(String productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        return productRepository.save(product);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN') or @productService.isOwner(#productId)")
    public void deleteProduct(String productId) {
        Product product = findProductOrThrow(productId);
        authorizeOwner(product);

        productRepository.deleteById(productId);
    }

    // Helper methods

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() == null) {
            throw new IllegalArgumentException("Price is required");
        }
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }

    private Product findProductOrThrow(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private void authorizeOwner(Product product) {
        String currentUserId = getCurrentUserId();
        if (!product.getUserId().equals(currentUserId) && !isAdmin()) {
            throw new RuntimeException("Not authorized to perform this action");
        }
    }

    public boolean isOwner(String productId) {
        Product product = findProductOrThrow(productId);
        return product.getUserId().equals(getCurrentUserId());
    }
}
