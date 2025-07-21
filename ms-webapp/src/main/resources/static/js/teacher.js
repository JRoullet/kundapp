function validateSessionForm() {
    const form = document.getElementById('sessionForm');
    const formData = new FormData(form);

    // Validation du sujet (obligatoire)
    const subject = formData.get('subject');
    if (!subject) {
        toastSystem.error('Erreur de validation', 'Le type de cours est obligatoire');
        return false;
    }

    // Validation de la description (obligatoire)
    const description = formData.get('description');
    if (!description || description.trim().length === 0) {
        toastSystem.error('Erreur de validation', 'La description est obligatoire');
        return false;
    }

    // Validation du nom de salle (obligatoire)
    const roomName = formData.get('roomName');
    if (!roomName || roomName.trim().length === 0) {
        toastSystem.error('Erreur de validation', 'Le nom de la salle/lieu est obligatoire');
        return false;
    }

    // Validation du code postal (obligatoire avec pattern)
    const postalCode = formData.get('postalCode');
    const postalCodeRegex = /^(0[1-9]|[1-8][0-9]|9[0-8])\d{3}$/;
    if (!postalCode || !postalCodeRegex.test(postalCode)) {
        toastSystem.error('Erreur de validation', 'Le code postal doit être un code postal français valide (ex: 75001)');
        return false;
    }

    // Validation du lien Google Maps (optionnel mais si présent doit être valide)
    const googleMapsLink = formData.get('googleMapsLink');
    if (googleMapsLink && googleMapsLink.trim() !== '') {
        const googleMapsRegex = /^https:\/\/maps\.app\.goo\.gl\/.*/;
        if (!googleMapsRegex.test(googleMapsLink)) {
            toastSystem.error('Erreur de validation', 'Le lien doit être un lien Google Maps valide (https://maps.app.goo.gl/...)');
            return false;
        }
    }

    // Validation de la date et heure (combinées en startDateTime)
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

    // Validation du nombre de places
    const availableSpots = parseInt(formData.get('availableSpots'));
    if (!availableSpots || availableSpots < 1 || availableSpots > 50) {
        toastSystem.error('Erreur de validation', 'Le nombre de places doit être entre 1 et 50');
        return false;
    }

    // Validation de la durée
    const duration = parseInt(formData.get('durationMinutes'));
    if (!duration || duration < 15 || duration > 300) {
        toastSystem.error('Erreur de validation', 'La durée doit être entre 15 et 300 minutes');
        return false;
    }

    // Validation des crédits requis
    const creditsRequired = parseInt(formData.get('creditsRequired'));
    if (!creditsRequired || creditsRequired < 1 || creditsRequired > 10) {
        toastSystem.error('Erreur de validation', 'Le nombre de crédits requis doit être entre 1 et 10');
        return false;
    }

    return true;
}// Teacher JavaScript - Session management

// Toast System for success/error messages (réutilisé depuis admin.js)
class ToastSystem {
    constructor() {
        this.container = document.getElementById('toastContainer');
        this.toasts = [];
        this.maxToasts = 5;
    }

    show(type, title, message, duration = 3000) {
        if (this.toasts.length >= this.maxToasts) {
            this.hide(this.toasts[0]);
        }

        const toast = this.create(type, title, message);
        this.container.appendChild(toast);
        this.toasts.push(toast);

        setTimeout(() => {
            toast.classList.add('show');
        }, 10);

        if (duration > 0) {
            setTimeout(() => {
                this.hide(toast);
            }, duration);
        }

        return toast;
    }

    create(type, title, message) {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;

        const icons = {
            success: '✓',
            error: '✕',
            info: 'ℹ'
        };

        toast.innerHTML = `
            <button class="toast-close" onclick="toastSystem.hide(this.parentElement)">×</button>
            <div class="toast-content">
                <div class="toast-icon">${icons[type] || 'ℹ'}</div>
                <div class="toast-message">
                    <strong>${title}</strong>
                    ${message ? `<div style="margin-top: 4px; opacity: 0.9;">${message}</div>` : ''}
                </div>
            </div>
        `;

        toast.addEventListener('click', () => {
            this.hide(toast);
        });

        return toast;
    }

    hide(toast) {
        if (!toast || !toast.parentElement) return;

        toast.classList.remove('show');
        toast.classList.add('hide');

        setTimeout(() => {
            if (toast.parentElement) {
                toast.parentElement.removeChild(toast);
            }
            this.toasts = this.toasts.filter(t => t !== toast);
        }, 400);
    }

    success(title, message, duration) {
        return this.show('success', title, message, duration);
    }

    error(title, message, duration) {
        return this.show('error', title, message, duration);
    }

    info(title, message, duration) {
        return this.show('info', title, message, duration);
    }
}

