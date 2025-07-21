SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET CHARACTER SET utf8mb4;

USE coursemgmtdb;

INSERT INTO session (
    teacher_id,
    subject,
    description,
    room_name,
    postal_code,
    google_maps_link,
    available_spots,
    start_date_time,
    duration_minutes,
    credits_required,
    bring_your_mattress
) VALUES (
             3,  -- teacher_id (Jenna Watkins)
             'KUNDALINI',
             'Session de Kundalini pour débutants. Découvrez l\'éveil de l\'énergie intérieure à travers des postures, la respiration et la méditation.',
             'Studio Zen Centre-ville',
             '75001',
             'https://maps.google.com/?q=Studio+Zen+Paris',
             12,
             '2025-07-20 18:00:00',
             90,
             1,
             FALSE
         ),
         (
             3,  -- teacher_id (Jenna Watkins)
             'MEDITATION',
             'Méditation guidée en pleine conscience. Apprenez à calmer votre mental et à vous connecter à votre essence.',
             'Espace Lotus',
             '75004',
             'https://maps.google.com/?q=Espace+Lotus+Paris+75001',
             8,
             '2025-07-22 19:30:00',
             60,
             1,
             TRUE
         );