package com.letsplay.repository;

import com.letsplay.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> getProductById(String productId); //mongoDb's id
    List<Product> getProductByName(String productName);
}

