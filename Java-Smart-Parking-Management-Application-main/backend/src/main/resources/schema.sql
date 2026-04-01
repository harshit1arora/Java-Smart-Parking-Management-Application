-- ============================================================
--  Smart Parking Management System — Schema (H2 / MySQL)
-- ============================================================

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS emergency_log;
DROP TABLE IF EXISTS hotspots;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS parking_slots;
DROP TABLE IF EXISTS parking_areas;
DROP TABLE IF EXISTS users;

-- -----------------------------------------------
-- 1. USERS
-- -----------------------------------------------
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    phone         VARCHAR(15),
    password_hash VARCHAR(255)  NOT NULL,
    role          VARCHAR(20)   NOT NULL DEFAULT 'user'
                  CHECK (role IN ('user','admin','emergency')),
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------
-- 2. PARKING AREAS
-- -----------------------------------------------
CREATE TABLE parking_areas (
    area_id     INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    location    VARCHAR(255),
    total_slots INT          NOT NULL DEFAULT 0,
    lat         DOUBLE,
    lng         DOUBLE,
    is_hotspot  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------
-- 3. PARKING SLOTS
-- -----------------------------------------------
CREATE TABLE parking_slots (
    slot_id     INT AUTO_INCREMENT PRIMARY KEY,
    area_id     INT         NOT NULL,
    slot_number VARCHAR(10) NOT NULL,
    slot_type   VARCHAR(20) NOT NULL DEFAULT 'regular'
                CHECK (slot_type IN ('regular','emergency')),
    status      VARCHAR(20) NOT NULL DEFAULT 'free'
                CHECK (UPPER(status) IN ('FREE', 'OCCUPIED', 'RESERVED')),
    updated_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (area_id) REFERENCES parking_areas(area_id) ON DELETE CASCADE
);

-- -----------------------------------------------
-- 4. BOOKINGS
-- -----------------------------------------------
CREATE TABLE bookings (
    booking_id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id           INT         NOT NULL,
    slot_id           INT         NOT NULL,
    start_time        TIMESTAMP,
    end_time          TIMESTAMP,
    duration_minutes  INT,
    final_amount      DECIMAL(10,2),
    status            VARCHAR(20) NOT NULL DEFAULT 'active'
                      CHECK (UPPER(status) IN ('ACTIVE','COMPLETED','CANCELLED','RESERVED')),
    created_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (slot_id) REFERENCES parking_slots(slot_id)
);

-- -----------------------------------------------
-- 5. PAYMENTS
-- -----------------------------------------------
CREATE TABLE payments (
    payment_id     INT AUTO_INCREMENT PRIMARY KEY,
    booking_id     INT            NOT NULL,
    amount         DECIMAL(10,2)  NOT NULL,
    method         VARCHAR(20)    NOT NULL DEFAULT 'UPI'
                   CHECK (UPPER(method) IN ('UPI','CARD','WALLET','RAZORPAY')),
    status         VARCHAR(20)    NOT NULL DEFAULT 'pending'
                   CHECK (UPPER(status) IN ('PENDING','SUCCESS','FAILED')),
    transaction_id VARCHAR(100),
    order_id       VARCHAR(100),
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

-- -----------------------------------------------
-- 6. HOTSPOTS
-- -----------------------------------------------
CREATE TABLE hotspots (
    hotspot_id   INT AUTO_INCREMENT PRIMARY KEY,
    area_id      INT  NOT NULL,
    peak_start   TIME,
    peak_end     TIME,
    demand_score INT  DEFAULT 0,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (area_id) REFERENCES parking_areas(area_id) ON DELETE CASCADE
);

-- -----------------------------------------------
-- 7. EMERGENCY LOG
-- -----------------------------------------------
CREATE TABLE emergency_log (
    log_id     INT AUTO_INCREMENT PRIMARY KEY,
    area_id    INT,
    slot_id    INT,
    user_id    INT,
    vehicle_id VARCHAR(50),
    action     VARCHAR(255),
    logged_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
