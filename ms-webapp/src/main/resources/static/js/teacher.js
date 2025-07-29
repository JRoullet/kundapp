// Teacher JavaScript - Session management
// Depends on common.js which must be loaded first

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Teacher page loaded');

    // Set minimum datetime to now for both creation and update
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset()); // Adjust for timezone
    const minDateTime = now.toISOString().slice(0, 16); // Format: YYYY-MM-DDTHH:MM

    const sessionStartDateTime = document.getElementById('sessionStartDateTime');
    if (sessionStartDateTime) {
        sessionStartDateTime.setAttribute('min', minDateTime);
    }

    // Session form validation on submit
    const sessionForm = document.getElementById('sessionForm');
    if (sessionForm) {
        sessionForm.addEventListener('submit', function(e) {
            if (!validateSessionForm()) {
                e.preventDefault();
                return false;
            }
        });
    }

    // Session update form validation on submit
    const sessionUpdateForm = document.getElementById('sessionUpdateForm');
    if (sessionUpdateForm) {
        sessionUpdateForm.addEventListener('submit', function(e) {
            if (!validateSessionUpdateForm()) {
                e.preventDefault();
                return false;
            }
        });
    }

    initializeSessionTypeHandlers();
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
    sessionForm.action = '/teacher/sessions/create';

    // Set minimum datetime to now
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    const minDateTime = now.toISOString().slice(0, 16);

    const sessionStartDateTime = document.getElementById('sessionStartDateTime');
    if (sessionStartDateTime) {
        sessionStartDateTime.setAttribute('min', minDateTime);
    }

    // Set default values
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

// ========================================
// SESSION UPDATE MODAL MANAGEMENT
// ========================================

