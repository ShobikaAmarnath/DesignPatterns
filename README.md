#  Design Patterns – Java Implementations

This repository showcases **real-world Java implementations of multiple software design patterns** through two independent, production-style CLI applications:

-  **Smart Office – Interactive Room Booking System**
-  **E-Commerce CLI Application**

Each project demonstrates practical, pattern-driven software architecture emphasizing **clean code, scalability, and maintainability** — ideal for academic, interview, and professional portfolio purposes.


##  Projects Overview

###  **E-Commerce CLI Application**
A modular command-line shopping system simulating a real e-commerce flow:
- Product catalog management (Add, View, Bundle)
- Shopping cart operations
- Checkout with multiple payment methods
- Real-time order status updates via Observer pattern
- Discounts, taxes, and add-ons using Decorator pattern

**Patterns Implemented:**
`Singleton`, `Factory`, `Strategy`, `Observer`, `Decorator`, `Composite`

👉 [Read More → `ecommerce/README.md`](./ecommerce/README.md)

---

###  **Smart Office – Interactive Room Booking System**
A menu-driven smart office simulation managing:
- Room configurations and booking schedules  
- Occupancy detection via sensors  
- Automated AC and lighting systems  
- Auto-release of unoccupied rooms  

**Patterns Implemented:**
`Singleton`, `Command`, `Observer`, `Facade`, `Factory`, `Decorator (extension)`

👉 [Read More → `smart_office/README.md`](./smart_office/README.md)


##  Getting Started

```bash
# Clone repository
git clone https://github.com/ShobikaAmarnath/DesignPatterns.git
cd DesignPatterns

# Navigate to individual projects
cd ecommerce        # For E-Commerce system
# or
cd smart_office     # For Smart Office system
```