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



// service is responsible for business logic and data manipulation. It chooses how to handle data and interacts with the repository layer.
// it doesn't handle HTTP requests directly, that's the controller's job.
@Service
public class ProductService {
    private ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Product createProduct(Product product) {

        String userId = getCurrentUserId(); //checking the current user
        product.setUserId(userId); // setting the userId

        if (product.getName() == null || product.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (product.getPrice() == null) {
            throw new IllegalArgumentException("Price is required");
        }

        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        return productRepository.save(product);
    }

    @PermitAll
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN') or @productService.isOwner(#productId)")
    public Product updateProduct(String productId, Product updatedProduct) {
        Product product = getProductById(productId);
        String currentUserId = getCurrentUserId();

        if (!product.getUserId().equals(currentUserId) && !isAdmin()) {
            throw new RuntimeException("Not authorized to update this product");
        }

        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setDescription(updatedProduct.getDescription());

        return productRepository.save(product);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN') or @productService.isOwner(#productId)")
    public void deleteProduct(String productId) {
        Product product = getProductById(productId);
        String currentUserId = getCurrentUserId();

        if (!product.getUserId().equals(currentUserId) && !isAdmin()) {
            throw new RuntimeException("Not authorized to delete this product");
        }
        productRepository.deleteById(productId);
    }


}
