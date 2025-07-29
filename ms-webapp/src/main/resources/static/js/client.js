// Client Sessions JavaScript - Modern Vanilla JS with Fetch API only
// Depends on common.js

// Global state
let allAvailableSessions = [];
let allUpcomingSessions = [];
let allHistorySessions = [];

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Client sessions page loaded');

    // Initialize components
    initializeFilters();
    initializeTabs();

    // Load initial data
    loadAvailableSessions();
});

// ========================================
// TAB MANAGEMENT
// ========================================

function initializeTabs() {
    const tabButtons = document.querySelectorAll('[data-mdb-toggle="tab"]');

    tabButtons.forEach(button => {
        button.addEventListener('shown.mdb.tab', function(event) {
            const targetTab = event.target.getAttribute('data-mdb-target');

            switch(targetTab) {
                case '#available':
                    if (allAvailableSessions.length === 0) {
                        loadAvailableSessions();
                    }
                    break;
                case '#upcoming':
                    if (allUpcomingSessions.length === 0) {
                        loadUpcomingSessions();
                    }
                    break;
                case '#history':
                    if (allHistorySessions.length === 0) {
                        loadHistorySessions();
                    }
                    break;
            }
        });
    });
}

// ========================================
// DATA LOADING FUNCTIONS - Modern Fetch API
// ========================================

