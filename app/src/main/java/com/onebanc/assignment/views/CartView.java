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

import com.onebanc.assignment.R;
import com.onebanc.assignment.models.CartItem;

import java.util.List;

public class CartView extends FrameLayout {
    private Context context;
    private LinearLayout cartItemsContainer;
    private TextView subtotalText;
    private TextView taxText;
    private TextView grandTotalText;
    private Button placeOrderButton;
    private Button backButton;
    private TextView emptyCartMessage;
    private CartViewListener listener;

    public interface CartViewListener {
        void onBackPressed();
        void onPlaceOrderClicked();
        void onCartItemUpdated();
    }

    public CartView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_cart, this, true);

        // Initialize views
        cartItemsContainer = view.findViewById(R.id.cart_items_container);
        subtotalText = view.findViewById(R.id.subtotal_value);
        taxText = view.findViewById(R.id.tax_value);
        grandTotalText = view.findViewById(R.id.grand_total_value);
        placeOrderButton = view.findViewById(R.id.place_order_button);
        backButton = view.findViewById(R.id.back_button);
        emptyCartMessage = view.findViewById(R.id.empty_cart_message);

        // Set listeners
        backButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBackPressed();
            }
        });

        placeOrderButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaceOrderClicked();
            }
        });
    }

    public void setCartViewListener(CartViewListener listener) {
        this.listener = listener;
    }

    public void updateCartItems(List<CartItem> cartItems, double subtotal, double taxAmount, double grandTotal) {
        cartItemsContainer.removeAllViews();

        if (cartItems == null || cartItems.isEmpty()) {
            // Show empty cart message
            emptyCartMessage.setVisibility(VISIBLE);
            placeOrderButton.setEnabled(false);
            subtotalText.setText("₹0.00");
            taxText.setText("₹0.00");
            grandTotalText.setText("₹0.00");
            return;
        }

        // Hide empty cart message
        emptyCartMessage.setVisibility(GONE);
        placeOrderButton.setEnabled(true);

        // Add title
        TextView titleText = new TextView(context);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(8));
        titleText.setLayoutParams(titleParams);
        titleText.setText(R.string.your_cart);
        titleText.setTextSize(18);
        titleText.setTextColor(Color.BLACK);
        cartItemsContainer.addView(titleText);

        // Add cart items
        for (CartItem item : cartItems) {
            View itemView = createCartItemView(item);
            cartItemsContainer.addView(itemView);
        }

        // Set price summaries
        subtotalText.setText(String.format("₹%.2f", subtotal));
        taxText.setText(String.format("₹%.2f", taxAmount));
        grandTotalText.setText(String.format("₹%.2f", grandTotal));
    }

    private View createCartItemView(CartItem item) {
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
        nameText.setText(item.getDish().getName());
        nameText.setTextSize(16);
        nameText.setTextColor(Color.BLACK);
        infoLayout.addView(nameText);

        // Unit price
        TextView priceText = new TextView(context);
        priceText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        priceText.setText(String.format("₹%.2f x %d", item.getDish().getPrice(), item.getQuantity()));
        priceText.setTextSize(14);
        infoLayout.addView(priceText);

        // Subtotal for this item
        TextView subtotalText = new TextView(context);
        subtotalText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        subtotalText.setText(String.format("Subtotal: ₹%.2f", item.getSubtotal()));
        subtotalText.setTextSize(14);
        subtotalText.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        subtotalText.setPadding(0, dpToPx(4), 0, 0);
        infoLayout.addView(subtotalText);

        layout.addView(infoLayout);

        // Quantity controls
        LinearLayout quantityControls = new LinearLayout(context);
        quantityControls.setOrientation(LinearLayout.HORIZONTAL);
        quantityControls.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        quantityControls.setPadding(0, dpToPx(28), dpToPx(8), 0);

        // Decrease button
        Button decreaseButton = new Button(context);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                dpToPx(40), dpToPx(40));
        decreaseButton.setLayoutParams(buttonParams);
        decreaseButton.setText("-");
        decreaseButton.setTextSize(16);
        decreaseButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        decreaseButton.setTextColor(Color.WHITE);
        decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.decrementQuantity();
            } else {
                // Remove from cart if quantity becomes 0
                item.setQuantity(0);
            }
            if (listener != null) {
                listener.onCartItemUpdated();
            }
        });
        quantityControls.addView(decreaseButton);

        // Quantity display
        TextView quantityText = new TextView(context);
        LinearLayout.LayoutParams quantityParams = new LinearLayout.LayoutParams(
                dpToPx(40), dpToPx(40));
        quantityText.setLayoutParams(quantityParams);
        quantityText.setText(String.valueOf(item.getQuantity()));
        quantityText.setTextSize(16);
        quantityText.setTextColor(Color.BLACK);
        quantityText.setGravity(android.view.Gravity.CENTER);
        quantityControls.addView(quantityText);

        // Increase button
        Button increaseButton = new Button(context);
        increaseButton.setLayoutParams(buttonParams);
        increaseButton.setText("+");
        increaseButton.setTextSize(16);
        increaseButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        increaseButton.setTextColor(Color.WHITE);
        increaseButton.setOnClickListener(v -> {
            item.incrementQuantity();
            if (listener != null) {
                listener.onCartItemUpdated();
            }
        });
        quantityControls.addView(increaseButton);

        layout.addView(quantityControls);

        card.addView(layout);
        return card;
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