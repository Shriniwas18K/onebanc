package com.onebanc.assignment.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.onebanc.assignment.R;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.onebanc.assignment.models.CartItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Simple memory cache for images
    private static Map<String, Bitmap> imageCache = new HashMap<>();

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

        // Set placeholder color initially
        imageView.setBackgroundColor(getPlaceholderColor(item.getDish().getId()));

        // Load dish image from URL if available
        String imageUrl = item.getDish().getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImageFromUrl(imageUrl, imageView);
        } else {
            // If no image URL, use placeholder or default image
            imageView.setImageResource(getPlaceholderColor(Color.GREEN));
        }

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

    // Image loading methods
    private void loadImageFromUrl(String imageUrl, ImageView imageView) {
        // Check if the image is already in our cache
        if (imageCache.containsKey(imageUrl)) {
            imageView.setImageBitmap(imageCache.get(imageUrl));
            return;
        }

        // Otherwise, load it asynchronously
        new ImageDownloadTask(imageView).execute(imageUrl);
    }

    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private String imageUrl;

        public ImageDownloadTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            imageUrl = urls[0];
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                // Add to cache
                imageCache.put(imageUrl, result);
                // Set the bitmap to the ImageView
                imageView.setImageBitmap(result);
            }
        }
    }

    // Helper methods
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private int getPlaceholderColor(int id) {
        // Generate a color based on the ID for consistency
        int[] colors = {
                Color.parseColor("#FF5722"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#FFC107"),
                Color.parseColor("#8BC34A"),
                Color.parseColor("#009688"),
                Color.parseColor("#03A9F4"),
                Color.parseColor("#673AB7")
        };
        return colors[Math.abs(id) % colors.length];
    }
}