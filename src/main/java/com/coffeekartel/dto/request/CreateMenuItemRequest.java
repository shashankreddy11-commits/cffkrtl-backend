package com.coffeekartel.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateMenuItemRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Slug is required")
    @Size(max = 100)
    private String slug;

    @NotBlank(message = "Item name is required")
    @Size(max = 150)
    private String name;

    private String subCategory;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    private BigDecimal price;

    private boolean isSignature;

    private boolean isPopular;

    @NotBlank(message = "Dietary type is required (BEVERAGE, VEG, NON_VEG)")
    private String dietaryType;

    private String imageUrl;

    public CreateMenuItemRequest() {
    }

    public CreateMenuItemRequest(Long categoryId, String slug, String name, String subCategory, String description, BigDecimal price, boolean isSignature, boolean isPopular, String dietaryType, String imageUrl) {
        this.categoryId = categoryId;
        this.slug = slug;
        this.name = name;
        this.subCategory = subCategory;
        this.description = description;
        this.price = price;
        this.isSignature = isSignature;
        this.isPopular = isPopular;
        this.dietaryType = dietaryType;
        this.imageUrl = imageUrl;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isSignature() {
        return isSignature;
    }

    public void setSignature(boolean signature) {
        isSignature = signature;
    }

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public String getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(String dietaryType) {
        this.dietaryType = dietaryType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
