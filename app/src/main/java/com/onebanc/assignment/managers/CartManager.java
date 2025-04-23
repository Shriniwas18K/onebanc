package com.onebanc.assignment.managers;

import com.onebanc.assignment.models.CartItem;
import com.onebanc.assignment.models.Dish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private Map<Integer, CartItem> cartItems;
    private static final double CGST_RATE = 0.025; // 2.5%
    private static final double SGST_RATE = 0.025; // 2.5%

    public CartManager() {
        cartItems = new HashMap<>();
    }

    /**
     * Add a dish to the cart
     * If dish already exists, increase quantity
     */
    public void addDishToCart(Dish dish) {
        int dishId = dish.getId();

        if (cartItems.containsKey(dishId)) {
            // Dish already in cart, increase quantity
            cartItems.get(dishId).incrementQuantity();
        } else {
            // New dish, add to cart with quantity 1
            cartItems.put(dishId, new CartItem(dish, 1));
        }
    }

    /**
     * Remove one quantity of a dish from the cart
     * Remove dish completely if quantity becomes zero
     */
    public void removeDishFromCart(int dishId) {
        if (cartItems.containsKey(dishId)) {
            CartItem item = cartItems.get(dishId);
            item.decrementQuantity();

            // Remove item if quantity is zero
            if (item.getQuantity() <= 0) {
                cartItems.remove(dishId);
            }
        }
    }

    /**
     * Remove dish completely from cart regardless of quantity
     */
    public void removeDishCompletelyFromCart(int dishId) {
        cartItems.remove(dishId);
    }

    /**
     * Update the quantity of a dish in the cart
     * Remove if quantity is set to zero or less
     */
    public void updateDishQuantity(int dishId, int quantity) {
        if (cartItems.containsKey(dishId)) {
            if (quantity <= 0) {
                cartItems.remove(dishId);
            } else {
                cartItems.get(dishId).setQuantity(quantity);
            }
        }
    }

    /**
     * Get all items in the cart as a list
     */
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems.values());
    }

    /**
     * Get total number of items (sum of quantities)
     */
    public int getTotalItemCount() {
        int count = 0;
        for (CartItem item : cartItems.values()) {
            count += item.getQuantity();
        }
        return count;
    }

    /**
     * Get total number of unique dishes in cart
     */
    public int getUniqueItemCount() {
        return cartItems.size();
    }

    /**
     * Get subtotal (sum of all items prices)
     */
    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems.values()) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    /**
     * Calculate CGST amount (2.5% of subtotal)
     */
    public double getCGST() {
        return getSubtotal() * CGST_RATE;
    }

    /**
     * Calculate SGST amount (2.5% of subtotal)
     */
    public double getSGST() {
        return getSubtotal() * SGST_RATE;
    }

    /**
     * Get total tax amount (CGST + SGST)
     */
    public double getTaxAmount() {
        return getCGST() + getSGST();
    }

    /**
     * Get grand total (subtotal + taxes)
     */
    public double getGrandTotal() {
        return getSubtotal() + getTaxAmount();
    }

    /**
     * Check if cart is empty
     */
    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    /**
     * Clear all items from cart
     */
    public void clearCart() {
        cartItems.clear();
    }

    /**
     * Check if a specific dish is in the cart
     */
    public boolean containsDish(int dishId) {
        return cartItems.containsKey(dishId);
    }

    /**
     * Get quantity of a specific dish in the cart
     */
    public int getDishQuantity(int dishId) {
        if (cartItems.containsKey(dishId)) {
            return cartItems.get(dishId).getQuantity();
        }
        return 0;
    }
}