function loadAvailableSessions() {
    fetch('/client/sessions/available', {
        method: 'GET',
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
        .then(sessions => {
            allAvailableSessions = sessions;
            renderAvailableSessions(sessions);
        })
        .catch(error => {
            console.error('Error loading available sessions:', error);
            toastSystem.error('Erreur', 'Impossible de charger les sessions disponibles');
            showNoDataMessage('available');
        });
}

function loadUpcomingSessions() {
    fetch('/client/sessions/upcoming', {
        method: 'GET',
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
        .then(sessions => {
            allUpcomingSessions = sessions;
            renderUpcomingSessions(sessions);
        })
        .catch(error => {
            console.error('Error loading upcoming sessions:', error);
            toastSystem.error('Erreur', 'Impossible de charger vos sessions à venir');
            showNoDataMessage('upcoming');
        });
}

function loadHistorySessions() {
    fetch('/client/sessions/history', {
        method: 'GET',
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
        .then(sessions => {
            allHistorySessions = sessions;
            renderHistoryTable(sessions);
        })
        .catch(error => {
            console.error('Error loading history sessions:', error);
            toastSystem.error('Erreur', 'Impossible de charger l\'historique');
            showNoDataMessage('history');
        });
}

// ========================================
// RENDERING FUNCTIONS
// ========================================

function renderAvailableSessions(sessions) {
    const container = document.getElementById('availableSessionsContainer');
    const noDataElement = document.getElementById('noAvailableSessions');

    if (!sessions || sessions.length === 0) {
        container.innerHTML = '';
        noDataElement.style.display = 'block';
        return;
    }

    noDataElement.style.display = 'none';
    container.innerHTML = sessions.map(session => createSessionCard(session, 'available')).join('');
}

function renderUpcomingSessions(sessions) {
    const container = document.getElementById('upcomingSessionsContainer');
    const noDataElement = document.getElementById('noUpcomingSessions');

    if (!sessions || sessions.length === 0) {
        container.innerHTML = '';
        noDataElement.style.display = 'block';
        return;
    }

    noDataElement.style.display = 'none';
    container.innerHTML = sessions.map(session => createSessionCard(session, 'upcoming')).join('');
}

function renderHistoryTable(sessions) {
    const container = document.getElementById('historyTableContainer');
    const noDataElement = document.getElementById('noHistorySessions');

    if (!sessions || sessions.length === 0) {
        container.innerHTML = '';
        noDataElement.style.display = 'block';
        return;
    }

    noDataElement.style.display = 'none';
    container.innerHTML = createHistoryTable(sessions);
}

// ========================================
// CARD CREATION FUNCTIONS
// ========================================

function createSessionCard(session, type) {
    const isRegistered = session.isUserRegistered;
    const remainingSpots = session.availableSpots - session.registeredParticipants;
    const sessionDate = new Date(session.startDateTime);
    const endDate = new Date(sessionDate.getTime() + session.durationMinutes * 60000);

    return `
        <div class="col-md-6 col-lg-4 mb-4" 
             data-session-card 
             data-subject="${session.subject}" 
             data-online="${session.isOnline}"
             data-teacher="${session.teacherFirstName} ${session.teacherLastName}">
            <div class="card session-card h-100 border-0 shadow-sm" data-session-id="${session.id}">
                <div class="card-body d-flex flex-column">
                    <!-- Header with subject and status -->
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div>
                            <h5 class="session-title mb-1 fw-bold">${session.subject}</h5>
                        </div>
                        <div>
                            ${isRegistered ? '<span class="badge bg-success">Inscrit ✓</span>' : ''}
                        </div>
                    </div>
                    
                    <!-- Session Info Layout -->
                    <div class="row mb-3">
                        <!-- Left Column - Date/Time/Location -->
                        <div class="col-6">
                            <p class="mb-1">
                                <small class="text-muted">${formatDate(sessionDate)}</small>
                            </p>
                            <p class="mb-1">
                                <small class="text-muted">${formatTime(sessionDate)} - ${formatTime(endDate)}</small>
                            </p>
                            <p class="mb-1">
                                <small class="text-muted">
                                    ${session.isOnline ?
        '<i class="fas fa-video me-1"></i>En ligne' :
        `<i class="fas fa-map-marker-alt me-1"></i>${session.roomName || 'Lieu non défini'}`
    }
                                </small>
                            </p>
                        </div>
                        
                        <!-- Center Column - Teacher Info -->
                        <div class="col-6 text-center">
                            <p class="mb-1 fw-bold">Prof. ${session.teacherFirstName} ${session.teacherLastName}</p>
                            <p class="mb-0">
                                <small class="text-muted">
                                    ${session.registeredParticipants} participants (${Math.max(0, remainingSpots)} places)
                                </small>
                            </p>
                        </div>
                    </div>
                    
                    <!-- Action Button -->
                    <div class="mt-auto text-center">
                        ${createActionButton(session, type, remainingSpots)}
                    </div>
                </div>
            </div>
        </div>
    `;
}

function createActionButton(session, type, remainingSpots) {
    if (type === 'upcoming') {
        return `
            <button class="btn btn-outline-danger btn-sm" 
                    onclick="unregisterFromSession(${session.id})">
                <i class="fas fa-times me-1"></i>Se désinscrire
            </button>
        `;
    }

    if (session.isUserRegistered) {
        return `
            <button class="btn btn-outline-danger btn-sm" 
                    onclick="unregisterFromSession(${session.id})">
                <i class="fas fa-times me-1"></i>Se désinscrire
            </button>
        `;
    }

    const isDisabled = remainingSpots <= 0;
    return `
        <button class="btn btn-primary btn-sm ${isDisabled ? 'disabled' : ''}" 
                onclick="registerForSession(${session.id})" 
                ${isDisabled ? 'disabled' : ''}>
            <i class="fas fa-calendar-plus me-1"></i>
            ${isDisabled ? 'Complet' : 'S\'inscrire'}
        </button>
    `;
}

function createHistoryTable(sessions) {
    return `
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Session</th>
                    <th>Date</th>
                    <th>Professeur</th>
                    <th>Lieu/Type</th>
                    <th>Statut</th>
                </tr>
            </thead>
            <tbody>
                ${sessions.map(session => `
                    <tr>
                        <td>
                            <strong>${session.subject}</strong>
                            <br><small class="text-muted">${session.description || ''}</small>
                        </td>
                        <td>
                            ${formatDate(new Date(session.startDateTime))}
                            <br><small class="text-muted">${formatTime(new Date(session.startDateTime))}</small>
                        </td>
                        <td>Prof. ${session.teacherFirstName} ${session.teacherLastName}</td>
                        <td>
                            ${session.isOnline ?
        '<i class="fas fa-video me-1"></i>En ligne' :
        `<i class="fas fa-map-marker-alt me-1"></i>${session.roomName}`
    }
                        </td>
                        <td><span class="badge bg-success">Terminée</span></td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}

// ========================================
// FILTER FUNCTIONS
// ========================================

function initializeFilters() {
    const subjectFilter = document.getElementById('subjectFilter');
    const onlineFilter = document.getElementById('filterOnline');
    const irlFilter = document.getElementById('filterIrl');
    const teacherSearch = document.getElementById('teacherSearch');

    // Debounced filter application
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

        // Apply filters
        const matchesSubject = !subjectFilter || subject === subjectFilter;
        const matchesType = (isOnline && onlineChecked) || (!isOnline && irlChecked);
        const matchesTeacher = !teacherQuery || teacherName.includes(teacherQuery);

        const shouldShow = matchesSubject && matchesType && matchesTeacher;

        // Smooth animation
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
// SESSION ACTIONS - Modern Fetch API
// ========================================

function registerForSession(sessionId) {
    console.log('Attempting to register for session:', sessionId);

    // Find session and check credits
    const session = allAvailableSessions.find(s => s.id === sessionId);
    if (!session) {
        toastSystem.error('Erreur', 'Session introuvable');
        return;
    }

    // Confirm registration
    if (!confirm(`Confirmer l'inscription à cette session pour ${session.creditsRequired} crédit${session.creditsRequired > 1 ? 's' : ''} ?`)) {
        return;
    }

    // Disable button during request
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
                // Reload current tab data
                loadAvailableSessions();
                // Reset upcoming sessions to reload if accessed
                allUpcomingSessions = [];
            } else {
                toastSystem.error('Erreur d\'inscription', data.message || 'Une erreur est survenue');
                resetRegisterButton(button, sessionId);
            }
        })
        .catch(error => {
            console.error('Registration error:', error);
            toastSystem.error('Erreur de connexion', 'Impossible de traiter votre inscription');
            resetRegisterButton(button, sessionId);
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
                // Reload all tabs data
                loadAvailableSessions();
                loadUpcomingSessions();
                // Reset arrays to force reload
                allAvailableSessions = [];
                allUpcomingSessions = [];
            } else {
                toastSystem.error('Erreur de désinscription', data.message || 'Une erreur est survenue');
            }
        })
        .catch(error => {
            console.error('Unregistration error:', error);
            toastSystem.error('Erreur de connexion', 'Impossible de traiter votre désinscription');
        });
}

// ========================================
// UTILITY FUNCTIONS
// ========================================

function formatDate(date) {
    return date.toLocaleDateString('fr-FR', {
        weekday: 'long',
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    });
}

function formatTime(date) {
    return date.toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

function resetRegisterButton(button, sessionId) {
    if (button) {
        button.disabled = false;
        button.innerHTML = '<i class="fas fa-calendar-plus me-2"></i>S\'inscrire';
    }
}

function showNoDataMessage(type) {
    const containers = {
        available: 'availableSessionsContainer',
        upcoming: 'upcomingSessionsContainer',
        history: 'historyTableContainer'
    };

    const noDataElements = {
        available: 'noAvailableSessions',
        upcoming: 'noUpcomingSessions',
        history: 'noHistorySessions'
    };

    if (containers[type]) {
        document.getElementById(containers[type]).innerHTML = '';
        document.getElementById(noDataElements[type]).style.display = 'block';
    }
}