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
            showDescription(sessionTitle, description);
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

function showDescription(sessionTitle, description) {
    document.getElementById('sessionDescriptionTitle').textContent = sessionTitle;
    document.getElementById('sessionDescriptionContent').textContent = description || 'Aucune description disponible.';

    const modal = new mdb.Modal(document.getElementById('sessionDescriptionModal'));
    modal.show();
}

// ========================================
// SESSION ACTIONS
// ========================================

function registerForSession(sessionId) {
    console.log('Attempting to register for session:', sessionId);

    if (!confirm('Confirmer l\'inscription à cette session ?')) {
        return;
    }

    const button = document.querySelector(`[onclick*="registerForSession(${sessionId})"]`);
    if (button) {
        button.disabled = true;
        button.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Inscription...';
    }

    fetch(`/client/api/sessions/${sessionId}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': CommonUtils.getCsrfToken()
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                toastSystem.success('Inscription confirmée', 'Votre place est réservée !');
                window.location.reload();
            } else {
                toastSystem.error('Erreur d\'inscription', data.message || 'Une erreur est survenue');
                resetRegisterButton(button);
            }
        })
        .catch(error => {
            console.error('Registration error:', error);
            toastSystem.error('Erreur de connexion', 'Impossible de traiter votre inscription');
            resetRegisterButton(button);
        });
}

function unregisterFromSession(sessionId) {
    if (!confirm('Êtes-vous sûr de vouloir vous désinscrire de cette session ?')) {
        return;
    }

    fetch(`/client/api/sessions/${sessionId}/unregister`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': CommonUtils.getCsrfToken()
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                toastSystem.success('Désinscription confirmée', 'Vous avez été désinscrit de cette session');
                window.location.reload();
            } else {
                toastSystem.error('Erreur de désinscription', data.message || 'Une erreur est survenue');
            }
        })
        .catch(error => {
            console.error('Unregistration error:', error);
            toastSystem.error('Erreur de connexion', 'Impossible de traiter votre désinscription');
        });
}

function resetRegisterButton(button) {
    if (button) {
        button.disabled = false;
        button.innerHTML = '<i class="fas fa-calendar-plus me-2"></i>S\'inscrire';
    }
}