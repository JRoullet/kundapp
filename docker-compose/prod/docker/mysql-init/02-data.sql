-- ADMIN
INSERT INTO users (email, password, role, status)
VALUES ('admin@gmail.com', '$2a$12$QQFDdwrKmT3o8pBpHWLzw.CTuHUz49I/vHkyVi9UP3u267wURUy5G', 'ADMIN', true);

-- CLIENT
INSERT INTO users (email, password, role, status, first_name, last_name, phone_number, date_of_birth, street, city, zip_code, country, subscription_status)
VALUES ('user@gmail.com', '$2a$12$Z7E5G2ojHlZ7yfAr/5Bc7.4PAhZFSKvstTZC3eRwVHTIFdWWrb1yu', 'CLIENT', true, 'John', 'Doe', '514-5555-555', '2020-12-15', '22 Jump Street', 'Las Vegas', '83500', 'USA', 'NONE');

-- TEACHER
INSERT INTO users (email, password, role, status, first_name, last_name, phone_number, biography)
VALUES ('teacher@gmail.com', '$2a$12$nAWuJmux7OG52rjGGkIwYuhzVVOdQ2bsQo7BvdYKLho/mG6h8gFiu', 'TEACHER', true, 'Jenna', 'Watkins', '512-422-887', 'lalalalala lorem ipsum');