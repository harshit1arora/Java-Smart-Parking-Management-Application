-- ============================================================
--  Smart Parking Management System — Seed Data
-- ============================================================

-- -----------------------------------------------
-- USERS: 5 regular + 1 admin + 1 emergency
-- password_hash = BCrypt of 'password123' (mock plain hash for demo)
-- -----------------------------------------------
INSERT INTO users (name, email, phone, password_hash, role) VALUES
('Aarav Sharma',    'aarav@example.com',     '9876543210', '$2a$10$mockHashAarav',   'user'),
('Priya Mehta',     'priya@example.com',     '9012345678', '$2a$10$mockHashPriya',   'user'),
('Rohan Gupta',     'rohan@example.com',     '8123456789', '$2a$10$mockHashRohan',   'user'),
('Sneha Patel',     'sneha@example.com',     '7012345678', '$2a$10$mockHashSneha',   'user'),
('Vikram Singh',    'vikram@example.com',    '6098765432', '$2a$10$mockHashVikram',  'user'),
('Admin User',      'admin@smartpark.com',   '9999999999', '$2a$10$mockHashAdmin',   'admin'),
('Emergency Unit',  'emergency@gov.in',      '1800112345', '$2a$10$mockHashEmerg',   'emergency');

-- -----------------------------------------------
-- PARKING AREAS: 3 areas
-- -----------------------------------------------
INSERT INTO parking_areas (name, location, total_slots, lat, lng, is_hotspot) VALUES
('Central Mall Parking',    'MG Road, Bangalore',        20, 12.9716,  77.5946,  TRUE),
('Tech Park Parking',       'Whitefield, Bangalore',     20, 12.9698,  77.7499,  FALSE),
('Airport Parking Zone',    'Devanahalli, Bangalore',    20, 13.1989,  77.7068,  FALSE);

-- -----------------------------------------------
-- PARKING SLOTS: 20 per area (18 regular + 2 emergency)
-- Area 1 (Central Mall) — area_id = 1
-- -----------------------------------------------
INSERT INTO parking_slots (area_id, slot_number, slot_type, status) VALUES
-- Regular slots
(1,'A-01','regular','occupied'),
(1,'A-02','regular','occupied'),
(1,'A-03','regular','reserved'),
(1,'A-04','regular','free'),
(1,'A-05','regular','free'),
(1,'A-06','regular','occupied'),
(1,'A-07','regular','free'),
(1,'A-08','regular','occupied'),
(1,'A-09','regular','free'),
(1,'A-10','regular','occupied'),
(1,'A-11','regular','reserved'),
(1,'A-12','regular','free'),
(1,'A-13','regular','occupied'),
(1,'A-14','regular','free'),
(1,'A-15','regular','free'),
(1,'A-16','regular','occupied'),
(1,'A-17','regular','free'),
(1,'A-18','regular','free'),
-- Emergency slots
(1,'AE-01','emergency','free'),
(1,'AE-02','emergency','free');

-- Area 2 (Tech Park) — area_id = 2
INSERT INTO parking_slots (area_id, slot_number, slot_type, status) VALUES
(2,'B-01','regular','free'),
(2,'B-02','regular','free'),
(2,'B-03','regular','free'),
(2,'B-04','regular','free'),
(2,'B-05','regular','occupied'),
(2,'B-06','regular','free'),
(2,'B-07','regular','free'),
(2,'B-08','regular','free'),
(2,'B-09','regular','free'),
(2,'B-10','regular','free'),
(2,'B-11','regular','free'),
(2,'B-12','regular','occupied'),
(2,'B-13','regular','free'),
(2,'B-14','regular','free'),
(2,'B-15','regular','free'),
(2,'B-16','regular','free'),
(2,'B-17','regular','free'),
(2,'B-18','regular','free'),
(2,'BE-01','emergency','free'),
(2,'BE-02','emergency','occupied');

-- Area 3 (Airport) — area_id = 3
INSERT INTO parking_slots (area_id, slot_number, slot_type, status) VALUES
(3,'C-01','regular','free'),
(3,'C-02','regular','occupied'),
(3,'C-03','regular','free'),
(3,'C-04','regular','free'),
(3,'C-05','regular','free'),
(3,'C-06','regular','free'),
(3,'C-07','regular','occupied'),
(3,'C-08','regular','free'),
(3,'C-09','regular','free'),
(3,'C-10','regular','free'),
(3,'C-11','regular','free'),
(3,'C-12','regular','free'),
(3,'C-13','regular','free'),
(3,'C-14','regular','occupied'),
(3,'C-15','regular','free'),
(3,'C-16','regular','free'),
(3,'C-17','regular','free'),
(3,'C-18','regular','free'),
(3,'CE-01','emergency','free'),
(3,'CE-02','emergency','free');

-- -----------------------------------------------
-- HOTSPOTS: Central Mall is a hotspot
-- -----------------------------------------------
INSERT INTO hotspots (area_id, peak_start, peak_end, demand_score) VALUES
(1, '10:00:00', '14:00:00', 85),
(1, '17:00:00', '21:00:00', 92),
(2, '09:00:00', '11:00:00', 45);

-- -----------------------------------------------
-- SAMPLE BOOKING (for timer.html demo)
-- -----------------------------------------------
INSERT INTO bookings (user_id, slot_id, start_time, duration_minutes, status) VALUES
(1, 1, CURRENT_TIMESTAMP, 60, 'active');

-- SAMPLE PAYMENT
INSERT INTO payments (booking_id, amount, method, status, transaction_id, order_id) VALUES
(1, 20.00, 'UPI', 'success', 'TXN123456789', 'ORDER_987654');
