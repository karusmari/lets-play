package com.letsplay.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
    @Id
    private String productId;
    private String name;
    private String description;
    private double price;
    private String userId;

    // constructor
    public Product() {}
    public Product(String productId, String name, String description, double price, String userId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.userId = userId;
    }

    // getters and setters
    public String getId() { return productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}

