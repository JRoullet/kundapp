SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS coursemgmtdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE coursemgmtdb;

CREATE TABLE session (
             id BIGINT AUTO_INCREMENT PRIMARY KEY,
             teacher_id BIGINT NOT NULL,
             teacher_first_name VARCHAR(100) CHARACTER SET utf8mb4,
             teacher_last_name VARCHAR(100) CHARACTER SET utf8mb4,
             subject ENUM('YOGA', 'KUNDALINI', 'PILATES', 'SOIN_ENERGETIQUE', 'MEDITATION') CHARACTER SET utf8mb4,
             status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
             description TEXT CHARACTER SET utf8mb4,
             room_name VARCHAR(255) CHARACTER SET utf8mb4,
             postal_code VARCHAR(10) CHARACTER SET utf8mb4,
             google_maps_link TEXT CHARACTER SET utf8mb4,
             is_online BOOLEAN NOT NULL DEFAULT FALSE,
             zoom_link TEXT CHARACTER SET utf8mb4,
             available_spots INTEGER,
             start_date_time DATETIME,
             duration_minutes INTEGER,
             credits_required INTEGER DEFAULT 1,
             bring_your_mattress BOOLEAN DEFAULT FALSE,
             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE session_participants (
              session_id BIGINT,
              participant_id BIGINT,
              PRIMARY KEY (session_id, participant_id),
              FOREIGN KEY (session_id) REFERENCES session(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE reservation (
             id VARCHAR(36) PRIMARY KEY,
             user_id BIGINT NOT NULL,
             session_id BIGINT NOT NULL,
             amount INTEGER NOT NULL,
             status ENUM('RESERVED', 'CONFIRMED', 'CANCELLED', 'EXPIRED') DEFAULT 'RESERVED',
             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             expires_at TIMESTAMP NOT NULL,
             confirmed_at TIMESTAMP NULL,

             FOREIGN KEY (session_id) REFERENCES session(id) ON DELETE CASCADE,
             INDEX idx_user_session (user_id, session_id),
             INDEX idx_expires_at (expires_at, status)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;