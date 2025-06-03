-- creating identitydb users table

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status BOOLEAN DEFAULT true,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    profile_picture VARCHAR(255),
    date_of_birth DATE,
    street VARCHAR(255),
    city VARCHAR(100),
    zip_code VARCHAR(20),
    country VARCHAR(100),
    biography TEXT,
    subscription_status VARCHAR(50) DEFAULT 'NONE'
    );