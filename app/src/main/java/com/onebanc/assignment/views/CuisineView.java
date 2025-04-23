package com.onebanc.assignment.views;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.onebanc.assignment.models.Cuisine;
import com.onebanc.assignment.models.Dish;
import com.onebanc.assignment.R;
import java.util.List;

public class CuisineView extends FrameLayout {
    private Context context;
    private TextView titleText;
    private LinearLayout dishesContainer;
    private TextView cartBadgeText;
    private Button backButton;
    private Cuisine currentCuisine;
    private CuisineViewListener listener;

    public interface CuisineViewListener {
        void onBackPressed();
        void onAddDishToCart(Dish dish);
        void onCartButtonClicked();
    }

    public CuisineView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_cuisine, this, true);

        // Initialize views
        titleText = view.findViewById(R.id.cuisine_title);
        dishesContainer = view.findViewById(R.id.dishes_container);
        cartBadgeText = view.findViewById(R.id.cart_badge);
        backButton = view.findViewById(R.id.back_button);
        FrameLayout cartButton = view.findViewById(R.id.cart_button);

        // Set listeners
        backButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBackPressed();
            }
        });

        cartButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCartButtonClicked();
            }
        });
    }

    public void setCuisine(Cuisine cuisine) {
        this.currentCuisine = cuisine;
        titleText.setText(cuisine.getName());
    }

    public Cuisine getCurrentCuisine() {
        return currentCuisine;
    }

    public void setDishes(List<Dish> dishes) {
        displayDishes(dishes);
    }

    public void setCuisineViewListener(CuisineViewListener listener) {
        this.listener = listener;
    }

    private void displayDishes(List<Dish> dishes) {
        dishesContainer.removeAllViews();

        if (dishes == null || dishes.isEmpty()) {
            TextView emptyText = new TextView(context);
            emptyText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            emptyText.setText(R.string.no_dishes_available);
            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.GRAY);
            emptyText.setPadding(dpToPx(16), dpToPx(32), dpToPx(16), dpToPx(16));
            emptyText.setGravity(android.view.Gravity.CENTER);
            dishesContainer.addView(emptyText);
            return;
        }

        for (Dish dish : dishes) {
            View dishView = createDishView(dish);
            dishesContainer.addView(dishView);
        }
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

        // Dish description (if available)
        if (dish.getDescription() != null && !dish.getDescription().isEmpty()) {
            TextView descText = new TextView(context);
            descText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            descText.setText(dish.getDescription());
            descText.setTextSize(12);
            descText.setTextColor(Color.GRAY);
            descText.setMaxLines(2);
            infoLayout.addView(descText);
        }

        // Dish price
        TextView priceText = new TextView(context);
        priceText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        priceText.setText(String.format("₹%.2f", dish.getPrice()));
        priceText.setTextSize(14);
        priceText.setPadding(0, dpToPx(4), 0, 0);
        infoLayout.addView(priceText);

        layout.addView(infoLayout);

        // Quantity controls
        LinearLayout quantityControls = new LinearLayout(context);
        quantityControls.setOrientation(LinearLayout.HORIZONTAL);
        quantityControls.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        quantityControls.setPadding(0, dpToPx(32), dpToPx(8), 0);

        // Add button
        Button addButton = new Button(context);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                dpToPx(80), dpToPx(40));
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
        quantityControls.addView(addButton);

        layout.addView(quantityControls);

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