function openEditSessionModal(sessionId) {
    console.log('Opening edit session modal for session:', sessionId);

    // Configure form action
    document.getElementById('sessionUpdateForm').action = '/teacher/sessions/' + sessionId + '/update';
    document.getElementById('sessionUpdateId').value = sessionId;

    // Fetch session data from endpoint
    fetch('/teacher/sessions/' + sessionId + '/details')
        .then(response => {
            if (!response.ok) {
                throw new Error('Session not found');
            }
            return response.json();
        })
        .then(session => {
            console.log('Session data loaded:', session);

            // Populate basic fields
            document.getElementById('sessionUpdateSubject').value = session.subject || '';
            document.getElementById('sessionUpdateDescription').value = session.description || '';
            document.getElementById('sessionUpdateRoomName').value = session.roomName || '';
            document.getElementById('sessionUpdatePostalCode').value = session.postalCode || '';
            document.getElementById('sessionUpdateGoogleMapsLink').value = session.googleMapsLink || '';
            document.getElementById('sessionUpdateAvailableSpots').value = session.availableSpots || '';
            document.getElementById('sessionUpdateCreditsRequired').value = session.creditsRequired || '';
            document.getElementById('sessionUpdateDurationMinutes').value = session.durationMinutes || '';

            // Handle checkbox
            document.getElementById('sessionUpdateBringYourMattress').checked = session.bringYourMattress || false;

            if (session.startDateTime) {
                const datetimeLocal = session.startDateTime.slice(0, 16);
                document.getElementById('sessionUpdateStartDateTime').value = datetimeLocal;
            }

            // Update participants count
            const participantsCount = session.registeredParticipants || 0;
            document.getElementById('sessionUpdateParticipantsCount').textContent =
                `${participantsCount} participant(s) inscrit(s)`;

            // Configure cancel button
            const cancelBtn = document.getElementById('sessionUpdateCancelBtn');
            cancelBtn.onclick = () => confirmCancelSessionFromUpdateModal(sessionId);

            // Show modal
            const modal = new mdb.Modal(document.getElementById('sessionUpdateModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error loading session data:', error);
            toastSystem.error('Erreur', 'Impossible de charger les données de la session');
        });
}

function confirmCancelSessionFromUpdateModal(sessionId) {
    showConfirmation(
        'Annuler la séance',
        'Êtes-vous sûr de vouloir annuler cette séance ? Cette action est irréversible et les participants seront notifiés.',
        function() {
            // Close update modal first
            const updateModal = mdb.Modal.getInstance(document.getElementById('sessionUpdateModal'));
            updateModal.hide();

            // Submit cancellation request
            submitActionWithParams('/teacher/sessions/cancel', {sessionId: sessionId});
        }
    );
}

// ========================================
// FORM VALIDATION - UNIFIED
// ========================================

function validateSessionCommonFields(formData) {
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

    // Conditional validation around session type
    const isOnline = formData.get('isOnline') === 'true';

    if (isOnline) {
        // Online session validation
        const zoomLink = formData.get('zoomLink');
        if (!zoomLink || zoomLink.trim() === '') {
            toastSystem.error('Erreur de validation', 'Le lien Zoom est obligatoire pour une session en ligne');
            return false;
        }

        const zoomRegex = /^https:\/\/(.*\.)?zoom\.(us|com)\/j\/\d+.*$/;
        if (!zoomRegex.test(zoomLink)) {
            toastSystem.error('Erreur de validation', 'Le lien doit être un lien Zoom valide');
            return false;
        }
    } else {
        // Irl session validation
        const roomName = formData.get('roomName');
        if (!roomName || roomName.trim().length === 0) {
            toastSystem.error('Erreur de validation', 'Le nom de la salle/lieu est obligatoire');
            return false;
        }

        const postalCode = formData.get('postalCode');
        const postalCodeRegex = /^(0[1-9]|[1-8][0-9]|9[0-8])\d{3}$/;
        if (!postalCode || !postalCodeRegex.test(postalCode)) {
            toastSystem.error('Erreur de validation', 'Le code postal doit être un code postal français valide (ex: 75001)');
            return false;
        }

        // Google Maps link validation (optional but if present must be valid)
        const googleMapsLink = formData.get('googleMapsLink');
        if (googleMapsLink && googleMapsLink.trim() !== '') {
            const googleMapsRegex = /^https:\/\/(maps\.google\.(com|fr)|maps\.app\.goo\.gl)\/.*/;
            if (!googleMapsRegex.test(googleMapsLink)) {
                toastSystem.error('Erreur de validation', 'Le lien doit être un lien Google Maps valide');
                return false;
            }
        }
    }

    // Start date time validation (unified for both forms)
    const startDateTime = formData.get('startDateTime');
    if (!startDateTime) {
        toastSystem.error('Erreur de validation', 'La date et l\'heure sont obligatoires');
        return false;
    }

    const sessionDateTime = new Date(startDateTime);
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

function validateSessionForm() {
    const form = document.getElementById('sessionForm');
    const formData = new FormData(form);
    return validateSessionCommonFields(formData);
}

function validateSessionUpdateForm() {
    const form = document.getElementById('sessionUpdateForm');
    const formData = new FormData(form);
    return validateSessionCommonFields(formData);
}

// ========================================
// SESSION TYPE MANAGEMENT
// ========================================

function initializeSessionTypeHandlers() {
    const sessionTypeRadios = document.querySelectorAll('input[name="sessionType"]');
    const isOnlineHidden = document.getElementById('sessionIsOnline');

    sessionTypeRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            const isOnline = this.value === 'true';
            isOnlineHidden.value = isOnline;
            toggleSessionFields(isOnline);
        });
    });
}

function toggleSessionFields(isOnline) {
    const irlFields = document.getElementById('irlFields');
    const onlineFields = document.getElementById('onlineFields');
    const materialCompleteSection  = document.getElementById('materialCompleteSection');
    const locationTitle = document.getElementById('locationSectionTitle');

    const roomNameInput = document.getElementById('sessionRoomName');
    const postalCodeInput = document.getElementById('sessionPostalCode');
    const zoomLinkInput = document.getElementById('sessionZoomLink');

    if (isOnline) {
        irlFields.style.display = 'none';
        onlineFields.style.display = 'block';
        materialCompleteSection.style.display = 'none';
        locationTitle.textContent = 'Paramètres de la session en ligne';

        roomNameInput.removeAttribute('required');
        postalCodeInput.removeAttribute('required');
        zoomLinkInput.setAttribute('required', 'required');

        roomNameInput.value = '';
        postalCodeInput.value = '';
        document.getElementById('sessionGoogleMapsLink').value = '';
        document.getElementById('sessionBringYourMattress').checked = false;

    } else {
        irlFields.style.display = 'block';
        onlineFields.style.display = 'none';
        materialCompleteSection.style.display = 'block';
        locationTitle.textContent = 'Lieu de la séance';

        roomNameInput.setAttribute('required', 'required');
        postalCodeInput.setAttribute('required', 'required');
        zoomLinkInput.removeAttribute('required');

        zoomLinkInput.value = '';
    }
}