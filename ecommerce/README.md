#  E-Commerce CLI Application

A modular, scalable **Command-Line Interface (CLI) E-Commerce System** built in Java — designed to demonstrate **core software design patterns** such as Singleton, Factory, Strategy, Observer, Decorator, and Composite.  
This project simulates a real-world shopping experience.

##  Introduction

This CLI-based e-commerce system allows customers and admins to:

- Browse and manage products.
- Add products to a shopping cart.
- Checkout with multiple payment options.
- Track order status with real-time updates.
- View order history and order details.

The project emphasizes **clean code**, **design patterns**, and **production-like architecture** to simulate a real-world backend system.

##  Features

- **Product Catalog Management**  
  Add, view, and manage products via CLI.

- **Shopping Cart Operations**  
  Add, remove, and update products; calculate cart total.

- **Order Management**  
  Place orders and track status from payment to delivery.

- **Payment Processing**  
  Multiple payment strategies (Credit Card, PayPal).

- **Admin Mode**  
  View products, view all orders, and manage catalog.

- **Observer Pattern Integration**  
  Customers and admin receive real-time notifications on order status changes.

## 🛠 Design Patterns Used

| Pattern         | Purpose |
|-----------------|---------|
| **Singleton**   | Ensures a single instance of **DatabaseConnection**. |
| **Factory**     | Dynamically creates payment method instances. |
| **Strategy**    | Implements different payment methods under a common interface. |
| **Observer**    | Allows customers and admins to subscribe to order status changes. |
| **Decorator**   | Adds dynamic behaviors (Discounts, Tax, Gift Wrap) to products and orders. |
| **Composite**   | Handles product bundles as composite objects of products. |

##  Setup Instructions

### Requirements
- Java 17+
- Maven (optional)

### Steps
1. **Clone the repository**:
   ```bash
   git clone https://github.com/ShobikaAmarnath/DesignPatterns.git
   cd ecommerce
   ```
2. **Compile**:
    ```
    javac -d out src/**/*.java
    ```

3. **Run**:
    ```
    java -cp out com.ecommerce.Main
    ```

4. Follow the on-screen menus for Admin and Customer operations.

## Example Demo

### **1. Options Included**:

**Admin Menu**
```
=== Owner/Admin Menu ===
1. Add Product
2. View Product Catalog
3. View Orders
4. Show Database Summary
5. Exit Owner Mode
Choose option: 
```
**Customer Menu**
```
=== Customer Menu ===
1. View Products
2. Add Product to Cart
3. View Cart
4. Remove Product from Cart
5. Checkout
6. Exit Customer Mode
Choose option: 
```

### **2. Admin Flow**:

**Adding a Product (Composite Pattern)**
```
Add Product Type:
1. Single Product
2. Product Bundle
Choose type: 1
Enter product name: Laptop
Enter product price: 40000
Enter discount percentage (or press Enter to skip): 
✅Product 'Laptop' saved successfully to the database.
Add another product? (y/n, default n): y
```
**Viewing Product Catalog**
```
=== Product Catalog ===
1. Laptop
Product: Laptop | Price: $40000.00
    + Tax (18.00%)
    Price after tax: $47200.00
Price: $47200.00
----------------------------
2. Laptop Combo
Bundle: Laptop Combo
  - Product: Laptop | Price: $40000.00
  - Product: Mouse | Price: $1000.00
  - Product: Airpods | Price: $999.00
  Total Bundle Price: $41999.00
    - Discount (5.00%)
    Price after discount: $39899.05
    + Tax (18.00%)
    Price after tax: $47080.88
Price: $47080.88
```
**Database (Singleton Pattern)**
```
=== Database Summary ===
Products stored: 40
Orders stored: 10
```

### **Customer Flow**:
**Add to Cart**
```
Enter product number to add (or press Enter to stop): 2
'Laptop Combo' added to your cart.
Enter product number to add (or press Enter to stop): 4
'Kitchen Utensils' added to your cart.
Enter product number to add (or press Enter to stop): 
```

**Checkout (Added Decorator Pattern - Discount, tax, Gift Wrap)**
```
--- Final Order ---
Bundle: Order Items
  - Bundle: Laptop Combo
  - Product: Laptop | Price: $40000.00
  - Product: Mouse | Price: $1000.00
  - Product: Airpods | Price: $999.00
  Total Bundle Price: $41999.00
    - Discount (5.00%)
    Price after discount: $39899.05
    + Tax (18.00%)
    Price after tax: $47080.88
    + Gift Wrap ($5.00)

  - Bundle: Kitchen Utensils
  - Product: Stove | Price: $10000.00
  - Product: Cookware | Price: $200.00
  - Product: Spoons | Price: $50.00
  Total Bundle Price: $10250.00
    - Discount (5.00%)
    Price after discount: $9737.50
    + Tax (18.00%)
    Price after tax: $11490.25
    + Gift Wrap ($5.00)

  - Product: Laptop | Price: $40000.00
    + Tax (18.00%)
    Price after tax: $47200.00
    + Gift Wrap ($5.00)

  Total Bundle Price: $105786.13
    - Discount (10.00%)
    Price after discount: $95207.52

Total amount: $95207.52
```
**Payment (Factory Pattern and Strategy Pattern) and Order Status (Observer Pattern)**
```
Payment Methods:
1. Credit Card
2. PayPal
3. UPI
Choose payment method (1-3): 1
Paid $95207.52 using Credit Card.

🛒 Order placed successfully: ORD1759560655971
🧾 Order 'ORD1759560655971' saved successfully to the database.

👤 [Customer: Alice] - Your order ORD1759560655971 is now PAID.
🧑‍💼 [Admin Notification] - Order ORD1759560655971 changed status to: PAID

👤 [Customer: Alice] - Your order ORD1759560655971 is now SHIPPED.
🧑‍💼 [Admin Notification] - Order ORD1759560655971 changed status to: SHIPPED

👤 [Customer: Alice] - Your order ORD1759560655971 is now DELIVERED.
🧑‍💼 [Admin Notification] - Order ORD1759560655971 changed status to: DELIVERED
```

##  Why This Project Stands Out

- Demonstrates **multiple real-world design patterns** clearly.
- Implements a **modular and maintainable architecture**.
- Uses the **Observer pattern** for real-time updates.
- Simulates realistic **e-commerce checkout flows**.
- Serves as a **miniature production-level project** for portfolio presentation.



##  Future Enhancements

- Integrate a real **SQL or NoSQL database**.
- Develop a **REST API version** using Spring Boot.
- Add **authentication and authorization**.
- Create a **web or GUI front-end**.
- Implement **WebSocket for live order tracking**.
- Add **Admin Product Management features** such as update and delete products.
