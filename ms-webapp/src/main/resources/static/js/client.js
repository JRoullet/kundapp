// Client Sessions JavaScript - Modern Vanilla JS - No AJAX
// Depends on common.js

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Client sessions page loaded');
    initializeFilters();
    initializeSessionButtons();
});

// ========================================
// SESSION BUTTON HANDLERS
// ========================================

function initializeSessionButtons() {
    // Register buttons
    document.addEventListener('click', function(e) {
        if (e.target.closest('.register-btn')) {
            const sessionId = e.target.closest('.register-btn').dataset.sessionId;
            registerForSession(sessionId);
        }

        if (e.target.closest('.unregister-btn')) {
            const sessionId = e.target.closest('.unregister-btn').dataset.sessionId;
            unregisterFromSession(sessionId);
        }

        if (e.target.classList.contains('description-btn-floating')) {
            const sessionTitle = e.target.getAttribute('data-session-title');
            const description = e.target.getAttribute('data-session-description');
            const teacherName = e.target.getAttribute('data-teacher-name');
            showSessionDetails(sessionTitle, description, teacherName);
        }
    });
}

// ========================================
// FILTER FUNCTIONS
// ========================================

function initializeFilters() {
    const subjectFilter = document.getElementById('subjectFilter');
    const onlineFilter = document.getElementById('filterOnline');
    const irlFilter = document.getElementById('filterIrl');
    const teacherSearch = document.getElementById('teacherSearch');

    const debouncedFilter = CommonUtils.debounce(() => applyFilters(), 300);

    if (subjectFilter) subjectFilter.addEventListener('change', debouncedFilter);
    if (onlineFilter) onlineFilter.addEventListener('change', debouncedFilter);
    if (irlFilter) irlFilter.addEventListener('change', debouncedFilter);
    if (teacherSearch) teacherSearch.addEventListener('input', debouncedFilter);
}

function applyFilters() {
    const subjectFilter = document.getElementById('subjectFilter').value;
    const onlineChecked = document.getElementById('filterOnline').checked;
    const irlChecked = document.getElementById('filterIrl').checked;
    const teacherQuery = document.getElementById('teacherSearch').value.toLowerCase();

    const sessionCards = document.querySelectorAll('[data-session-card]');

    sessionCards.forEach(card => {
        const subject = card.dataset.subject;
        const isOnline = card.dataset.online === 'true';
        const teacherName = card.dataset.teacher.toLowerCase();

        const matchesSubject = !subjectFilter || subject === subjectFilter;
        const matchesType = (isOnline && onlineChecked) || (!isOnline && irlChecked);
        const matchesTeacher = !teacherQuery || teacherName.includes(teacherQuery);

        const shouldShow = matchesSubject && matchesType && matchesTeacher;

        if (shouldShow) {
            card.style.display = 'block';
            requestAnimationFrame(() => {
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            });
        } else {
            card.style.opacity = '0';
            card.style.transform = 'translateY(-10px)';
            setTimeout(() => {
                card.style.display = 'none';
            }, 200);
        }
    });
}

function toggleTeacherSearch() {
    const container = document.getElementById('teacherSearchContainer');
    const button = document.getElementById('toggleTeacherBtn');

    if (container.style.display === 'none') {
        container.style.display = 'block';
        button.innerHTML = '<i class="fas fa-times me-1"></i>Fermer recherche';
        document.getElementById('teacherSearch').focus();
    } else {
        container.style.display = 'none';
        button.innerHTML = '<i class="fas fa-search me-1"></i>Rechercher par professeur';
        clearTeacherSearch();
    }
}

function clearTeacherSearch() {
    document.getElementById('teacherSearch').value = '';
    applyFilters();
}

// ========================================
// MODAL FUNCTIONS
// ========================================

function showSessionDetails(sessionTitle, description, teacherName) {
    document.getElementById('sessionDescriptionTitle').textContent = teacherName ?
        `${sessionTitle} avec ${teacherName}` :
        sessionTitle;
    document.getElementById('sessionDescriptionContent').textContent = description || 'Aucune description disponible.';

    const modal = new mdb.Modal(document.getElementById('sessionDescriptionModal'));
    modal.show();
}

// ========================================
// SESSION ACTIONS - CORRIGÉ POUR NOUVEAU BACKEND
// ========================================

