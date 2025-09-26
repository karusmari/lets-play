package com.letsplay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

public class UpdateProductRequest {
    private String name;
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Price must be a valid number with max 6 digits and 2 decimals")
    private Double price;

    public UpdateProductRequest() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price) { this.price = price; }
}

