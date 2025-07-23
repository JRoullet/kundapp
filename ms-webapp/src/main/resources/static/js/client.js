// Client JavaScript - Session reservation and management
// Depends on common.js which must be loaded first

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Client page loaded');

    // Initialize filters
    initializeFilters();
});

// ========================================
// FILTERS FUNCTIONALITY
// ========================================

function initializeFilters() {
    const subjectFilter = document.getElementById('subjectFilter');
    if (subjectFilter) {
        // Use debounce for performance optimization
        const debouncedFilter = CommonUtils.debounce(() => filterSessionsBySubject(), 150);
        subjectFilter.addEventListener('change', debouncedFilter);
    }
}

function filterSessionsBySubject() {
    const selectedSubject = document.getElementById('subjectFilter').value;
    const sessionCards = document.querySelectorAll('.session-card');

    // Use modern animation API
    sessionCards.forEach(card => {
        const cardSubject = card.querySelector('.session-title').textContent.trim();
        const cardContainer = card.closest('.col-md-6, .col-lg-4');

        const shouldShow = !selectedSubject || cardSubject === selectedSubject;

        // Smooth animation instead of simple show/hide
        if (shouldShow) {
            cardContainer.style.display = 'block';
            requestAnimationFrame(() => {
                cardContainer.style.opacity = '1';
                cardContainer.style.transform = 'translateY(0)';
            });
        } else {
            cardContainer.style.opacity = '0';
            cardContainer.style.transform = 'translateY(-10px)';
            setTimeout(() => {
                cardContainer.style.display = 'none';
            }, 200);
        }
    });
}

// ========================================
// SESSION RESERVATION
// ========================================

function reserveSession(sessionId) {
    console.log('Attempting to reserve session:', sessionId);

    // Check if user has enough credits
    const userCredits = getUserCredits();
    const sessionCreditsRequired = getSessionCreditsRequired(sessionId);

    if (userCredits < sessionCreditsRequired) {
        toastSystem.error('Crédits insuffisants',
            `Cette session nécessite ${sessionCreditsRequired} crédit${sessionCreditsRequired > 1 ? 's' : ''}, mais vous n'en avez que ${userCredits}.`);
        return;
    }

    // Confirm reservation
    if (!confirm(`Confirmer la réservation de cette session pour ${sessionCreditsRequired} crédit${sessionCreditsRequired > 1 ? 's' : ''} ?`)) {
        return;
    }

    // Disable button during request
    const button = document.querySelector(`[onclick*="reserveSession(${sessionId})"]`);
    if (button) {
        button.disabled = true;
        button.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Réservation...';
    }

    // Make reservation API call using modern fetch
    fetch(`/client/session/${sessionId}/reserve`, {
        method: 'POST',
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
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
                toastSystem.success('Réservation confirmée', 'Votre place est réservée !');
                // Refresh page to update spots and credits
                setTimeout(() => {
                    window.location.reload();
                }, 1500);
            } else {
                toastSystem.error('Erreur de réservation', data.message || 'Une erreur est survenue');
                resetReservationButton(button, sessionId);
            }
        })
        .catch(error => {
            console.error('Error during reservation:', error);
            toastSystem.error('Erreur de connexion', 'Impossible de traiter votre réservation. Veuillez réessayer.');
            resetReservationButton(button, sessionId);
        });
}

// ========================================
// UTILITY FUNCTIONS
// ========================================

function getUserCredits() {
    const creditsElement = document.querySelector('[data-user-credits]');
    return creditsElement ? parseInt(creditsElement.dataset.userCredits) : 0;
}

function getSessionCreditsRequired(sessionId) {
    const sessionCard = document.querySelector(`[data-session-id="${sessionId}"]`);
    if (sessionCard) {
        const creditsText = sessionCard.querySelector('.credits-badge')?.textContent;
        const match = creditsText?.match(/(\d+)/);
        return match ? parseInt(match[1]) : 1;
    }
    return 1;
}

function resetReservationButton(button, sessionId) {
    if (button) {
        button.disabled = false;
        button.innerHTML = '<i class="fas fa-calendar-plus me-2"></i>Réserver';
    }
}