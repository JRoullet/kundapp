SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS coursemgmtdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE coursemgmtdb;

CREATE TABLE session (
             id BIGINT AUTO_INCREMENT PRIMARY KEY,
             teacher_id BIGINT NOT NULL,
             subject ENUM('YOGA', 'KUNDALINI', 'PILATES', 'SOIN_ENERGETIQUE', 'MEDITATION') CHARACTER SET utf8mb4,
             status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
             description TEXT CHARACTER SET utf8mb4,
             room_name VARCHAR(255) CHARACTER SET utf8mb4,
             postal_code VARCHAR(10) CHARACTER SET utf8mb4,
             google_maps_link TEXT CHARACTER SET utf8mb4,
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