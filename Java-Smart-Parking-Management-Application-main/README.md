# 🚗 Java Smart Parking Management Application

The **Smart Parking Management Application** is a full-stack Java-based enterprise solution designed to solve modern urban parking challenges. It features AI-driven hotspot detection, real-time slot tracking, automated billing, and secure end-to-end payment integration.

Built with a robust **Spring Boot** backend and a premium **Vanilla JavaScript + HTML/CSS** frontend, the system optimizes parking space utilization while providing a seamless, high-performance experience for users and administrators.

---

## 🚀 Key Features

### 🅿️ Advanced Parking Logic
- ✅ **Real-Time Slot Availability** – Live grid visualization of parking slots (Free, Occupied, Reserved).
- ✅ **Dynamic Pricing & Billing** – Automated fee calculation (₹10/30 min or demand-based) based on live session timing.
- ✅ **Automatic Release** – Real-time status updates and session termination with final billing.
- ✅ **Authorization & Security** – Role-based access control (User, Admin, Emergency) with secure Firebase-backed authentication.

### 💳 Secure Payment Integration
- ✅ **Integrated Payment Gateway** – Unified checkout via **Razorpay** for UPI, Cards, and Wallets.
- ✅ **Transaction Verification** – Backend-level signature verification to ensure data integrity and prevent fraud.
- ✅ **Automated Record Keeping** – Real-time payment tracking and booking status updates in the SQL database.

### 🔥 Smart Features & Analytics
- ✅ **Location-Based Search** – Proximity-based area discovery using the **Haversine formula** in Java to calculate distances and return the top 5 nearest locations.
- ✅ **Hotspot Detection Logic** – Real-time demand tracking using `HashMap<Integer, Integer>` to monitor active bookings. If occupancy exceeds **70%**, the area is flagged as a hotspot via a `ScheduledExecutorService` running every 5 minutes.
- ✅ **Navigation Simulation** – One-click integration that generates a direct **Google Maps URL** (`https://www.google.com/maps/dir/?api=1&destination={lat},{lng}`) for seamless arrival.
- ✅ **Emergency Vehicle Priority** – Automated scan for dedicated `emergency` slots. If unavailable, the system identifies the nearest regular slot and enables a **force-release** mechanism if occupied for > 2 hours, logging all overrides in an `emergency_log` table.

---

## 🏗️ Project Structure

```bash
smartparking/
├── backend/                        # Spring Boot Application
│   ├── src/main/java/com/smartparking/
│   │   ├── controller/             # REST API Endpoints (Parking, Booking, Payments)
│   │   ├── service/                # Business Logic (Timer, Billing, Verification)
│   │   ├── model/                  # POJOs / Database Entities
│   │   ├── repository/             # Data Access Layer (JDBC/JPA)
│   │   ├── util/                   # Helper functions (Hotspot, Distance logic)
│   │   └── SmartParkingApp.java    # Main Entry Point
│   ├── src/main/resources/
│   │   ├── application.properties  # DB & Razorpay Config (Protected)
│   │   └── schema.sql              # Database Initialization Script
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

## 🛠️ Setup Instructions (Windows)

### 🔧 Prerequisites
- **Java JDK 17+**
- **Maven 3.6+**
- **MySQL 8.0+**
- **Python 3.x** (for serving the frontend)

### ⚙️ Backend Setup (Spring Boot)
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Configure your MySQL credentials and Razorpay keys in `src/main/resources/application.properties`.
3. Build and run the server:
   ```bash
   mvn spring-boot:run
   ```
The API server will start at `http://localhost:8080`.

### 🌐 Frontend Setup
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

## 📊 Database Architecture

The system utilizes a relational schema for high data integrity:
1. **`users`**: Profiles with role-based access control (User, Admin, Emergency).
2. **`parking_areas`**: Location data and live hotspot tracking.
3. **`parking_slots`**: Individual slot states (Regular vs Emergency).
4. **`bookings`**: Session records with precise entry/exit timestamps.
5. **`payments`**: Transaction logs linked to Razorpay `order_id`.
6. **`hotspots`**: Demand scoring logic for peak-hour pricing.

---

## 🤝 System Design & MVC Flow
- **Architecture**: Clear separation of Controllers, Services, and Repositories.
- **Concurrency**: Uses `ScheduledExecutorService` for live status refreshing and hotspot detection every 5 minutes.
- **RESTful APIs**: Standardized JSON responses for all frontend interactions.
- **Global Error Handling**: Centralized exception mapping with custom error messages.

---

## 📜 License
This project is licensed under the MIT License.
