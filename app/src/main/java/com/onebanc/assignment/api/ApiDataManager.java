package com.onebanc.assignment.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.onebanc.assignment.models.Cuisine;
import com.onebanc.assignment.models.Dish;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiDataManager {
    private static final String TAG = "ApiDataManager";
    private static final String BASE_URL = "https://uat.onebanc.ai";
    private static final String API_KEY = "uonebancservceemultrS3cg8RaL30";

    private RequestQueue requestQueue;
    private Context context;

    // Callbacks for async operations
    public interface ApiResponseListener<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    public ApiDataManager(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Get all cuisine categories and their dishes
     */
    public void getAllCuisinesAndDishes(int page, int count, final ApiResponseListener<List<Cuisine>> listener) {
        String endpoint = "/emulator/interview/get_item_list";

        try {
            // Prepare request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("page", page);
            requestBody.put("count", count);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL + endpoint,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Check if request was successful
                                int responseCode = response.getInt("response_code");
                                if (responseCode == 200) {
                                    List<Cuisine> cuisines = parseCuisines(response);
                                    listener.onSuccess(cuisines);
                                } else {
                                    String errorMessage = response.getString("response_message");
                                    listener.onError(errorMessage);
                                }
                            } catch (JSONException e) {
                                listener.onError("Error parsing response: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            listener.onError("Network error: " + error.getMessage());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("X-Partner-API-Key", API_KEY);
                    headers.put("X-Forward-Proxy-Action", "get_item_list");
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            listener.onError("Error creating request: " + e.getMessage());
        }
    }

    /**
     * Get specific item by ID
     */
    public void getItemById(int itemId, final ApiResponseListener<Dish> listener) {
        String endpoint = "/emulator/interview/get_item_by_id";

        try {
            // Prepare request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("item_id", itemId);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL + endpoint,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Check if request was successful
                                int responseCode = response.getInt("response_code");
                                if (responseCode == 200) {
                                    Dish dish = parseDishDetails(response);
                                    listener.onSuccess(dish);
                                } else {
                                    String errorMessage = response.getString("response_message");
                                    listener.onError(errorMessage);
                                }
                            } catch (JSONException e) {
                                listener.onError("Error parsing response: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            listener.onError("Network error: " + error.getMessage());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("X-Partner-API-Key", API_KEY);
                    headers.put("X-Forward-Proxy-Action", "get_item_by_id");
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            listener.onError("Error creating request: " + e.getMessage());
        }
    }

    /**
     * Get items by filter (cuisine type, price range, minimum rating)
     */
    public void getItemsByFilter(List<String> cuisineTypes, Double minPrice, Double maxPrice,
                                 Float minRating, final ApiResponseListener<List<Cuisine>> listener) {
        String endpoint = "/emulator/interview/get_item_by_filter";

        try {
            // Prepare request body with filters
            JSONObject requestBody = new JSONObject();

            // Add cuisine filter if specified
            if (cuisineTypes != null && !cuisineTypes.isEmpty()) {
                JSONArray cuisineArray = new JSONArray();
                for (String cuisine : cuisineTypes) {
                    cuisineArray.put(cuisine);
                }
                requestBody.put("cuisine_type", cuisineArray);
            }

            // Add price range filter if specified
            if (minPrice != null && maxPrice != null) {
                JSONObject priceRange = new JSONObject();
                priceRange.put("min_amount", minPrice);
                priceRange.put("max_amount", maxPrice);
                requestBody.put("price_range", priceRange);
            }

            // Add rating filter if specified
            if (minRating != null) {
                requestBody.put("min_rating", minRating);
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL + endpoint,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Check if request was successful
                                int responseCode = response.getInt("response_code");
                                if (responseCode == 200) {
                                    List<Cuisine> cuisines = parseCuisines(response);
                                    listener.onSuccess(cuisines);
                                } else {
                                    String errorMessage = response.getString("response_message");
                                    listener.onError(errorMessage);
                                }
                            } catch (JSONException e) {
                                listener.onError("Error parsing response: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            listener.onError("Network error: " + error.getMessage());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("X-Partner-API-Key", API_KEY);
                    headers.put("X-Forward-Proxy-Action", "get_item_by_filter");
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            listener.onError("Error creating request: " + e.getMessage());
        }
    }

    /**
     * Make payment for ordered items
     */
    public void makePayment(double totalAmount, int totalItems, List<Map<String, Object>> orderItems,
                            final ApiResponseListener<String> listener) {
        String endpoint = "/emulator/interview/make_payment";

        try {
            // Prepare request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("total_amount", String.valueOf(totalAmount));
            requestBody.put("total_items", totalItems);

            // Add order items
            JSONArray itemsArray = new JSONArray();
            for (Map<String, Object> item : orderItems) {
                JSONObject itemObj = new JSONObject();
                itemObj.put("cuisine_id", item.get("cuisine_id"));
                itemObj.put("item_id", item.get("item_id"));
                itemObj.put("item_price", item.get("item_price"));
                itemObj.put("item_quantity", item.get("item_quantity"));
                itemsArray.put(itemObj);
            }
            requestBody.put("data", itemsArray);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    BASE_URL + endpoint,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Check if request was successful
                                int responseCode = response.getInt("response_code");
                                if (responseCode == 200) {
                                    String txnRefNo = response.getString("txn_ref_no");
                                    listener.onSuccess(txnRefNo);
                                } else {
                                    String errorMessage = response.getString("response_message");
                                    listener.onError(errorMessage);
                                }
                            } catch (JSONException e) {
                                listener.onError("Error parsing response: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            listener.onError("Network error: " + error.getMessage());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("X-Partner-API-Key", API_KEY);
                    headers.put("X-Forward-Proxy-Action", "make_payment");
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            listener.onError("Error creating request: " + e.getMessage());
        }
    }

    // Helper methods to parse API responses

    private List<Cuisine> parseCuisines(JSONObject response) throws JSONException {
        List<Cuisine> cuisineList = new ArrayList<>();

        JSONArray cuisinesArray = response.getJSONArray("cuisines");
        for (int i = 0; i < cuisinesArray.length(); i++) {
            JSONObject cuisineObj = cuisinesArray.getJSONObject(i);

            // Parse cuisine details
            int cuisineId = Integer.parseInt(cuisineObj.getString("cuisine_id"));
            String cuisineName = cuisineObj.getString("cuisine_name");
            String cuisineImageUrl = cuisineObj.getString("cuisine_image_url");

            Cuisine cuisine = new Cuisine(cuisineId, cuisineName, cuisineImageUrl);
            cuisineList.add(cuisine);

            // Parse dishes for this cuisine
            JSONArray dishesArray = cuisineObj.getJSONArray("items");
            List<Dish> dishes = new ArrayList<>();

            for (int j = 0; j < dishesArray.length(); j++) {
                JSONObject dishObj = dishesArray.getJSONObject(j);

                int dishId = Integer.parseInt(dishObj.getString("id"));
                String dishName = dishObj.getString("name");
                String imageUrl = dishObj.getString("image_url");
                double price = Double.parseDouble(dishObj.getString("price"));
                float rating = Float.parseFloat(dishObj.getString("rating"));

                // Create dish object (using empty description since it's not provided by API)
                Dish dish = new Dish(dishId, dishName, "", price, imageUrl, rating, cuisineId, false);
                dishes.add(dish);
            }

            // Store dishes for each cuisine (would require adding a dishes field to Cuisine class)
            // cuisine.setDishes(dishes);
        }

        return cuisineList;
    }

    private Dish parseDishDetails(JSONObject response) throws JSONException {
        int cuisineId = Integer.parseInt(response.getString("cuisine_id"));
        int itemId = response.getInt("item_id");
        String itemName = response.getString("item_name");
        double itemPrice = response.getDouble("item_price");
        float itemRating = (float) response.getDouble("item_rating");
        String itemImageUrl = response.getString("item_image_url");

        // Create dish object (using empty description since it's not provided by API)
        return new Dish(itemId, itemName, "", itemPrice, itemImageUrl, itemRating, cuisineId, false);
    }
}