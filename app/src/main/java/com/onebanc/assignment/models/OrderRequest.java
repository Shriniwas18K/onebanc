package com.onebanc.assignment.models;

import java.util.ArrayList;
import java.util.List;

public class OrderRequest {
    private double totalAmount;
    private int totalItems;
    private List<OrderItem> items;

    public OrderRequest() {
        this.items = new ArrayList<>();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(int cuisineId, int itemId, double itemPrice, int itemQuantity) {
        this.items.add(new OrderItem(cuisineId, itemId, itemPrice, itemQuantity));
    }

    public static class OrderItem {
        private int cuisineId;
        private int itemId;
        private double itemPrice;
        private int itemQuantity;

        public OrderItem(int cuisineId, int itemId, double itemPrice, int itemQuantity) {
            this.cuisineId = cuisineId;
            this.itemId = itemId;
            this.itemPrice = itemPrice;
            this.itemQuantity = itemQuantity;
        }

        public int getCuisineId() {
            return cuisineId;
        }

        public int getItemId() {
            return itemId;
        }

        public double getItemPrice() {
            return itemPrice;
        }

        public int getItemQuantity() {
            return itemQuantity;
        }
    }
}