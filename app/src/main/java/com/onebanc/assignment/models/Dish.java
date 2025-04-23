package com.onebanc.assignment.models;

public class Dish {
    private int id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private float rating;
    private int cuisineId;
    private boolean isTopDish;

    public Dish(int id, String name, String description, double price, String imageUrl, float rating, int cuisineId, boolean isTopDish) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.cuisineId = cuisineId;
        this.isTopDish = isTopDish;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getCuisineId() {
        return cuisineId;
    }

    public void setCuisineId(int cuisineId) {
        this.cuisineId = cuisineId;
    }

    public boolean isTopDish() {
        return isTopDish;
    }

    public void setTopDish(boolean topDish) {
        isTopDish = topDish;
    }
}