-- Run this against your EXISTING local_explorer_ai database to add
-- the new tourist spots / mall / hotel without re-running the full schema.
USE local_explorer_ai;

INSERT INTO places (name, category, latitude, longitude, avg_rating, price_level, student_discount) VALUES
('Ananda Bhavan', 'Restaurant', 11.0176, 76.9674, 4.2, 1, TRUE),
('Isha Yoga Center', 'Tourist Spot', 11.0364, 76.7605, 4.7, 1, FALSE),
('Marudhamalai Temple', 'Tourist Spot', 11.0530, 76.8749, 4.6, 1, FALSE),
('Perur Pateeswarar Temple', 'Tourist Spot', 10.9736, 76.8988, 4.5, 1, FALSE),
('VOC Park and Zoo', 'Tourist Spot', 11.0027, 76.9605, 4.2, 1, TRUE),
('Black Thunder', 'Tourist Spot', 11.2990, 76.9400, 4.3, 3, TRUE),
('Brookefields Mall', 'Mall', 11.0176, 76.9642, 4.4, 2, FALSE),
('Fun Republic Mall', 'Mall', 11.0347, 76.9707, 4.3, 2, FALSE);
