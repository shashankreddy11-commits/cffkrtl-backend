package com.coffeekartel.dto.request;

import jakarta.validation.constraints.Min;

public class OrderItemRequest {

    private Long menuItemId;

    private String slug;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    public OrderItemRequest() {
    }

    public OrderItemRequest(Long menuItemId, int quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    public OrderItemRequest(String slug, int quantity) {
        this.slug = slug;
        this.quantity = quantity;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
