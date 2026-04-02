#  Java Smart Parking Management Application

The **Smart Parking Management Application** is a full-stack Java-based enterprise solution designed to solve modern urban parking challenges. It features AI-driven hotspot detection, real-time slot tracking, automated billing, and secure end-to-end payment integration.

Built with a robust **Spring Boot** backend and a premium **Vanilla JavaScript + HTML/CSS** frontend, the system optimizes parking space utilization while providing a seamless, high-performance experience for users and administrators.

---

## Key Features

### Advanced Parking Logic
- **Real-Time Slot Availability** – Live grid visualization of parking slots (Free, Occupied, Reserved).
- **Dynamic Pricing & Billing** – Automated fee calculation (₹50/hour) based on live session timing.
- **Automatic Release** – Real-time status updates and session termination with final billing.
- **Authorization & Security** – Role-based access control (User, Admin, Emergency) with secure BCrypt-backed authentication.

### Secure Payment Integration
- **Integrated Payment Gateway** – Unified checkout via **Razorpay** for UPI, Cards, and Wallets.
- **Transaction Verification** – Backend-level signature verification to ensure data integrity and prevent fraud.
- **Automated Record Keeping** – Real-time payment tracking and booking status updates in the SQL database.

### Smart Features & Analytics
- **Location-Based Search** – Proximity-based area discovery using coordinate calculation to return nearest locations.
- **Hotspot Detection Logic** – Real-time demand tracking to monitor active bookings and flag high-occupancy areas.
- **Navigation Simulation** – One-click integration that generates a direct **Google Maps URL** for seamless arrival.
- **Emergency Vehicle Priority** – Automated scan for dedicated `emergency` slots. If unavailable, the system identifies the nearest regular slot and prioritizes it for emergency use.

---

## 🏗️ Project Structure

```bash
smartparking/
├── backend/                        # Spring Boot Application
│   ├── src/main/java/com/smartparking/
│   │   ├── controller/             # REST API Endpoints (Parking, Booking, Payments, Auth)
│   │   ├── service/                # Business Logic (Auth, Booking, Smart Parking, Payments)
│   │   ├── model/                  # POJOs / Database Entities (User, Slot, Area, Booking, etc.)
│   │   ├── repository/             # Data Access Layer (JDBC Template)
│   │   ├── util/                   # Helper functions (Billing, Distance logic)
│   │   └── SmartParkingApp.java    # Main Entry Point
│   ├── src/main/resources/
│   │   ├── application.properties  # DB & Razorpay Config
│   │   ├── schema.sql              # Database Schema Script
│   │   └── data.sql                # Seed Data Script
│   └── pom.xml                     # Maven Dependencies
│
└── frontend/                       # Web Client (HTML/CSS/JS)
    ├── index.html                  # Auth Portal (Login/Signup)
    ├── dashboard.html              # Search & Interactive Parking Map
    ├── booking.html                # Reserve & Manage Slots
    ├── payment.html                # Unified Payment Gateway
    └── timer.html                  # Active Session Monitor & Bill Calculation
```

---

## Setup Instructions (Windows)

### Prerequisites
- **Java JDK 17+**
- **Maven 3.6+**
- **H2 / MySQL Database**
- **Python 3.x** (for serving the frontend)

### Backend Setup (Spring Boot)
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Configure your database credentials and Razorpay keys in `src/main/resources/application.properties`.
3. Build and run the server:
   ```bash
   mvn spring-boot:run
   ```
The API server will start at `http://localhost:8080`.

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Start a local development server:
   ```bash
   python -m http.server 3000
   ```
The application will be accessible at `http://localhost:3000`.

---

## Database Architecture

The system utilizes a relational schema for high data integrity:
1. **`users`**: Profiles with role-based access control (User, Admin, Emergency).
2. **`parking_areas`**: Location data and live hotspot tracking.
3. **`parking_slots`**: Individual slot states (Regular vs Emergency).
4. **`bookings`**: Session records with precise entry/exit timestamps.
5. **`payments`**: Transaction logs linked to Razorpay `order_id`.
6. **`hotspots`**: Demand scoring logic for peak-hour pricing.

---
👥 Team Contributions

The Smart Parking Management Application was developed collaboratively, with each member contributing to specific modules to ensure a scalable, modular, and efficient system design.

🔹 Member 1 – Backend Core (Parking Logic & APIs)

Responsibility:

Core parking system logic
Real-time slot availability
Booking and release system
Timer-based billing

Key Contributions:

Developed REST APIs:
/checkSlots
/bookSlot
/releaseSlot
Implemented real-time slot status updates
Designed booking lifecycle (reserve → active → release)
Built automated billing system based on session duration

Technologies & Concepts:

Spring Boot / Servlets
Multithreading (ScheduledExecutorService)
JDBC

Member 2 – System Design + Payment + Integration

Responsibility:

Overall system architecture
Payment gateway integration
Module integration (frontend ↔ backend)

Key Contributions:

Integrated Razorpay payment gateway (mock/real)
Designed system flow (booking → payment → parking → release)
Connected frontend APIs with backend services
Implemented global exception handling
Ensured smooth communication across modules

Technologies & Concepts:

MVC Architecture
API Integration
Exception Handling (@ControllerAdvice)
🔹 Member 3 – Frontend (User Interface)

Responsibility:

Complete user interface design and interaction

Key Contributions:

Developed UI screens:
Login / Signup
Dashboard (search + parking areas)
Slot booking interface (grid visualization)
Payment page
Timer & billing screen
Implemented responsive design using HTML/CSS
Connected frontend with backend APIs using fetch/axios
Displayed real-time data (slots, billing, results)

Technologies & Concepts:

HTML, CSS, JavaScript
REST API integration
DOM manipulation
🔹 Member 4 – Database + Authentication

Responsibility:

Database design and management
User authentication system

Key Contributions:

Designed relational database schema:
Users
Parking Areas
Parking Slots
Bookings
Payments
Hotspots
Implemented login/signup functionality
Integrated password security using BCrypt hashing
Connected backend with database using JDBC
Managed data integrity using foreign keys and constraints

Technologies & Concepts:

SQL (MySQL/H2)
JDBC
Connection Pooling (HikariCP)
🔹 Member 5 – Smart Features (Navigation + Hotspots)

Responsibility:

Advanced intelligent features to enhance system capability

Key Contributions:

Implemented location-based parking search
Developed hotspot detection logic based on demand
Added navigation feature (Google Maps integration/simulation)
Built emergency vehicle priority system:
Allocates emergency slots
Overrides regular slots when necessary
Automated hotspot updates using scheduled tasks

Technologies & Concepts:

Java Collections (HashMap for demand tracking)
Haversine Formula (distance calculation)
ScheduledExecutorService
External API integration (Maps)

---

## 📜 License
This project is licensed under the MIT License.
