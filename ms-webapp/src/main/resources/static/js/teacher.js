// Teacher JavaScript - Session management
// Depends on common.js which must be loaded first

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Teacher page loaded');

    // Set minimum date to today for session creation
    const today = new Date().toISOString().split('T')[0];
    const sessionDate = document.getElementById('sessionDate');
    if (sessionDate) {
        sessionDate.setAttribute('min', today);
    }

    // Session form validation on submit
    const sessionForm = document.getElementById('sessionForm');
    if (sessionForm) {
        sessionForm.addEventListener('submit', function(e) {
            if (!validateSessionForm()) {
                e.preventDefault();
                return false;
            }

            // Combine date and time into startDateTime
            const dateValue = document.getElementById('sessionDate').value;
            const timeValue = document.getElementById('sessionTime').value;

            if (dateValue && timeValue) {
                const startDateTime = dateValue + 'T' + timeValue;

                // Create hidden input for startDateTime
                let startDateTimeInput = document.getElementById('startDateTime');
                if (!startDateTimeInput) {
                    startDateTimeInput = document.createElement('input');
                    startDateTimeInput.type = 'hidden';
                    startDateTimeInput.name = 'startDateTime';
                    startDateTimeInput.id = 'startDateTime';
                    sessionForm.appendChild(startDateTimeInput);
                }
                startDateTimeInput.value = startDateTime;
            }
        });
    }
});

// ========================================
// SESSION MODAL MANAGEMENT
// ========================================

function openCreateSessionModal() {
    console.log('Opening create session modal...');

    const modalTitle = document.getElementById('sessionModalTitle');
    const sessionForm = document.getElementById('sessionForm');

    if (!modalTitle || !sessionForm) {
        console.error('Modal elements not found');
        return;
    }

    modalTitle.textContent = 'Créer une séance';
    sessionForm.reset();
    sessionForm.action = '/teacher/session/create';

    // Set default values
    const today = new Date().toISOString().split('T')[0];
    const sessionDate = document.getElementById('sessionDate');
    if (sessionDate) {
        sessionDate.setAttribute('min', today);
    }

    const availableSpots = document.getElementById('sessionAvailableSpots');
    if (availableSpots) {
        availableSpots.value = '10';
    }

    const duration = document.getElementById('sessionDurationMinutes');
    if (duration) {
        duration.value = '60';
    }

    const creditsRequired = document.getElementById('sessionCreditsRequired');
    if (creditsRequired) {
        creditsRequired.value = '1';
    }

    const modal = new mdb.Modal(document.getElementById('sessionModal'));
    modal.show();
}

