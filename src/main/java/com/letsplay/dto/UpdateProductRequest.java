package com.letsplay.dto;

public class UpdateProductRequest {
    private String name;
    private String description;
    private Double price;

    public UpdateProductRequest() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price) { this.price = price; }
}

