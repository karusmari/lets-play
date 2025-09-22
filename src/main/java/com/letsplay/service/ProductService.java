package com.letsplay.service;

import com.letsplay.model.Product;
import com.letsplay.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;


// service is responsible for business logic and data manipulation. It chooses how to handle data and interacts with the repository layer.
// it doesn't handle HTTP requests directly, that's the controller's job.
@Service
public class ProductService {
    private ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {

        // to receive the id of the user logged in
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId;
        if (principal instanceof UserDetails) {
            userId = ((UserDetails) principal).getUsername();
        } else {
            userId = principal.toString();
        }
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

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    public Product updateProduct(String productId, Product updatedProduct) {
        Product product = getProductById(productId);

        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setDescription(updatedProduct.getDescription());

        //only the owner and admin can update
        String currentUserId = getCurrentUserId();
        if (!product.getUserId().equals(currentUserId) && !isAdmin()) {
            throw new RuntimeException("Not authorized to update this product");
        }

        return productRepository.save(product);
    }

    public void deleteProduct(String productId) {
        Product product = getProductById(productId);
        String currentUserId = getCurrentUserId();
        if (!product.getUserId().equals(currentUserId) && !isAdmin()) {
            throw new RuntimeException("Not authorized to delete this product");
        }
        productRepository.deleteById(productId);
    }


}