function openEditSessionModal(sessionId) {
    document.getElementById('sessionModalTitle').textContent = 'Modifier la séance';
    document.getElementById('sessionForm').action = '/teacher/session/' + sessionId + '/update';

    // Fetch session data
    fetch('/teacher/session/' + sessionId + '/data')
        .then(response => {
            if (!response.ok) {
                throw new Error('Session not found');
            }
            return response.json();
        })
        .then(session => {
            // Populate form fields
            document.getElementById('sessionSubject').value = session.subject || '';
            document.getElementById('sessionDescription').value = session.description || '';
            document.getElementById('sessionRoomName').value = session.roomName || '';
            document.getElementById('sessionPostalCode').value = session.postalCode || '';
            document.getElementById('sessionGoogleMapsLink').value = session.googleMapsLink || '';

            // Handle startDateTime splitting
            if (session.startDateTime) {
                const startDate = new Date(session.startDateTime);
                document.getElementById('sessionDate').value = startDate.toISOString().split('T')[0];
                document.getElementById('sessionTime').value = startDate.toTimeString().split(' ')[0].substring(0,5);
            }

            document.getElementById('sessionDurationMinutes').value = session.durationMinutes || '';
            document.getElementById('sessionAvailableSpots').value = session.availableSpots || '';
            document.getElementById('sessionCreditsRequired').value = session.creditsRequired || '';
            document.getElementById('sessionBringYourMattress').checked = session.bringYourMattress || false;

            const modal = new mdb.Modal(document.getElementById('sessionModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error loading session data:', error);
            toastSystem.error('Erreur', 'Impossible de charger les données de la session');
        });
}

// ========================================
// SESSION ACTIONS
// ========================================

function confirmCancelSession(sessionId) {
    showConfirmation(
        'Annuler la séance',
        'Êtes-vous sûr de vouloir annuler cette séance ? Cette action est irréversible et les participants seront notifiés.',
        function() {
            submitActionWithParams('/teacher/session/cancel', {sessionId:sessionId});
        }
    );
}

// ========================================
// FORM VALIDATION
// ========================================

function validateSessionForm() {
    const form = document.getElementById('sessionForm');
    const formData = new FormData(form);

    // Subject validation (required)
    const subject = formData.get('subject');
    if (!subject) {
        toastSystem.error('Erreur de validation', 'Le type de cours est obligatoire');
        return false;
    }

    // Description validation (required)
    const description = formData.get('description');
    if (!description || description.trim().length === 0) {
        toastSystem.error('Erreur de validation', 'La description est obligatoire');
        return false;
    }

    // Room name validation (required)
    const roomName = formData.get('roomName');
    if (!roomName || roomName.trim().length === 0) {
        toastSystem.error('Erreur de validation', 'Le nom de la salle/lieu est obligatoire');
        return false;
    }

    // Postal code validation (required with pattern)
    const postalCode = formData.get('postalCode');
    const postalCodeRegex = /^(0[1-9]|[1-8][0-9]|9[0-8])\d{3}$/;
    if (!postalCode || !postalCodeRegex.test(postalCode)) {
        toastSystem.error('Erreur de validation', 'Le code postal doit être un code postal français valide (ex: 75001)');
        return false;
    }

    // Google Maps link validation (optional but if present must be valid)
    const googleMapsLink = formData.get('googleMapsLink');
    if (googleMapsLink && googleMapsLink.trim() !== '') {
        // Flexible regex accepting all Google Maps formats
        const googleMapsRegex = /^https:\/\/(maps\.google\.(com|fr)|maps\.app\.goo\.gl)\/.*/;
        if (!googleMapsRegex.test(googleMapsLink)) {
            toastSystem.error('Erreur de validation', 'Le lien doit être un lien Google Maps valide');
            return false;
        }
    }

    // Date and time validation (combined as startDateTime)
    const sessionDate = formData.get('date');
    const sessionTime = formData.get('time');

    if (!sessionDate || !sessionTime) {
        toastSystem.error('Erreur de validation', 'La date et l\'heure sont obligatoires');
        return false;
    }

    const sessionDateTime = new Date(sessionDate + 'T' + sessionTime);
    const now = new Date();

    if (sessionDateTime <= now) {
        toastSystem.error('Erreur de validation', 'La session doit être programmée dans le futur');
        return false;
    }

    // Available spots validation
    const availableSpots = parseInt(formData.get('availableSpots'));
    if (!availableSpots || availableSpots < 1 || availableSpots > 50) {
        toastSystem.error('Erreur de validation', 'Le nombre de places doit être entre 1 et 50');
        return false;
    }

    // Duration validation
    const duration = parseInt(formData.get('durationMinutes'));
    if (!duration || duration < 15 || duration > 300) {
        toastSystem.error('Erreur de validation', 'La durée doit être entre 15 et 300 minutes');
        return false;
    }

    // Credits required validation
    const creditsRequired = parseInt(formData.get('creditsRequired'));
    if (!creditsRequired || creditsRequired < 1 || creditsRequired > 10) {
        toastSystem.error('Erreur de validation', 'Le nombre de crédits requis doit être entre 1 et 10');
        return false;
    }

    return true;
}