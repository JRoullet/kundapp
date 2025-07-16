USE coursemgmtdb;

-- Session de base avec Jenna Watkins (teacher_id = 1 depuis identitydb)
INSERT INTO session (
    teacher_id,
    subject,
    description,
    room_name,
    google_maps_link,
    available_spots,
    start_date_time,
    duration_minutes,
    credits_required
) VALUES (
 1,  -- teacher_id (Jenna Watkins)
 'KUNDALINI',
 'Session de Kundalini pour débutants. Découvrez l\'éveil de l\'énergie intérieure à travers des postures, la respiration et la méditation.',
 'Studio Zen Centre-ville',
 'https://maps.google.com/?q=Studio+Zen+Paris',
 12,  -- 12 places disponibles
 '2025-07-20 18:00:00',  -- Dimanche 20 juillet 2025 à 18h
 90,  -- 1h30
 1    -- 1 crédit requis
);

-- Session supplémentaire pour tester
INSERT INTO session (
    teacher_id,
    subject,
    description,
    room_name,
    google_maps_link,
    available_spots,
    start_date_time,
    duration_minutes,
    credits_required
) VALUES (
 1,  -- teacher_id (Jenna Watkins)
 'MEDITATION',
 'Méditation guidée en pleine conscience. Apprenez à calmer votre mental et à vous connecter à votre essence.',
 'Espace Lotus',
 'https://maps.google.com/?q=Espace+Lotus+Paris+75001',
 8,   -- 8 places disponibles
 '2025-07-22 19:30:00',  -- Mardi 22 juillet 2025 à 19h30
 60,  -- 1h
 1    -- 1 crédit requis
);