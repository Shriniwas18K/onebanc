# OneBanc Restaurant App

A feature-rich restaurant application developed for the OneBanc technical assignment that allows users to explore cuisines, view dishes, add items to cart, and place orders.

![image](https://github.com/user-attachments/assets/4c3f094e-ea45-40b0-8f83-8f468397bd0b)

## Features

### Multiple Screens with Intuitive Navigation
- **Home Screen**: Displays cuisine categories in an elegant horizontal infinite scroll and top 3 famous dishes
- **Cuisine Screen**: Lists dishes for the selected cuisine category with add-to-cart functionality
- **Cart Screen**: Summarizes selected items with pricing breakdown and order placement feature

### User-Friendly Interface
- Interactive UI with smooth animations and transitions
- Support for language switching between English and Hindi
- Intuitive item quantity management

### Technical Highlights
- Built using native Android technologies without third-party libraries
- Implements efficient API integration for fetching cuisine and dish data
- Handles edge cases and error scenarios gracefully
- Responsive design that works across different device sizes

## Screenshots

<table>
  <tr>
    <td>![image](https://github.com/user-attachments/assets/3784ef93-0d69-4c22-a1a5-7b695d90596d)
</td>
    <td>![image](https://github.com/user-attachments/assets/5bfaadd0-3267-491a-86be-895d42d0d451)
</td>
  </tr>
</table>

## App Architecture

The application follows the MVVM (Model-View-ViewModel) architecture pattern:

- **Model**: Data classes representing cuisines, dishes, and cart items
- **View**: Activities and fragments for UI rendering
- **ViewModel**: Business logic and state management
- **Repository**: Handles data operations and API integration

## API Integration

The app integrates with the following OneBanc APIs:

1. **get_item_list**: Fetches cuisine categories and dishes
2. **get_item_by_id**: Retrieves details of a specific dish
3. **get_item_by_filter**: Filters dishes based on cuisine type, price range, and rating
4. **make_payment**: Processes order payment

## Setup Instructions

1. Clone the repository:
   ```
   git clone https://github.com/Shriniwas/onebanc.git
   ```

2. Open the project in Android Studio

3. Build and run the application on an emulator or physical device

## Requirements

- Android Studio Arctic Fox (2020.3.1) or later
- Minimum SDK: API 21 (Android 5.0 Lollipop)
- Target SDK: API 33 (Android 13)
- Java Development Kit (JDK) 11 or later

## Implementation Details

### Home Screen
- Implements a custom `InfiniteViewPager` for horizontal cuisine carousel
- Uses `RecyclerView` with custom layout for displaying top dishes
- Dynamic content loading with visual feedback during API calls

### Cuisine Screen
- Efficiently loads and caches images for smooth scrolling
- Implements quantity counter with animation feedback
- Allows seamless transition back to home screen

### Cart Screen
- Real-time calculation of subtotal, taxes, and final amount
- Order summary with grouped items by cuisine
- Order confirmation with transaction reference tracking

## Testing

The application includes:
- Unit tests for ViewModel business logic
- UI tests for critical user flows
- Integration tests for API interactions

Run tests using:
```
./gradlew test
./gradlew connectedAndroidTest
```

## Future Enhancements

- User authentication and order history
- Favorite dishes and cuisines
- Delivery tracking integration
- Payment method selection
- Performance optimizations for image loading

## License

This project is part of a technical assignment and not licensed for public use.
