package com.letsplay.dto;

public class ProductResponse {
    private String name;
    private String description;
    private double price;
    private String sellerName;

    public ProductResponse() {}

    // User's product response constructor
    public ProductResponse(String name, String description, double price, String sellerName) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellerName = sellerName;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public String getSellerName() { return sellerName; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price) { this.price = price; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
}
