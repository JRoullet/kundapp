// Client Sessions JavaScript - Meta Tags Simple Version
// Depends on common.js (which handles flash messages automatically)

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Client sessions page loaded');
    initializeFilters();
    initializeModals();
    handleTabRedirection();
    handleCreditsAnimation();
});

// ========================================
// CREDITS ANIMATION - SIMPLE META TAG DETECTION
// ========================================

function handleCreditsAnimation() {
    const creditsOperationMeta = document.querySelector('meta[name="credits-operation"]');
    const newCreditsMeta = document.querySelector('meta[name="new-credits"]');

    if (creditsOperationMeta && creditsOperationMeta.content) {
        const operation = creditsOperationMeta.content;
        const newCredits = newCreditsMeta ? parseInt(newCreditsMeta.content) : null;

        console.log(`Credits operation detected: ${operation}, newCredits: ${newCredits}`);

        //Value
        if (newCredits !== null) {
            const creditsCountElement = document.getElementById('creditsCount');
            if (creditsCountElement) {
                creditsCountElement.textContent = newCredits;
                console.log(`Credits value updated to: ${newCredits}`);
            }
        }
    }
}

// ========================================
// TAB REDIRECTION - MINIMAL JS
// ========================================

function handleTabRedirection() {
    const urlParams = new URLSearchParams(window.location.search);
    const tab = urlParams.get('tab');

    if (tab === 'upcoming') {
        document.getElementById('upcoming-tab').click();
    }
}

// ========================================
// MODAL HANDLERS
// ========================================

function initializeModals() {
    document.addEventListener('click', function(e) {
        // Info button
        if (e.target.classList.contains('description-btn-floating')) {
            const sessionTitle = e.target.getAttribute('data-session-title');
            const description = e.target.getAttribute('data-session-description');
            const teacherName = e.target.getAttribute('data-teacher-name');
            showSessionDetails(sessionTitle, description, teacherName);
        }

        // Registration modal
        if (e.target.classList.contains('register-modal-btn')) {
            const button = e.target;
            showRegistrationModal(
                button.getAttribute('data-session-id'),
                button.getAttribute('data-session-subject'),
                button.getAttribute('data-session-date'),
                button.getAttribute('data-session-time'),
                button.getAttribute('data-session-end-time'),
                button.getAttribute('data-session-teacher'),
                button.getAttribute('data-session-type'),
                button.getAttribute('data-session-credits')
            );
        }

        // Unregistration modal
        if (e.target.classList.contains('unregister-modal-btn')) {
            const button = e.target;
            showUnregistrationModal(
                button.getAttribute('data-session-id'),
                button.getAttribute('data-session-subject'),
                button.getAttribute('data-session-date'),
                button.getAttribute('data-session-time'),
                button.getAttribute('data-session-teacher')
            );
        }
    });
}

function showSessionDetails(sessionTitle, description, teacherName) {
    document.getElementById('sessionDescriptionTitle').textContent = teacherName ?
        `${sessionTitle} avec ${teacherName}` :
        sessionTitle;
    document.getElementById('sessionDescriptionContent').textContent = description || 'Aucune description disponible.';

    const modal = new mdb.Modal(document.getElementById('sessionDescriptionModal'));
    modal.show();
}

function showRegistrationModal(sessionId, subject, date, time, endTime, teacher, type, credits) {
    // Fill modal content
    document.getElementById('regModalSubject').textContent = subject;
    document.getElementById('regModalDate').textContent = date;
    document.getElementById('regModalTime').textContent = `${time} - ${endTime}`;
    document.getElementById('regModalTeacher').textContent = `Prof. ${teacher}`;
    document.getElementById('regModalType').textContent = type;
    document.getElementById('regModalCredits').textContent = credits;

    // Configure form action for redirection to upcoming tab
    const form = document.getElementById('regConfirmForm');
    form.action = `/client/sessions/${sessionId}/register?tab=upcoming`;

    // Show modal
    const modal = new mdb.Modal(document.getElementById('registrationModal'));
    modal.show();
}

function showUnregistrationModal(sessionId, subject, date, time, teacher) {
    // Fill modal content
    document.getElementById('unregModalSubject').textContent = subject;
    document.getElementById('unregModalDate').textContent = date;
    document.getElementById('unregModalTime').textContent = time;
    document.getElementById('unregModalTeacher').textContent = teacher;

    // Configure form action for redirection to upcoming tab
    const form = document.getElementById('unregConfirmForm');
    form.action = `/client/sessions/${sessionId}/unregister?tab=upcoming`;

    // Show modal
    const modal = new mdb.Modal(document.getElementById('unregistrationModal'));
    modal.show();
}

// ========================================
// FILTER FUNCTIONS - KEEP AS IS
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