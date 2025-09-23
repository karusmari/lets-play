package com.letsplay.dto;

public class AdminProductResponse {
    private String productId;
    private String name;
    private String description;
    private Double price;
    private String sellerName;

    public AdminProductResponse(String productId, String name, String description, Double price, String sellerName) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellerName = sellerName;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public String getSellerName() { return sellerName; }
}
