-- =========================================================
-- Local Explorer AI Pro - Core Database Schema (No-Login Build)
-- Run this in MySQL Workbench / CLI before starting the app
-- =========================================================

CREATE DATABASE IF NOT EXISTS local_explorer_ai;
USE local_explorer_ai;

-- Kept minimal since there's no login screen - just gives expenses/reviews
-- a place to point at. One row is seeded below as the "current user".
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL
);

CREATE TABLE places (
    place_id      INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    category      VARCHAR(50) NOT NULL,
    latitude      DOUBLE NOT NULL,
    longitude     DOUBLE NOT NULL,
    avg_rating    DECIMAL(2,1) DEFAULT 0.0,
    price_level   INT DEFAULT 1,
    student_discount BOOLEAN DEFAULT FALSE
);

CREATE TABLE reviews (
    review_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    place_id      INT NOT NULL,
    rating        INT CHECK (rating BETWEEN 1 AND 5),
    comment       TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (place_id) REFERENCES places(place_id) ON DELETE CASCADE
);

CREATE TABLE expenses (
    expense_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    category      VARCHAR(50) NOT NULL,
    amount        DECIMAL(10,2) NOT NULL,
    note          VARCHAR(255),
    spent_on      DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE emergency_contacts (
    contact_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    label         VARCHAR(50) NOT NULL,
    phone         VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Default "current user" the app uses since there's no login screen.
-- Its user_id (1) is hardcoded as DEFAULT_USER_ID in BudgetScreen.java.
INSERT INTO users (user_id, full_name) VALUES (1, 'Shiva');

-- Sample seed data so the app has something to show on first run
INSERT INTO places (name, category, latitude, longitude, avg_rating, price_level, student_discount) VALUES
('Annapoorna Mess', 'Restaurant', 11.0168, 76.9558, 4.3, 1, TRUE),
('Sri Krishna Sweets', 'Restaurant', 11.0028, 76.9615, 4.1, 2, FALSE),
('Ananda Bhavan', 'Restaurant', 11.0176, 76.9674, 4.2, 1, TRUE),
('Coimbatore Medical College Hospital', 'Hospital', 11.0526, 76.9605, 4.0, 1, FALSE),
('Apollo Pharmacy - RS Puram', 'Pharmacy', 11.0080, 76.9500, 4.2, 1, FALSE),
('Central Library Coimbatore', 'Library', 11.0018, 76.9629, 4.5, 1, TRUE),
('Gandhipuram Bus Stand', 'Bus Stop', 11.0183, 76.9670, 3.8, 1, FALSE),
('Isha Yoga Center', 'Tourist Spot', 11.0364, 76.7605, 4.7, 1, FALSE),
('Marudhamalai Temple', 'Tourist Spot', 11.0530, 76.8749, 4.6, 1, FALSE),
('Perur Pateeswarar Temple', 'Tourist Spot', 10.9736, 76.8988, 4.5, 1, FALSE),
('VOC Park and Zoo', 'Tourist Spot', 11.0027, 76.9605, 4.2, 1, TRUE),
('Black Thunder', 'Tourist Spot', 11.2990, 76.9400, 4.3, 3, TRUE),
('Brookefields Mall', 'Mall', 11.0176, 76.9642, 4.4, 2, FALSE),
('Fun Republic Mall', 'Mall', 11.0347, 76.9707, 4.3, 2, FALSE);
