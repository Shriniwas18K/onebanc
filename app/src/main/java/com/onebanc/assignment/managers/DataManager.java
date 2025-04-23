package com.onebanc.assignment.managers;

import com.onebanc.assignment.models.Cuisine;
import com.onebanc.assignment.models.Dish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataManager {
    private List<Cuisine> cuisines;
    private List<Dish> dishes;
    private Map<Integer, List<Dish>> dishesByCuisine;

    public DataManager() {
        cuisines = new ArrayList<>();
        dishes = new ArrayList<>();
        dishesByCuisine = new HashMap<>();
    }

    /**
     * Initialize sample data for the application
     * In a real app, this would fetch data from an API
     */
    public void initializeData() {
        // Create cuisines
        cuisines.add(new Cuisine(1, "North Indian", "https://example.com/north_indian.jpg"));
        cuisines.add(new Cuisine(2, "Chinese", "https://example.com/chinese.jpg"));
        cuisines.add(new Cuisine(3, "Mexican", "https://example.com/mexican.jpg"));
        cuisines.add(new Cuisine(4, "South Indian", "https://example.com/south_indian.jpg"));
        cuisines.add(new Cuisine(5, "Italian", "https://example.com/italian.jpg"));

        // Create dishes
        // North Indian dishes
        dishes.add(new Dish(101, "Butter Chicken", "Tender chicken in creamy tomato sauce",
                299.0, "https://example.com/butter_chicken.jpg", 4.7f, 1, true));
        dishes.add(new Dish(102, "Paneer Tikka", "Marinated cottage cheese pieces grilled to perfection",
                249.0, "https://example.com/paneer_tikka.jpg", 4.5f, 1, false));
        dishes.add(new Dish(103, "Dal Makhani", "Black lentils cooked with butter and cream",
                199.0, "https://example.com/dal_makhani.jpg", 4.3f, 1, false));
        dishes.add(new Dish(104, "Naan", "Tandoor-baked flatbread",
                59.0, "https://example.com/naan.jpg", 4.6f, 1, false));

        // Chinese dishes
        dishes.add(new Dish(201, "Schezwan Noodles", "Stir-fried noodles with vegetables in spicy sauce",
                189.0, "https://example.com/schezwan_noodles.jpg", 4.2f, 2, false));
        dishes.add(new Dish(202, "Manchurian", "Fried vegetable dumplings in tangy sauce",
                199.0, "https://example.com/manchurian.jpg", 4.4f, 2, true));
        dishes.add(new Dish(203, "Spring Rolls", "Crispy rolls filled with vegetables",
                129.0, "https://example.com/spring_rolls.jpg", 4.1f, 2, false));

        // Mexican dishes
        dishes.add(new Dish(301, "Tacos", "Corn tortillas with various fillings",
                219.0, "https://example.com/tacos.jpg", 4.5f, 3, false));
        dishes.add(new Dish(302, "Nachos", "Tortilla chips with cheese and toppings",
                249.0, "https://example.com/nachos.jpg", 4.6f, 3, false));
        dishes.add(new Dish(303, "Quesadilla", "Grilled tortilla filled with cheese and vegetables",
                229.0, "https://example.com/quesadilla.jpg", 4.3f, 3, true));

        // South Indian dishes
        dishes.add(new Dish(401, "Masala Dosa", "Crispy rice crepe with potato filling",
                179.0, "https://example.com/masala_dosa.jpg", 4.7f, 4, false));
        dishes.add(new Dish(402, "Idli Sambar", "Steamed rice cakes with lentil soup",
                159.0, "https://example.com/idli_sambar.jpg", 4.4f, 4, false));
        dishes.add(new Dish(403, "Vada", "Savory fried snack",
                129.0, "https://example.com/vada.jpg", 4.3f, 4, false));

        // Italian dishes
        dishes.add(new Dish(501, "Margherita Pizza", "Classic pizza with tomato and cheese",
                279.0, "https://example.com/margherita.jpg", 4.8f, 5, false));
        dishes.add(new Dish(502, "Pasta Alfredo", "Pasta in creamy white sauce",
                249.0, "https://example.com/pasta_alfredo.jpg", 4.6f, 5, false));
        dishes.add(new Dish(503, "Lasagna", "Layered pasta with sauce and cheese",
                299.0, "https://example.com/lasagna.jpg", 4.7f, 5, false));

        // Organize dishes by cuisine for faster access
        for (Dish dish : dishes) {
            if (!dishesByCuisine.containsKey(dish.getCuisineId())) {
                dishesByCuisine.put(dish.getCuisineId(), new ArrayList<>());
            }
            dishesByCuisine.get(dish.getCuisineId()).add(dish);
        }
    }

    /**
     * Get all cuisine categories
     */
    public List<Cuisine> getCuisines() {
        return cuisines;
    }

    /**
     * Get a cuisine by its ID
     */
    public Cuisine getCuisineById(int cuisineId) {
        for (Cuisine cuisine : cuisines) {
            if (cuisine.getId() == cuisineId) {
                return cuisine;
            }
        }
        return null;
    }

    /**
     * Get all dishes
     */
    public List<Dish> getAllDishes() {
        return dishes;
    }

    /**
     * Get dishes for a specific cuisine
     */
    public List<Dish> getDishesByCuisine(int cuisineId) {
        return dishesByCuisine.getOrDefault(cuisineId, new ArrayList<>());
    }

    /**
     * Get top dishes marked as featured
     */
    public List<Dish> getTopDishes() {
        List<Dish> topDishes = new ArrayList<>();
        for (Dish dish : dishes) {
            if (dish.isTopDish()) {
                topDishes.add(dish);
            }
            // Return max 3 top dishes
            if (topDishes.size() >= 3) {
                break;
            }
        }
        return topDishes;
    }

    /**
     * Get a dish by its ID
     */
    public Dish getDishById(int dishId) {
        for (Dish dish : dishes) {
            if (dish.getId() == dishId) {
                return dish;
            }
        }
        return null;
    }
}