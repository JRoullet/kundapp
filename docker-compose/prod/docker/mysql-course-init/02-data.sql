SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET CHARACTER SET utf8mb4;

USE coursemgmtdb;

INSERT INTO session (
    teacher_id,
    teacher_first_name,
    teacher_last_name,
    subject,
    status,
    description,
    room_name,
    postal_code,
    google_maps_link,
    is_online,
    zoom_link,
    available_spots,
    start_date_time,
    duration_minutes,
    credits_required,
    bring_your_mattress
) VALUES (
             3,
             'Jenna',
             'Watkins',
             -- FIN NOUVEAUX CHAMPS
             'KUNDALINI',
             'COMPLETED',
             'Session de Kundalini pour débutants. Découvrez l\'éveil de l\'énergie intérieure à travers des postures, la respiration et la méditation.',
             'Studio Zen Centre-ville',
             '75001',
             'https://maps.google.com/?q=Studio+Zen+Paris',
             FALSE,
             NULL,
             12,
             '2025-07-20 18:00:00',
             90,
             1,
             TRUE
         ),
         (
             3,
             'Jenna',
             'Watkins',
             'MEDITATION',
             'SCHEDULED',
             'Méditation guidée en pleine conscience en ligne. Connectez-vous depuis chez vous pour une séance de relaxation profonde.',
             NULL,
             NULL,
             NULL,
             TRUE,
             'https://zoom.us/j/123456789?pwd=abcdef123456',
             15,
             '2025-07-22 19:30:00',
             60,
             1,
             FALSE
         );