function registerForSession(sessionId, sessionName, buttonElement) {
    console.log('Attempting to register for session:', sessionId);

    if (!confirm(`Confirmer l'inscription à ${sessionName} ?`)) {
        return;
    }

    // Show loading state
    const originalText = buttonElement.innerHTML;
    buttonElement.disabled = true;
    buttonElement.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Inscription...';

    // ✅ ENDPOINT CORRIGÉ - Correspond aux contrôleurs backend
    fetch(`/client/sessions/${sessionId}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': CommonUtils.getCsrfToken()
        }
    })
        .then(response => {
            console.log('Registration response status:', response.status);

            if (response.ok) {
                // ✅ 200 OK = succès
                toastSystem.success('Inscription confirmée', `Votre place est réservée pour ${sessionName} !`);

                // Update button state
                updateButtonToUnregisterState(buttonElement, sessionId, sessionName);

                // Refresh page after short delay to update session list
                setTimeout(() => {
                    window.location.reload();
                }, 1500);

                return;
            }

            // ✅ GESTION D'ERREURS BASÉE SUR LES CODES HTTP DU GlobalExceptionHandler
            return response.text().then(errorText => {
                const errorMessage = getRegistrationErrorMessage(response.status);
                toastSystem.error('Erreur d\'inscription', errorMessage);
                resetRegisterButton(buttonElement, originalText);
            });
        })
        .catch(error => {
            console.error('Registration error:', error);
            toastSystem.error('Erreur de connexion', 'Impossible de traiter votre inscription');
            resetRegisterButton(buttonElement, originalText);
        });
}

function unregisterFromSession(sessionId, sessionName, buttonElement) {
    if (!confirm(`Êtes-vous sûr de vouloir vous désinscrire de ${sessionName} ?\n\nAttention: L'annulation n'est pas possible dans les 48h précédant la session.`)) {
        return;
    }

    // Show loading state
    const originalText = buttonElement.innerHTML;
    buttonElement.disabled = true;
    buttonElement.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Annulation...';

    // ✅ ENDPOINT CORRIGÉ - Correspond aux contrôleurs backend
    fetch(`/client/sessions/${sessionId}/unregister`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': CommonUtils.getCsrfToken()
        }
    })
        .then(response => {
            console.log('Unregistration response status:', response.status);

            if (response.ok) {
                // ✅ 200 OK = succès
                toastSystem.success('Désinscription confirmée', `Vous avez été désinscrit de ${sessionName}. Vos crédits ont été remboursés.`);

                // Update button state
                updateButtonToRegisterState(buttonElement, sessionId, sessionName);

                // Refresh page after short delay
                setTimeout(() => {
                    window.location.reload();
                }, 1500);

                return;
            }

            // ✅ GESTION D'ERREURS BASÉE SUR LES CODES HTTP
            return response.text().then(errorText => {
                const errorMessage = getUnregistrationErrorMessage(response.status);
                toastSystem.error('Erreur d\'annulation', errorMessage);
                resetUnregisterButton(buttonElement, originalText);
            });
        })
        .catch(error => {
            console.error('Unregistration error:', error);
            toastSystem.error('Erreur de connexion', 'Impossible de traiter votre désinscription');
            resetUnregisterButton(buttonElement, originalText);
        });
}

// ========================================
// ✅ NOUVELLES FONCTIONS UTILITAIRES
// ========================================

function getRegistrationErrorMessage(status) {
    switch(status) {
        case 402: // PAYMENT_REQUIRED
            return 'Crédits insuffisants pour cette session';
        case 404: // NOT_FOUND
            return 'Session non trouvée ou non disponible';
        case 409: // CONFLICT
            return 'Session complète ou vous êtes déjà inscrit';
        case 500: // INTERNAL_SERVER_ERROR
            return 'Erreur technique. L\'équipe support a été notifiée.';
        default:
            return 'Erreur lors de l\'inscription. Veuillez réessayer.';
    }
}

function getUnregistrationErrorMessage(status) {
    switch(status) {
        case 403: // FORBIDDEN
            return 'Annulation impossible: délai de 48h dépassé';
        case 404: // NOT_FOUND
            return 'Session non trouvée';
        case 409: // CONFLICT
            return 'Vous n\'êtes pas inscrit à cette session';
        case 500: // INTERNAL_SERVER_ERROR
            return 'Erreur technique. L\'équipe support a été notifiée.';
        default:
            return 'Erreur lors de l\'annulation. Veuillez réessayer.';
    }
}

function updateButtonToUnregisterState(buttonElement, sessionId, sessionName) {
    buttonElement.innerHTML = 'SE DÉSINSCRIRE';
    buttonElement.className = 'btn btn-outline-danger btn-sm unregister-btn';
    buttonElement.setAttribute('data-session-id', sessionId);
    buttonElement.setAttribute('data-session-name', sessionName);
    buttonElement.disabled = false;
}

function updateButtonToRegisterState(buttonElement, sessionId, sessionName) {
    buttonElement.innerHTML = 'S\'INSCRIRE';
    buttonElement.className = 'btn btn-primary btn-sm register-btn';
    buttonElement.setAttribute('data-session-id', sessionId);
    buttonElement.setAttribute('data-session-name', sessionName);
    buttonElement.disabled = false;
}

function resetRegisterButton(buttonElement, originalText) {
    if (buttonElement) {
        buttonElement.disabled = false;
        buttonElement.innerHTML = originalText || '<i class="fas fa-calendar-plus me-2"></i>S\'inscrire';
    }
}

function resetUnregisterButton(buttonElement, originalText) {
    if (buttonElement) {
        buttonElement.disabled = false;
        buttonElement.innerHTML = originalText || 'SE DÉSINSCRIRE';
    }

}