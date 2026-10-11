package com.coffeekartel.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderTrackingResponse {

    private String orderNumber;
    private String serviceType;
    private String tableNumber;
    private String orderStatus;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private List<TrackingItem> items;

    public static class TrackingItem {
        private String itemName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        public TrackingItem() {
        }

        public TrackingItem(String itemName, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }
    }

    public OrderTrackingResponse() {
    }

    public OrderTrackingResponse(String orderNumber, String serviceType, String tableNumber,
                                 String orderStatus, BigDecimal totalAmount, Instant createdAt,
                                 List<TrackingItem> items) {
        this.orderNumber = orderNumber;
        this.serviceType = serviceType;
        this.tableNumber = tableNumber;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.items = items;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<TrackingItem> getItems() {
        return items;
    }

    public void setItems(List<TrackingItem> items) {
        this.items = items;
    }
}
