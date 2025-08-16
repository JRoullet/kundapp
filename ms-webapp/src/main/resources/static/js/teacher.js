// Teacher JavaScript - Session management (Factorized)
// Depends on common.js which must be loaded first

// ========================================
// INITIALIZATION
// ========================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('Teacher page loaded');

    // Set minimum datetime for both forms
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    const minDateTime = now.toISOString().slice(0, 16);

    setMinDateTimeForElement('sessionStartDateTime', minDateTime);
    setMinDateTimeForElement('sessionUpdateStartDateTime', minDateTime);

    // Initialize form validations
    initializeFormValidation('sessionForm', validateSessionForm);
    initializeFormValidation('sessionUpdateForm', validateSessionUpdateForm);

    // Initialize session type handlers for both contexts
    initializeSessionTypeHandlers(SESSION_CONTEXTS.CREATE);
    initializeSessionTypeHandlers(SESSION_CONTEXTS.UPDATE_TEACHER);
});

// ========================================
// UTILITY FUNCTIONS
// ========================================

function setMinDateTimeForElement(elementId, minDateTime) {
    const element = document.getElementById(elementId);
    if (element) {
        element.setAttribute('min', minDateTime);
    }
}

function initializeFormValidation(formId, validationFunction) {
    const form = document.getElementById(formId);
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validationFunction()) {
                e.preventDefault();
                return false;
            }
        });
    }
}

// ========================================
// GLOBAL VARIABLES
// ========================================
let currentSessionIdForParticipants = null;
let participantsLoaded = false;

// ========================================
// SESSION PARTICIPANTS MANAGEMENT
// ========================================
function loadParticipantsInModal() {
    // Show loading
    document.getElementById('participantsLoading').style.display = 'block';
    document.getElementById('participantsContent').style.display = 'none';
    document.getElementById('participantsEmpty').style.display = 'none';

    // Fetch participants
    fetch(`/teacher/sessions/${currentSessionIdForParticipants}/participants`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch participants');
            }
            return response.json();
        })
        .then(participants => {
            document.getElementById('participantsLoading').style.display = 'none';
            participantsLoaded = true;

            if (participants && participants.length > 0) {
                populateParticipantsTable(participants);
                document.getElementById('participantsContent').style.display = 'block';
            } else {
                document.getElementById('participantsEmpty').style.display = 'block';
            }
        })
        .catch(error => {
            console.error('Error fetching participants:', error);
            document.getElementById('participantsLoading').style.display = 'none';
            document.getElementById('participantsEmpty').style.display = 'block';
        });
}

function populateParticipantsTable(participants) {
    const tbody = document.getElementById('participantsTableBody');
    tbody.innerHTML = '';

    participants.forEach(participant => {
        const row = tbody.insertRow();
        row.innerHTML = `
            <td>${participant.firstName}</td>
            <td>${participant.lastName}</td>
            <td>${participant.email}</td>
        `;
    });
}

// Function for homepage participants modal
function showParticipants(sessionId) {
    console.log('showParticipants called with sessionId:', sessionId);
    const modal = new mdb.Modal(document.getElementById('participantsModal'));

    // Show loading state
    document.getElementById('participantsModalLoading').style.display = 'block';
    document.getElementById('participantsModalContent').style.display = 'none';
    document.getElementById('participantsModalEmpty').style.display = 'none';

    modal.show();

    // Fetch participants
    fetch(`/teacher/sessions/${sessionId}/participants`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch participants');
            }
            return response.json();
        })
        .then(participants => {
            document.getElementById('participantsModalLoading').style.display = 'none';

            if (participants && participants.length > 0) {
                populateParticipantsModalTable(participants);
                document.getElementById('participantsModalContent').style.display = 'block';
            } else {
                document.getElementById('participantsModalEmpty').style.display = 'block';
            }
        })
        .catch(error => {
            console.error('Error fetching participants:', error);
            document.getElementById('participantsModalLoading').style.display = 'none';
            document.getElementById('participantsModalEmpty').style.display = 'block';
            CommonUtils.showToast('error', 'Erreur', 'Impossible de charger la liste des participants');
        });
}

function populateParticipantsModalTable(participants) {
    const tbody = document.getElementById('participantsModalTableBody');
    tbody.innerHTML = '';

    participants.forEach(participant => {
        const row = tbody.insertRow();
        row.innerHTML = `
            <td>${participant.firstName}</td>
            <td>${participant.lastName}</td>
            <td>${participant.email}</td>
        `;
    });
}

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

    // Set default values
    setDefaultFormValues();

    const modal = new mdb.Modal(document.getElementById('sessionModal'));
    modal.show();
}

function setDefaultFormValues() {
    const elements = {
        availableSpots: document.getElementById('sessionAvailableSpots'),
        duration: document.getElementById('sessionDurationMinutes'),
        creditsRequired: document.getElementById('sessionCreditsRequired')
    };

    if (elements.availableSpots) elements.availableSpots.value = '10';
    if (elements.duration) elements.duration.value = '60';
    if (elements.creditsRequired) elements.creditsRequired.value = '1';
}

