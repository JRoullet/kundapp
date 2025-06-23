-- creating identitydb users table
USE identitydb;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS address;

-- Create Address table first (referenced table)
CREATE TABLE address (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         street VARCHAR(255),
                         city VARCHAR(255),
                         zip_code VARCHAR(10),
                         country VARCHAR(255)
);

-- Create Users table with foreign key to Address
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role ENUM('ADMIN','CLIENT','TEACHER') NOT NULL,
                       created_at DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
                       status BIT(1) DEFAULT 1,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       phone_number VARCHAR(255),
                       date_of_birth DATE,
                       address_id BIGINT,
                       biography VARCHAR(1000),
                       subscription_status VARCHAR(50) DEFAULT 'NONE',
                       credits INT,

    -- Foreign key constraint
                       CONSTRAINT fk_user_address FOREIGN KEY (address_id) REFERENCES address(id)
);