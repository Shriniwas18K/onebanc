package com.onebanc.assignment.api;

import android.util.Log;

import com.onebanc.assignment.models.Cuisine;
import com.onebanc.assignment.models.Dish;
import com.onebanc.assignment.models.OrderRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "https://uat.onebanc.ai";
    private static final String ENDPOINT_GET_ITEM_LIST = "/emulator/interview/get_item_list";
    private static final String ENDPOINT_GET_ITEM_BY_ID = "/emulator/interview/get_item_by_id";
    private static final String ENDPOINT_GET_ITEM_BY_FILTER = "/emulator/interview/get_item_by_filter";
    private static final String ENDPOINT_MAKE_PAYMENT = "/emulator/interview/make_payment";

    private static final String API_KEY = "uonebancservceemultrS3cg8RaL30";

    private static final Executor executor = Executors.newSingleThreadExecutor();

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    // Get list of all items with pagination
    public void getItemList(int page, int count, ApiCallback<Map<String, Object>> callback) {
        executor.execute(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("page", page);
                requestBody.put("count", count);

                String response = makePostRequest(ENDPOINT_GET_ITEM_LIST, "get_item_list", requestBody);
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("response_code") == 200) {
                    List<Cuisine> cuisines = new ArrayList<>();
                    Map<Integer, List<Dish>> dishesByCuisine = new HashMap<>();

                    JSONArray cuisinesArray = jsonResponse.getJSONArray("cuisines");
                    for (int i = 0; i < cuisinesArray.length(); i++) {
                        JSONObject cuisineObj = cuisinesArray.getJSONObject(i);

                        int cuisineId = Integer.parseInt(cuisineObj.getString("cuisine_id"));
                        String cuisineName = cuisineObj.getString("cuisine_name");
                        String cuisineImageUrl = cuisineObj.getString("cuisine_image_url");

                        Cuisine cuisine = new Cuisine(cuisineId, cuisineName, cuisineImageUrl);
                        cuisines.add(cuisine);

                        List<Dish> dishes = new ArrayList<>();
                        JSONArray itemsArray = cuisineObj.getJSONArray("items");
                        for (int j = 0; j < itemsArray.length(); j++) {
                            JSONObject itemObj = itemsArray.getJSONObject(j);

                            int itemId = Integer.parseInt(itemObj.getString("id"));
                            String itemName = itemObj.getString("name");
                            String imageUrl = itemObj.getString("image_url");
                            double price = Double.parseDouble(itemObj.getString("price"));
                            float rating = Float.parseFloat(itemObj.getString("rating"));

                            Dish dish = new Dish(itemId, itemName, "", price, imageUrl, rating, cuisineId, false);
                            dishes.add(dish);
                        }

                        dishesByCuisine.put(cuisineId, dishes);
                    }

                    Map<String, Object> result = new HashMap<>();
                    result.put("cuisines", cuisines);
                    result.put("dishesByCuisine", dishesByCuisine);
                    result.put("page", jsonResponse.getInt("page"));
                    result.put("totalPages", jsonResponse.getInt("total_pages"));
                    result.put("totalItems", jsonResponse.getInt("total_items"));

                    callback.onSuccess(result);
                } else {
                    callback.onError("Error: " + jsonResponse.getString("response_message"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching item list", e);
                callback.onError("Error fetching items: " + e.getMessage());
            }
        });
    }

    // Get item by ID
    public void getItemById(int itemId, ApiCallback<Dish> callback) {
        executor.execute(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("item_id", itemId);

                String response = makePostRequest(ENDPOINT_GET_ITEM_BY_ID, "get_item_by_id", requestBody);
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("response_code") == 200) {
                    int cuisineId = Integer.parseInt(jsonResponse.getString("cuisine_id"));
                    String cuisineName = jsonResponse.getString("cuisine_name");
                    int dishId = jsonResponse.getInt("item_id");
                    String dishName = jsonResponse.getString("item_name");
                    double price = jsonResponse.getDouble("item_price");
                    float rating = (float) jsonResponse.getDouble("item_rating");
                    String imageUrl = jsonResponse.getString("item_image_url");

                    Dish dish = new Dish(dishId, dishName, "", price, imageUrl, rating, cuisineId, false);
                    callback.onSuccess(dish);
                } else {
                    callback.onError("Error: " + jsonResponse.getString("response_message"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching item by ID", e);
                callback.onError("Error fetching item: " + e.getMessage());
            }
        });
    }

    // Get items by filter
    public void getItemsByFilter(List<String> cuisineTypes, Double minPrice, Double maxPrice,
                                 Float minRating, ApiCallback<Map<String, Object>> callback) {
        executor.execute(() -> {
            try {
                JSONObject requestBody = new JSONObject();

                // Add optional filters if provided
                if (cuisineTypes != null && !cuisineTypes.isEmpty()) {
                    JSONArray cuisineArray = new JSONArray();
                    for (String cuisine : cuisineTypes) {
                        cuisineArray.put(cuisine);
                    }
                    requestBody.put("cuisine_type", cuisineArray);
                }

                if (minPrice != null && maxPrice != null) {
                    JSONObject priceRange = new JSONObject();
                    priceRange.put("min_amount", minPrice);
                    priceRange.put("max_amount", maxPrice);
                    requestBody.put("price_range", priceRange);
                }

                if (minRating != null) {
                    requestBody.put("min_rating", minRating);
                }

                String response = makePostRequest(ENDPOINT_GET_ITEM_BY_FILTER, "get_item_by_filter", requestBody);
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("response_code") == 200) {
                    List<Cuisine> cuisines = new ArrayList<>();
                    Map<Integer, List<Dish>> dishesByCuisine = new HashMap<>();

                    JSONArray cuisinesArray = jsonResponse.getJSONArray("cuisines");
                    for (int i = 0; i < cuisinesArray.length(); i++) {
                        JSONObject cuisineObj = cuisinesArray.getJSONObject(i);

                        int cuisineId = cuisineObj.getInt("cuisine_id");
                        String cuisineName = cuisineObj.getString("cuisine_name");
                        String cuisineImageUrl = cuisineObj.getString("cuisine_image_url");

                        Cuisine cuisine = new Cuisine(cuisineId, cuisineName, cuisineImageUrl);
                        cuisines.add(cuisine);

                        List<Dish> dishes = new ArrayList<>();
                        JSONArray itemsArray = cuisineObj.getJSONArray("items");
                        for (int j = 0; j < itemsArray.length(); j++) {
                            JSONObject itemObj = itemsArray.getJSONObject(j);

                            int itemId = itemObj.getInt("id");
                            String itemName = itemObj.getString("name");
                            String imageUrl = itemObj.getString("image_url");

                            // Note: The API response doesn't include price and rating in the filter response
                            // You might need to call getItemById for complete information
                            Dish dish = new Dish(itemId, itemName, "", 0.0, imageUrl, 0.0f, cuisineId, false);
                            dishes.add(dish);
                        }

                        dishesByCuisine.put(cuisineId, dishes);
                    }

                    Map<String, Object> result = new HashMap<>();
                    result.put("cuisines", cuisines);
                    result.put("dishesByCuisine", dishesByCuisine);

                    callback.onSuccess(result);
                } else {
                    callback.onError("Error: " + jsonResponse.getString("response_message"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching items by filter", e);
                callback.onError("Error fetching filtered items: " + e.getMessage());
            }
        });
    }

    // Make payment
    public void makePayment(OrderRequest orderRequest, ApiCallback<String> callback) {
        executor.execute(() -> {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("total_amount", String.valueOf(orderRequest.getTotalAmount()));
                requestBody.put("total_items", orderRequest.getTotalItems());

                JSONArray itemsArray = new JSONArray();
                for (OrderRequest.OrderItem item : orderRequest.getItems()) {
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("cuisine_id", item.getCuisineId());
                    itemObj.put("item_id", item.getItemId());
                    itemObj.put("item_price", item.getItemPrice());
                    itemObj.put("item_quantity", item.getItemQuantity());
                    itemsArray.put(itemObj);
                }

                requestBody.put("data", itemsArray);

                String response = makePostRequest(ENDPOINT_MAKE_PAYMENT, "make_payment", requestBody);
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("response_code") == 200) {
                    String txnRefNo = jsonResponse.getString("txn_ref_no");
                    callback.onSuccess(txnRefNo);
                } else {
                    callback.onError("Error: " + jsonResponse.getString("response_message"));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error making payment", e);
                callback.onError("Error processing payment: " + e.getMessage());
            }
        });
    }

    // Helper method to make HTTP POST requests
    private String makePostRequest(String endpoint, String action, JSONObject requestBody)
            throws IOException, JSONException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Partner-API-Key", API_KEY);
            connection.setRequestProperty("X-Forward-Proxy-Action", action);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            } else {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                throw new IOException("HTTP error code: " + responseCode + ", Message: " + response.toString());
            }
        } finally {
            connection.disconnect();
        }
    }
}
