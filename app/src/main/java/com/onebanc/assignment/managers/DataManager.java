package com.onebanc.assignment.managers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.onebanc.assignment.api.ApiClient;
import com.onebanc.assignment.models.Cuisine;
import com.onebanc.assignment.models.Dish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static final String TAG = "DataManager";
    private Context context;
    private ApiClient apiClient;

    private List<Cuisine> cuisines;
    private List<Dish> dishes;
    private Map<Integer, List<Dish>> dishesByCuisine;
    private List<Dish> topDishes;
    private int currentPage = 1;
    private int totalPages = 1;
    private static final int PAGE_SIZE = 10;

    private DataFetchListener dataFetchListener;

    public interface DataFetchListener {
        void onDataFetched();
        void onError(String errorMessage);
    }

    public DataManager(Context context) {
        this.context = context;
        this.apiClient = new ApiClient();
        this.cuisines = new ArrayList<>();
        this.dishes = new ArrayList<>();
        this.dishesByCuisine = new HashMap<>();
        this.topDishes = new ArrayList<>();
    }

    public void setDataFetchListener(DataFetchListener listener) {
        this.dataFetchListener = listener;
    }

    /**
     * Fetch data from API
     */
    public void fetchData() {
        // Reset to first page when fetching all data
        currentPage = 1;
        fetchNextPage();
    }

    /**
     * Fetch next page of data
     */
    public void fetchNextPage() {
        if (currentPage > totalPages && totalPages > 0) {
            // No more pages to fetch
            return;
        }

        apiClient.getItemList(currentPage, PAGE_SIZE, new ApiClient.ApiCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                // If it's the first page, clear existing data
                if (currentPage == 1) {
                    cuisines.clear();
                    dishesByCuisine.clear();
                    dishes.clear();
                }

                // Update data
                List<Cuisine> fetchedCuisines = (List<Cuisine>) result.get("cuisines");
                Map<Integer, List<Dish>> fetchedDishesByCuisine = (Map<Integer, List<Dish>>) result.get("dishesByCuisine");

                cuisines.addAll(fetchedCuisines);

                // Add dishes to the dishes list and update the dishesByCuisine map
                for (Map.Entry<Integer, List<Dish>> entry : fetchedDishesByCuisine.entrySet()) {
                    if (!dishesByCuisine.containsKey(entry.getKey())) {
                        dishesByCuisine.put(entry.getKey(), new ArrayList<>());
                    }
                    dishesByCuisine.get(entry.getKey()).addAll(entry.getValue());
                    dishes.addAll(entry.getValue());
                }

                // Update pagination information
                currentPage = (int) result.get("page") + 1; // Next page
                totalPages = (int) result.get("totalPages");

                // After fetching the data, identify top dishes
                // For simplicity, we'll consider the first 3 dishes with rating above 4.5 as "top"
                identifyTopDishes();

                if (dataFetchListener != null) {
                    dataFetchListener.onDataFetched();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error fetching data: " + errorMessage);
                if (dataFetchListener != null) {
                    dataFetchListener.onError(errorMessage);
                }
            }
        });
    }

    /**
     * For our simple implementation, we'll consider dishes with high ratings as "top dishes"
     */
    private void identifyTopDishes() {
        topDishes.clear();

        List<Dish> allDishes = new ArrayList<>();
        for (List<Dish> cuisineDishes : dishesByCuisine.values()) {
            allDishes.addAll(cuisineDishes);
        }

        // Sort by rating in descending order
        allDishes.sort((dish1, dish2) -> Float.compare(dish2.getRating(), dish1.getRating()));

        // Take top 3 or less if there are fewer dishes
        int topDishCount = Math.min(3, allDishes.size());
        for (int i = 0; i < topDishCount; i++) {
            Dish dish = allDishes.get(i);
            // Mark as top dish
            dish.setTopDish(true);
            topDishes.add(dish);
        }
    }

    /**
     * Get dish details by ID
     */
    public void getDishById(int dishId, ApiClient.ApiCallback<Dish> callback) {
        // First check if dish is already loaded
        for (Dish dish : dishes) {
            if (dish.getId() == dishId) {
                callback.onSuccess(dish);
                return;
            }
        }

        // If not loaded, fetch from API
        apiClient.getItemById(dishId, callback);
    }

    /**
     * Filter dishes by cuisine, price range, and/or rating
     */
    public void filterDishes(List<String> cuisineTypes, Double minPrice, Double maxPrice,
                             Float minRating, ApiClient.ApiCallback<Map<String, Object>> callback) {
        apiClient.getItemsByFilter(cuisineTypes, minPrice, maxPrice, minRating, callback);
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
     * Get top dishes
     */
    public List<Dish> getTopDishes() {
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

    /**
     * Check if we have more pages to fetch
     */
    public boolean hasMorePages() {
        return currentPage <= totalPages;
    }
}