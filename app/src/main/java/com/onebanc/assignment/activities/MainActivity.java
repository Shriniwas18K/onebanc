package com.onebanc.assignment.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.onebanc.assignment.R;
import androidx.appcompat.app.AppCompatActivity;

import com.onebanc.assignment.managers.CartManager;
import com.onebanc.assignment.managers.DataManager;
import com.onebanc.assignment.models.Cuisine;
import com.onebanc.assignment.models.Dish;
import com.onebanc.assignment.views.CartView;
import com.onebanc.assignment.views.CuisineView;
import com.onebanc.assignment.views.HomeView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // Container for all our views
    private FrameLayout container;

    // Managers
    private DataManager dataManager;
    private CartManager cartManager;

    // Views
    private HomeView homeView;
    private CuisineView cuisineView;
    private CartView cartView;

    // Current view tracking
    public enum CurrentView {
        HOME,
        CUISINE,
        CART
    }

    private CurrentView currentView = CurrentView.HOME;

    // Language settings
    private static final String PREFS_NAME = "AppPrefs";
    private static final String LANGUAGE_KEY = "language";
    private static final String LANGUAGE_ENGLISH = "en";
    private static final String LANGUAGE_HINDI = "hi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize managers
        dataManager = new DataManager();
        cartManager = new CartManager();

        // Setup container
        container = findViewById(R.id.container);

        // Load language preference
        loadLanguagePreference();

        // Initialize data (In a real app, this would load from API)
        dataManager.initializeData();

        // Initialize views
        initializeViews();

        // Show home view
        showHomeView();
    }

    private void initializeViews() {
        homeView = new HomeView(this);
        homeView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        homeView.setHomeViewListener(new HomeView.HomeViewListener() {
            @Override
            public void onCuisineSelected(Cuisine cuisine) {
                showCuisineView(cuisine);
            }

            @Override
            public void onCartButtonClicked() {
                showCartView();
            }

            @Override
            public void onLanguageToggleClicked() {
                toggleLanguage();
            }

            @Override
            public void onAddDishToCart(Dish dish) {
                cartManager.addDishToCart(dish);
                updateCartBadge();
            }
        });
    }

    public void showHomeView() {
        container.removeAllViews();
        container.addView(homeView);
        currentView = CurrentView.HOME;

        // Update the home view with data
        homeView.setCuisines(dataManager.getCuisines());
        homeView.setTopDishes(dataManager.getTopDishes());
        updateCartBadge();
    }

    public void showCuisineView(Cuisine cuisine) {
        if (cuisineView == null) {
            cuisineView = new CuisineView(this);
            cuisineView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            cuisineView.setCuisineViewListener(new CuisineView.CuisineViewListener() {
                @Override
                public void onBackPressed() {
                    showHomeView();
                }

                @Override
                public void onAddDishToCart(Dish dish) {
                    cartManager.addDishToCart(dish);
                    cuisineView.updateCartBadge(cartManager.getTotalItemCount());
                }

                @Override
                public void onCartButtonClicked() {
                    showCartView();
                }
            });
        }

        container.removeAllViews();
        container.addView(cuisineView);
        currentView = CurrentView.CUISINE;

        // Set data in the view
        cuisineView.setCuisine(cuisine);
        cuisineView.setDishes(dataManager.getDishesByCuisine(cuisine.getId()));
        cuisineView.updateCartBadge(cartManager.getTotalItemCount());
    }

    public void showCartView() {
        if (cartView == null) {
            cartView = new CartView(this);
            cartView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            cartView.setCartViewListener(new CartView.CartViewListener() {
                @Override
                public void onBackPressed() {
                    if (currentView == CurrentView.CUISINE) {
                        Cuisine lastCuisine = cuisineView.getCurrentCuisine();
                        showCuisineView(lastCuisine);
                    } else {
                        showHomeView();
                    }
                }

                @Override
                public void onPlaceOrderClicked() {
                    // In a real app, this would process the order
                    cartManager.clearCart();
                    showHomeView();
                    // Show a success message/dialog
                }

                @Override
                public void onCartItemUpdated() {
                    cartView.updateCartItems(cartManager.getCartItems(),
                            cartManager.getSubtotal(),
                            cartManager.getTaxAmount(),
                            cartManager.getGrandTotal());
                }
            });
        }

        container.removeAllViews();
        container.addView(cartView);
        CurrentView previousView = currentView;
        currentView = CurrentView.CART;

        // Set cart data
        cartView.updateCartItems(cartManager.getCartItems(),
                cartManager.getSubtotal(),
                cartManager.getTaxAmount(),
                cartManager.getGrandTotal());
    }

    @Override
    public void onBackPressed() {
        switch (currentView) {
            case CUISINE:
                showHomeView();
                break;
            case CART:
                if (cuisineView != null && cuisineView.getCurrentCuisine() != null) {
                    showCuisineView(cuisineView.getCurrentCuisine());
                } else {
                    showHomeView();
                }
                break;
            case HOME:
                super.onBackPressed(); // Exit app
                break;
        }
    }

    private void updateCartBadge() {
        int itemCount = cartManager.getTotalItemCount();
        homeView.updateCartBadge(itemCount);
        if (cuisineView != null) {
            cuisineView.updateCartBadge(itemCount);
        }
    }

    // Language management
    private void loadLanguagePreference() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = preferences.getString(LANGUAGE_KEY, LANGUAGE_ENGLISH);
        setLocale(language);
    }

    private void toggleLanguage() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currentLanguage = preferences.getString(LANGUAGE_KEY, LANGUAGE_ENGLISH);
        String newLanguage = LANGUAGE_ENGLISH.equals(currentLanguage) ? LANGUAGE_HINDI : LANGUAGE_ENGLISH;

        // Save and apply new language
        preferences.edit().putString(LANGUAGE_KEY, newLanguage).apply();
        setLocale(newLanguage);

        // Recreate activity to apply language change
        recreate();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    // Getters for managers
    public DataManager getDataManager() {
        return dataManager;
    }

    public CartManager getCartManager() {
        return cartManager;
    }
}