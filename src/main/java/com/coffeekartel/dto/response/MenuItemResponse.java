package com.coffeekartel.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public class MenuItemResponse {

    private Long id;
    private String slug;
    private String name;
    private Long categoryId;
    private String categorySlug;
    private String categoryName;
    private String subCategory;
    private String description;
    private BigDecimal price;
    private String priceDisplay;
    private boolean isAvailable;
    private boolean isSignature;
    private boolean isPopular;
    private String dietaryType;
    private String imageUrl;
    private Instant createdAt;

    public MenuItemResponse() {
    }

    public MenuItemResponse(Long id, String slug, String name, Long categoryId,
                            String categorySlug, String categoryName, String subCategory,
                            String description, BigDecimal price, String priceDisplay,
                            boolean isAvailable, boolean isSignature, boolean isPopular,
                            String dietaryType, String imageUrl, Instant createdAt) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.categoryId = categoryId;
        this.categorySlug = categorySlug;
        this.categoryName = categoryName;
        this.subCategory = subCategory;
        this.description = description;
        this.price = price;
        this.priceDisplay = priceDisplay;
        this.isAvailable = isAvailable;
        this.isSignature = isSignature;
        this.isPopular = isPopular;
        this.dietaryType = dietaryType;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getPriceDisplay() {
        return priceDisplay;
    }

    public void setPriceDisplay(String priceDisplay) {
        this.priceDisplay = priceDisplay;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
