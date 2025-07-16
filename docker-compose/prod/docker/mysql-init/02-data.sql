SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET CHARACTER SET utf8mb4;

USE identitydb;

-- Clean existing data first
DELETE FROM users;
DELETE FROM address;

-- Reset auto-increment
ALTER TABLE address AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;

-- Insert addresses
INSERT INTO address (id, street, city, zip_code, country)
VALUES
    (1, '22 Jump Street', 'Las Vegas', '83500', 'USA'),
    (2, '15 Rue de la Paix', 'Paris', '75001', 'France');

-- ADMIN (no address)
INSERT INTO users (email, password, role, status)
VALUES ('admin@gmail.com', '$2a$12$QQFDdwrKmT3o8pBpHWLzw.CTuHUz49I/vHkyVi9UP3u267wURUy5G', 'ADMIN', true);

-- CLIENT (address_id = 1)
INSERT INTO users (email, password, role, status, first_name, last_name, phone_number, date_of_birth, address_id, subscription_status, credits)
VALUES ('user@gmail.com', '$2a$12$Z7E5G2ojHlZ7yfAr/5Bc7.4PAhZFSKvstTZC3eRwVHTIFdWWrb1yu', 'CLIENT', true, 'John', 'Doe', '514-5555-555', '2020-12-15', 1, 'NONE', 5);

-- TEACHER (address_id = 2)
INSERT INTO users (email, password, role, status, first_name, last_name, phone_number, address_id, biography)
VALUES ('teacher@gmail.com', '$2a$12$nAWuJmux7OG52rjGGkIwYuhzVVOdQ2bsQo7BvdYKLho/mG6h8gFiu', 'TEACHER', true, 'Jenna', 'Watkins', '512-422-887', 2, 'Experte en Kundalini et en yoga depuis 10 ans d\'expérience dans la méditation et l\'éveil spirituel.');

COMMIT;