// Initialize global toast system
const toastSystem = new ToastSystem();

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Teacher page loaded');

    // Validation du formulaire de session
    const sessionForm = document.getElementById('sessionForm');
    if (sessionForm) {
        sessionForm.addEventListener('submit', function(e) {
            if (!validateSessionForm()) {
                e.preventDefault();
                return false;
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

            document.getElementById('sessionDuration').value = session.durationMinutes || '';
            document.getElementById('sessionAvailableSpots').value = session.availableSpots || '';
            document.getElementById('sessionCreditsRequired').value = session.creditsRequired || '';
            document.getElementById('sessionBringMattress').checked = session.bringYourMattress || false;

            const modal = new mdb.Modal(document.getElementById('sessionModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Erreur lors du chargement des données de la session:', error);
            toastSystem.error('Erreur', 'Impossible de charger les données de la session');
        });
}

// ========================================
// SESSION ACTIONS
// ========================================

function viewSessionDetails(sessionId) {
    // Redirection vers une page de détails ou ouverture d'un modal de détails
    window.location.href = '/teacher/session/' + sessionId + '/details';
}

function confirmDeleteSession(sessionId) {
    showConfirmation(
        'Supprimer la séance',
        'Êtes-vous sûr de vouloir supprimer définitivement cette séance ? Cette action est irréversible.',
        function() {
            submitAction('/teacher/session/' + sessionId + '/delete');
        }
    );
}

// ========================================
// FORM VALIDATION
// ========================================

function validateSessionForm() {
    const form = document.getElementById('sessionForm');
    const formData = new FormData(form);

    // Validation du sujet
    const subject = formData.get('subject');
    if (!subject) {
        toastSystem.error('Erreur de validation', 'Le sujet est obligatoire');
        return false;
    }

    // Validation de la description
    const description = formData.get('description');
    if (!description || description.trim().length === 0) {
        toastSystem.error('Erreur de validation', 'La description est obligatoire');
        return false;
    }

    // Validation du nom de salle
    const roomName = formData.get('roomName');
    if (!roomName || roomName.trim().length === 0) {
        toastSystem.error('Erreur de validation', 'Le nom de la salle est obligatoire');
        return false;
    }

    // Validation du code postal
    const postalCode = formData.get('postalCode');
    const postalCodeRegex = /^(0[1-9]|[1-8][0-9]|9[0-8])\d{3}$/;
    if (!postalCode || !postalCodeRegex.test(postalCode)) {
        toastSystem.error('Erreur de validation', 'Le code postal doit être un code postal français valide');
        return false;
    }

    // Validation du lien Google Maps (optionnel mais si présent doit être valide)
    const googleMapsLink = formData.get('googleMapsLink');
    if (googleMapsLink && googleMapsLink.trim() !== '') {
        // Regex plus flexible pour tous les formats Google Maps
        const googleMapsRegex = /^https:\/\/(maps\.(google|app\.goo)\.(com|gl)\/|www\.google\.(com|fr)\/maps)/;
        if (!googleMapsRegex.test(googleMapsLink)) {
            toastSystem.error('Erreur de validation', 'Le lien doit être un lien Google Maps valide');
            return false;
        }
    }

    // Validation de la date et heure (combinées en startDateTime)
    const sessionDate = formData.get('date');
    const sessionTime = formData.get('time');

    if (sessionDate && sessionTime) {
        const sessionDateTime = new Date(sessionDate + 'T' + sessionTime);
        const now = new Date();

        if (sessionDateTime <= now) {
            toastSystem.error('Erreur de validation', 'La session doit être programmée dans le futur');
            return false;
        }
    }

    // Validation du nombre de places
    const availableSpots = parseInt(formData.get('availableSpots'));
    if (!availableSpots || availableSpots < 1 || availableSpots > 50) {
        toastSystem.error('Erreur de validation', 'Le nombre de places doit être entre 1 et 50');
        return false;
    }

    // Validation de la durée
    const duration = parseInt(formData.get('durationMinutes'));
    if (!duration || duration < 15 || duration > 300) {
        toastSystem.error('Erreur de validation', 'La durée doit être entre 15 et 300 minutes');
        return false;
    }

    // Validation des crédits requis
    const creditsRequired = parseInt(formData.get('creditsRequired'));
    if (creditsRequired && (creditsRequired < 1 || creditsRequired > 10)) {
        toastSystem.error('Erreur de validation', 'Le nombre de crédits requis doit être entre 1 et 10');
        return false;
    }

    return true;
}

// ========================================
// UTILITY FUNCTIONS
// ========================================

function showConfirmation(title, message, callback) {
    document.getElementById('confirmationTitle').textContent = title;
    document.getElementById('confirmationMessage').textContent = message;

    const confirmBtn = document.getElementById('confirmButton');
    confirmBtn.onclick = function() {
        callback();
        const modal = mdb.Modal.getInstance(document.getElementById('confirmationModal'));
        modal.hide();
    };

    const modal = new mdb.Modal(document.getElementById('confirmationModal'));
    modal.show();
}

function submitAction(actionUrl) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = actionUrl;
    form.style.display = 'none';

    // Add CSRF token from meta tag
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfInput = document.createElement('input');
    csrfInput.type = 'hidden';
    csrfInput.name = '_csrf';
    csrfInput.value = csrfToken;
    form.appendChild(csrfInput);

    document.body.appendChild(form);
    form.submit();
    document.body.removeChild(form);
}

// ========================================
// ADDITIONAL FEATURES (pour future évolution)
// ========================================

function exportSessionsToCSV() {
    // Fonctionnalité future pour exporter les sessions
    toastSystem.info('Export', 'Fonctionnalité en cours de développement');
}

function sendReminderEmails(sessionId) {
    // Fonctionnalité future pour envoyer des rappels
    toastSystem.info('Rappels', 'Fonctionnalité en cours de développement');
}