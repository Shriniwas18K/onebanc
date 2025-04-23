package com.onebanc.assignment.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.onebanc.assignment.R;
import com.onebanc.assignment.models.Cuisine;
import com.onebanc.assignment.models.Dish;

import java.util.List;
import java.util.Locale;

public class HomeView extends FrameLayout {
    private Context context;
    private LinearLayout cuisinesContainer;
    private LinearLayout topDishesContainer;
    private TextView cartBadgeText;
    private Button languageToggleButton;
    private int currentCuisineIndex = 0;
    private List<Cuisine> cuisines;
    private HomeViewListener listener;

    public interface HomeViewListener {
        void onCuisineSelected(Cuisine cuisine);
        void onCartButtonClicked();
        void onLanguageToggleClicked();
        void onAddDishToCart(Dish dish);
    }

    public HomeView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_home, this, true);

        // Initialize views
        cuisinesContainer = view.findViewById(R.id.cuisines_container);
        topDishesContainer = view.findViewById(R.id.top_dishes_container);
        cartBadgeText = view.findViewById(R.id.cart_badge);
        languageToggleButton = view.findViewById(R.id.language_toggle_button);
        Button cartButton = view.findViewById(R.id.cart_button);

        // Set listeners
        cartButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCartButtonClicked();
            }
        });

        languageToggleButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLanguageToggleClicked();
            }
        });

        // Set initial language button text
        updateLanguageButtonText();
    }

    private void updateLanguageButtonText() {
        // Check current locale and set button text accordingly
        String currentLanguage = Locale.getDefault().getLanguage();
        if ("hi".equals(currentLanguage)) {
            languageToggleButton.setText("English");
        } else {
            languageToggleButton.setText("हिंदी");
        }
    }

    public void setCuisines(List<Cuisine> cuisines) {
        this.cuisines = cuisines;
        displayCuisines();
    }

    public void setTopDishes(List<Dish> topDishes) {
        displayTopDishes(topDishes);
    }

    public void setHomeViewListener(HomeViewListener listener) {
        this.listener = listener;
    }

    private void displayCuisines() {
        cuisinesContainer.removeAllViews();

        if (cuisines == null || cuisines.isEmpty()) {
            return;
        }

        // Add left and right navigation buttons
        ImageView leftNavButton = new ImageView(context);
        leftNavButton.setImageResource(R.drawable.ic_arrow_left);
        leftNavButton.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(40), dpToPx(40)));
        leftNavButton.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        leftNavButton.setOnClickListener(v -> scrollToPreviousCuisine());
        cuisinesContainer.addView(leftNavButton);

        // Add cuisine cards
        for (int i = 0; i < cuisines.size(); i++) {
            Cuisine cuisine = cuisines.get(i);
            CardView cuisineCard = createCuisineCard(cuisine);
            cuisineCard.setVisibility(i == currentCuisineIndex ? VISIBLE : GONE);
            cuisinesContainer.addView(cuisineCard);
        }

        // Add right navigation button
        ImageView rightNavButton = new ImageView(context);
        rightNavButton.setImageResource(R.drawable.ic_arrow_right);
        rightNavButton.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(40), dpToPx(40)));
        rightNavButton.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        rightNavButton.setOnClickListener(v -> scrollToNextCuisine());
        cuisinesContainer.addView(rightNavButton);
    }

    private CardView createCuisineCard(Cuisine cuisine) {
        CardView card = new CardView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(280), dpToPx(160));
        params.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(8));
        card.setCardElevation(dpToPx(4));

        // Create content layout
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.MATCH_PARENT));

        // Image
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(120)));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // In a real app, load image from URL
        // For now set a placeholder color
        imageView.setBackgroundColor(getRandomColor());
        layout.addView(imageView);

        // Text
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(40)));
        textView.setText(cuisine.getName());
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));
        layout.addView(textView);

        card.addView(layout);

        card.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCuisineSelected(cuisine);
            }
        });

        return card;
    }

    private void scrollToNextCuisine() {
        if (cuisines == null || cuisines.isEmpty()) {
            return;
        }

        // Hide current cuisine
        ((View) cuisinesContainer.getChildAt(currentCuisineIndex + 1)).setVisibility(GONE);

        // Move to next (with wraparound)
        currentCuisineIndex = (currentCuisineIndex + 1) % cuisines.size();

        // Show new current cuisine
        ((View) cuisinesContainer.getChildAt(currentCuisineIndex + 1)).setVisibility(VISIBLE);
    }

    private void scrollToPreviousCuisine() {
        if (cuisines == null || cuisines.isEmpty()) {
            return;
        }

        // Hide current cuisine
        ((View) cuisinesContainer.getChildAt(currentCuisineIndex + 1)).setVisibility(GONE);

        // Move to previous (with wraparound)
        currentCuisineIndex = (currentCuisineIndex - 1 + cuisines.size()) % cuisines.size();

        // Show new current cuisine
        ((View) cuisinesContainer.getChildAt(currentCuisineIndex + 1)).setVisibility(VISIBLE);
    }

    private void displayTopDishes(List<Dish> topDishes) {
        topDishesContainer.removeAllViews();

        if (topDishes == null || topDishes.isEmpty()) {
            return;
        }

        // Title for the section
        TextView titleText = new TextView(context);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        titleText.setText(R.string.top_dishes);
        titleText.setTextSize(18);
        titleText.setTextColor(Color.BLACK);
        titleText.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(8));
        topDishesContainer.addView(titleText);

        // Grid for top dishes (we'll use a linear layout with width set to match_parent)
        LinearLayout dishesGrid = new LinearLayout(context);
        dishesGrid.setOrientation(LinearLayout.VERTICAL);
        dishesGrid.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        for (Dish dish : topDishes) {
            View dishView = createDishView(dish);
            dishesGrid.addView(dishView);
        }

        topDishesContainer.addView(dishesGrid);
    }

    private View createDishView(Dish dish) {
        CardView card = new CardView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(100));
        params.setMargins(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(8));
        card.setCardElevation(dpToPx(2));

        // Create content layout
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.MATCH_PARENT));

        // Image
        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                dpToPx(80), LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // In a real app, load image from URL
        // For now set a placeholder color
        imageView.setBackgroundColor(getRandomColor());
        layout.addView(imageView);

        // Info container
        LinearLayout infoLayout = new LinearLayout(context);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        infoLayout.setLayoutParams(infoParams);
        infoLayout.setPadding(dpToPx(12), dpToPx(8), dpToPx(8), dpToPx(8));

        // Dish name
        TextView nameText = new TextView(context);
        nameText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        nameText.setText(dish.getName());
        nameText.setTextSize(16);
        nameText.setTextColor(Color.BLACK);
        infoLayout.addView(nameText);

        // Dish price
        TextView priceText = new TextView(context);
        priceText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        priceText.setText(String.format("₹%.2f", dish.getPrice()));
        priceText.setTextSize(14);
        infoLayout.addView(priceText);

        // Rating
        TextView ratingText = new TextView(context);
        ratingText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ratingText.setText(String.format("★ %.1f", dish.getRating()));
        ratingText.setTextSize(12);
        ratingText.setTextColor(Color.parseColor("#FFA000"));
        ratingText.setPadding(0, dpToPx(4), 0, 0);
        infoLayout.addView(ratingText);

        layout.addView(infoLayout);

        // Add button
        Button addButton = new Button(context);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                dpToPx(80), dpToPx(40));
        buttonParams.setMargins(0, dpToPx(30), dpToPx(8), 0);
        addButton.setLayoutParams(buttonParams);
        addButton.setText(R.string.add);
        addButton.setTextSize(12);
        addButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        addButton.setTextColor(Color.WHITE);
        addButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddDishToCart(dish);
            }
        });
        layout.addView(addButton);

        card.addView(layout);
        return card;
    }

    public void updateCartBadge(int count) {
        if (count > 0) {
            cartBadgeText.setVisibility(VISIBLE);
            cartBadgeText.setText(String.valueOf(count));
        } else {
            cartBadgeText.setVisibility(GONE);
        }
    }

    // Helper methods
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private int getRandomColor() {
        // Generate a random color for placeholder images
        int[] colors = {
                Color.parseColor("#FF5722"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#FFC107"),
                Color.parseColor("#8BC34A"),
                Color.parseColor("#009688"),
                Color.parseColor("#03A9F4"),
                Color.parseColor("#673AB7")
        };
        return colors[(int) (Math.random() * colors.length)];
    }
}