function openEditSessionModal(sessionId) {
    console.log('Opening edit session modal for session:', sessionId);

    // Configure form
    document.getElementById('sessionUpdateForm').action = '/teacher/sessions/' + sessionId + '/update';
    document.getElementById('sessionUpdateId').value = sessionId;

    // Fetch and populate session data
    fetch('/teacher/sessions/' + sessionId + '/details')
        .then(response => {
            if (!response.ok) {
                throw new Error('Session not found');
            }
            return response.json();
        })
        .then(session => {
            console.log('Session data loaded:', session);
            populateUpdateForm(session, sessionId);

            const modal = new mdb.Modal(document.getElementById('sessionUpdateModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error loading session data:', error);
            toastSystem.error('Erreur', 'Impossible de charger les données de la session');
        });
}

function populateUpdateForm(session, sessionId) {
    // Populate basic fields
    const basicFields = {
        'sessionUpdateSubject': session.subject,
        'sessionUpdateDescription': session.description,
        'sessionUpdateAvailableSpots': session.availableSpots,
        'sessionUpdateCreditsRequired': session.creditsRequired,
        'sessionUpdateDurationMinutes': session.durationMinutes
    };

    Object.entries(basicFields).forEach(([id, value]) => {
        const element = document.getElementById(id);
        if (element && value !== undefined) {
            element.value = value;
        }
    });

    // Handle datetime
    if (session.startDateTime) {
        const datetimeLocal = session.startDateTime.slice(0, 16);
        document.getElementById('sessionUpdateStartDateTime').value = datetimeLocal;
    }

    // Handle session type and conditional fields
    const isOnline = session.isOnline || false;
    populateSessionTypeFields(isOnline, session, SESSION_CONTEXTS.UPDATE_TEACHER);

    currentSessionIdForParticipants = sessionId;
    participantsLoaded = false;
    console.log('Stored session ID for participants:', sessionId);
    console.log('Session data:', session);


    // Update participants count
    const participantsCount = session.registeredParticipants || 0;
    console.log('Participants count:', participantsCount);
    document.getElementById('sessionUpdateParticipantsCount').textContent =
        `${participantsCount} participant(s) inscrit(s)`;

    // Reset participants section
    document.getElementById('participantsSection').style.display = 'none';
    document.getElementById('participantsCountLine').style.display = 'block';
    document.getElementById('toggleParticipantsIcon').textContent = '▶';


    // Hide participants section
    const toggleBtn = document.getElementById('toggleParticipantsBtn');
    if (participantsCount > 0) {
        toggleBtn.style.display = 'inline-block';
    } else {
        toggleBtn.style.display = 'none';
    }

    // Configure cancel button
    const cancelBtn = document.getElementById('sessionUpdateCancelBtn');
    cancelBtn.onclick = () => confirmCancelSessionFromUpdateModal(session.id);
}


function toggleParticipantsSection() {
    const section = document.getElementById('participantsSection');
    const icon = document.getElementById('toggleParticipantsIcon');
    const countLine = document.getElementById('participantsCountLine');

    if (section.style.display === 'none') {
        // Display section
        section.style.display = 'block';
        icon.textContent = '▼';
        countLine.style.display = 'none';

        // Load participants if not already loaded
        if (!participantsLoaded && currentSessionIdForParticipants) {
            loadParticipantsInModal();
        }
    } else {
        // Hide section
        section.style.display = 'none';
        icon.textContent = '▶';
        countLine.style.display = 'block';
    }
}

function confirmCancelSessionFromUpdateModal(sessionId) {
    showConfirmation(
        'Annuler la séance',
        'Êtes-vous sûr de vouloir annuler cette séance ? Cette action est irréversible et les participants seront notifiés.',
        function() {
            const updateModal = mdb.Modal.getInstance(document.getElementById('sessionUpdateModal'));
            updateModal.hide();
            submitActionWithParams('/teacher/sessions/cancel', {sessionId: sessionId});
        }
    );
}

// ========================================
// FORM VALIDATION - UNIFIED
// ========================================

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

function validateSessionCommonFields(formData) {
    // Subject validation
    const subject = formData.get('subject');
    if (!subject) {
        toastSystem.error('Erreur de validation', 'Le type de cours est obligatoire');
        return false;
    }

    // Description validation
    const description = formData.get('description');
    if (!description || description.trim().length === 0) {
        toastSystem.error('Erreur de validation', 'La description est obligatoire');
        return false;
    }

    // Session type validation using common function
    if (!validateSessionTypeFields(formData)) {
        return false;
    }

    // Common field validations
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

    const availableSpots = parseInt(formData.get('availableSpots'));
    if (!availableSpots || availableSpots < 1 || availableSpots > 50) {
        toastSystem.error('Erreur de validation', 'Le nombre de places doit être entre 1 et 50');
        return false;
    }

    const duration = parseInt(formData.get('durationMinutes'));
    if (!duration || duration < 15 || duration > 300) {
        toastSystem.error('Erreur de validation', 'La durée doit être entre 15 et 300 minutes');
        return false;
    }

    const creditsRequired = parseInt(formData.get('creditsRequired'));
    if (!creditsRequired || creditsRequired < 1 || creditsRequired > 10) {
        toastSystem.error('Erreur de validation', 'Le nombre de crédits requis doit être entre 1 et 10');
        return false;
    }

    return true;
}