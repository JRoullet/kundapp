// Admin JavaScript - Teachers/Users management
// Depends on common.js which must be loaded first

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Admin page loaded');

    // Initialize session type handlers for admin
    initializeSessionTypeHandlers(SESSION_CONTEXTS.UPDATE_ADMIN);

    // Initialize form validation for session update
    const sessionUpdateForm = document.getElementById('sessionUpdateForm');
    if (sessionUpdateForm) {
        sessionUpdateForm.addEventListener('submit', function(e) {
            if (!validateSessionUpdateForm()) {
                e.preventDefault();
                return false;
            }
        });
    }

    // Manage tabs based on URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const activeTab = urlParams.get('tab');

    if (activeTab === 'users') {
        // Enable users tab
        document.getElementById('teachers-tab').classList.remove('active');
        document.getElementById('users-tab').classList.add('active');
        document.getElementById('teachers-content').classList.remove('show', 'active');
        document.getElementById('users-content').classList.add('show', 'active');
    } else if (activeTab === 'sessions') {
        // Enable sessions tab
        document.getElementById('teachers-tab').classList.remove('active');
        document.getElementById('sessions-tab').classList.add('active');
        document.getElementById('teachers-content').classList.remove('show', 'active');
        document.getElementById('sessions-content').classList.add('show', 'active');
    }
});

// ========================================
// SEARCH SECTION TOGGLE
// ========================================

function toggleSearchSection() {
    const searchFields = document.getElementById('searchFields');
    const searchIcon = document.getElementById('searchIcon');

    if (searchFields.style.display === 'none') {
        searchFields.style.display = 'flex';
        searchIcon.textContent = '▼';
    } else {
        searchFields.style.display = 'none';
        searchIcon.textContent = '▶';
    }
}

// ========================================
// TEACHER MODAL MANAGEMENT
// ========================================

function openCreateTeacherModal() {
    document.getElementById('teacherModalTitle').textContent = 'Créer un Teacher';
    document.getElementById('teacherForm').reset();
    document.getElementById('teacherId').value = '';
    document.getElementById('teacherForm').action = '/admin/teachers';

    const modal = new mdb.Modal(document.getElementById('teacherModal'));
    modal.show();
}

function openEditTeacherModal(teacherId) {
    // Configure form
    document.getElementById('teacherUpdateForm').action = '/admin/teachers/' + teacherId + '/update';
    document.getElementById('teacherUpdateId').value = teacherId;

    fetch('/admin/teachers/' + teacherId)
        .then(response => {
            if (!response.ok) {
                throw new Error('Teacher not found');
            }
            return response.json();
        })
        .then(teacher => {
            // Basic fields
            document.getElementById('teacherUpdateFirstName').value = teacher.firstName || '';
            document.getElementById('teacherUpdateLastName').value = teacher.lastName || '';
            document.getElementById('teacherUpdateEmail').value = teacher.email || '';
            document.getElementById('teacherUpdatePhone').value = teacher.phoneNumber || '';
            document.getElementById('teacherUpdateBirthDate').value = teacher.dateOfBirth || '';
            document.getElementById('teacherUpdateBiography').value = teacher.biography || '';

            // Address fields from address object
            const address = teacher.address || {};
            document.getElementById('teacherUpdateStreet').value = address.street || '';
            document.getElementById('teacherUpdateCity').value = address.city || '';
            document.getElementById('teacherUpdateZipCode').value = address.zipCode || '';
            document.getElementById('teacherUpdateCountry').value = address.country || '';

            // Configure action buttons
            const deleteBtn = document.getElementById('teacherUpdateDeleteBtn');
            const toggleStatusBtn = document.getElementById('teacherUpdateToggleStatusBtn');

            deleteBtn.onclick = () => confirmDeleteTeacherFromModal(teacherId);

            if (teacher.status) {
                toggleStatusBtn.textContent = 'Désactiver ce teacher';
                toggleStatusBtn.className = 'btn btn-outline-secondary btn-sm';
                toggleStatusBtn.onclick = () => confirmDisableTeacherFromModal(teacherId);
            } else {
                toggleStatusBtn.textContent = 'Activer ce teacher';
                toggleStatusBtn.className = 'btn btn-success';
                toggleStatusBtn.onclick = () => confirmEnableTeacherFromModal(teacherId);
            }

            // Show modal
            const modal = new mdb.Modal(document.getElementById('teacherUpdateModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error loading teacher data:', error);
            toastSystem.error('Erreur', 'Impossible de charger les données du teacher');
        });
}

// ========================================
// USER MODAL MANAGEMENT
// ========================================

function openCreateUserModal() {
    document.getElementById('userModalTitle').textContent = 'Créer un Client';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('userForm').action = '/admin/users';

    const modal = new mdb.Modal(document.getElementById('userModal'));
    modal.show();
}

function openEditUserModal(userId) {
    // Configure form
    document.getElementById('userUpdateForm').action = '/admin/users/' + userId + '/update';
    document.getElementById('userUpdateId').value = userId;

    fetch('/admin/users/' + userId)
        .then(response => {
            if (!response.ok) {
                throw new Error('User not found');
            }
            return response.json();
        })
        .then(user => {
            // Pre-fill fields
            document.getElementById('userUpdateFirstName').value = user.firstName || '';
            document.getElementById('userUpdateLastName').value = user.lastName || '';
            document.getElementById('userUpdateEmail').value = user.email || '';
            document.getElementById('userUpdatePhone').value = user.phoneNumber || '';
            document.getElementById('userUpdateBirthDate').value = user.dateOfBirth || '';
            document.getElementById('userUpdateCredits').value = user.credits || 0;

            // Address fields from address object
            const address = user.address || {};
            document.getElementById('userUpdateStreet').value = address.street || '';
            document.getElementById('userUpdateCity').value = address.city || '';
            document.getElementById('userUpdateZipCode').value = address.zipCode || '';
            document.getElementById('userUpdateCountry').value = address.country || '';

            // Configure action buttons
            const deleteBtn = document.getElementById('userUpdateDeleteBtn');
            const toggleStatusBtn = document.getElementById('userUpdateToggleStatusBtn');

            deleteBtn.onclick = () => confirmDeleteUserFromModal(userId);

            if (user.status) {
                toggleStatusBtn.textContent = 'Désactiver ce client';
                toggleStatusBtn.className = 'btn btn-outline-secondary btn-sm';
                toggleStatusBtn.onclick = () => confirmDisableUserFromModal(userId);
            } else {
                toggleStatusBtn.textContent = 'Activer ce client';
                toggleStatusBtn.className = 'btn btn-success';
                toggleStatusBtn.onclick = () => confirmEnableUserFromModal(userId);
            }

            // Show modal
            const modal = new mdb.Modal(document.getElementById('userUpdateModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error loading user data:', error);
            toastSystem.error('Erreur', 'Impossible de charger les données du user');
        });
}

function openCreditsModal(userId) {
    document.getElementById('creditsForm').action = '/admin/users/' + userId + '/credits/add';
    document.getElementById('creditsUserId').value = userId;

    const modal = new mdb.Modal(document.getElementById('creditsModal'));
    modal.show();
}

// ========================================
// SESSION MANAGEMENT
// ========================================

function showParticipants(sessionId) {
    const modal = new mdb.Modal(document.getElementById('participantsModal'));
    const loadingDiv = document.getElementById('participantsLoading');
    const contentDiv = document.getElementById('participantsContent');
    const emptyDiv = document.getElementById('participantsEmpty');
    const tableBody = document.getElementById('participantsTableBody');

    // Show loading state
    loadingDiv.style.display = 'block';
    contentDiv.style.display = 'none';
    emptyDiv.style.display = 'none';

    modal.show();

    // Simple fetch with promises - no async/await
    fetch('/admin/sessions/' + sessionId + '/participants', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': CommonUtils.getCsrfToken()
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch participants');
            }
            return response.json();
        })
        .then(participants => {
            loadingDiv.style.display = 'none';

            if (participants && participants.length > 0) {
                // Clear and populate table
                tableBody.innerHTML = '';
                participants.forEach(participant => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                    <td>${participant.firstName || '-'}</td>
                    <td>${participant.lastName || '-'}</td>
                    <td>${participant.email || '-'}</td>
                `;
                    tableBody.appendChild(row);
                });
                contentDiv.style.display = 'block';
            } else {
                emptyDiv.style.display = 'block';
            }
        })
        .catch(error => {
            console.error('Error loading participants:', error);
            loadingDiv.style.display = 'none';
            emptyDiv.style.display = 'block';
            toastSystem.error('Erreur', 'Impossible de charger les participants');
        });
}

function openEditSessionModal(sessionId) {
    // Configure form
    document.getElementById('sessionUpdateForm').action = '/admin/sessions/' + sessionId + '/update';
    document.getElementById('sessionUpdateId').value = sessionId;

    // Fetch session data
    fetch('/admin/sessions/' + sessionId + '/details')
        .then(response => {
            if (!response.ok) {
                throw new Error('Session not found');
            }
            return response.json();
        })
        .then(session => {
            populateAdminUpdateForm(session, sessionId);
            const modal = new mdb.Modal(document.getElementById('sessionUpdateModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error loading session data:', error);
            toastSystem.error('Erreur', 'Impossible de charger les données de la session');
        });
}

function populateAdminUpdateForm(session, sessionId) {
    // Configure form based on session status
    const isEditable = session.status === 'SCHEDULED';

    // Populate basic fields
    const fieldMapping = {
        'Subject': 'subject',
        'Description': 'description',
        'AvailableSpots': 'availableSpots',
        'CreditsRequired': 'creditsRequired',
        'DurationMinutes': 'durationMinutes'
    };

    Object.entries(fieldMapping).forEach(([fieldId, sessionProperty]) => {
        const element = document.getElementById(`sessionUpdate${fieldId}`);
        if (element && session[sessionProperty] !== undefined) {
            element.value = session[sessionProperty];
        }
    });

    // Handle datetime
    if (session.startDateTime) {
        document.getElementById('sessionUpdateStartDateTime').value = session.startDateTime.slice(0, 16);
    }

    // Handle session type using common function
    const isOnline = session.isOnline || false;
    populateSessionTypeFields(isOnline, session, SESSION_CONTEXTS.UPDATE_ADMIN);

    // Update participants count
    const participantsCount = session.registeredParticipants || 0;
    document.getElementById('sessionUpdateParticipantsCount').textContent = `${participantsCount} participant(s) inscrit(s)`;

    // Configure buttons based on status
    const saveBtn = document.querySelector('#sessionUpdateModal .btn-admin-primary');
    const cancelBtn = document.getElementById('sessionUpdateCancelBtn');
    const formElements = document.querySelectorAll('#sessionUpdateForm input, #sessionUpdateForm select, #sessionUpdateForm textarea');

    if (isEditable) {
        saveBtn.style.display = 'inline-block';
        cancelBtn.style.display = 'inline-block';
        cancelBtn.onclick = () => confirmCancelSessionFromUpdateModal(sessionId);
        formElements.forEach(el => el.disabled = false);
        document.querySelector('#sessionUpdateModal .modal-title').textContent = 'Modifier la session';
    } else {
        saveBtn.style.display = 'none';
        cancelBtn.style.display = 'none';
        formElements.forEach(el => el.disabled = true);
        const statusText = session.status === 'CANCELLED' ? 'annulée' : 'terminée';
        document.querySelector('#sessionUpdateModal .modal-title').textContent = `Consultation session ${statusText}`;
    }
}

function confirmCancelSessionFromUpdateModal(sessionId) {
    showConfirmation(
        'Annuler la Session',
        'Êtes-vous sûr de vouloir annuler cette session ? Les participants seront notifiés.',
        function() {
            const editModal = mdb.Modal.getInstance(document.getElementById('sessionUpdateModal'));
            editModal.hide();
            submitActionWithParams('/admin/sessions/cancel', {sessionId: sessionId});
        }
    );
}

function confirmCancelAdminSession(sessionId) {
    showConfirmation(
        'Annuler la Session',
        'Êtes-vous sûr de vouloir annuler cette session ? Les participants seront notifiés.',
        function() {
            submitAction('/admin/sessions/' + sessionId + '/cancel');
        }
    );
}

// ========================================
// SESSION FORM VALIDATION
// ========================================

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

    // Date validation
    const startDateTime = formData.get('startDateTime');
    if (!startDateTime) {
        toastSystem.error('Erreur de validation', 'La date et l\'heure sont obligatoires');
        return false;
    }

    // Other validations (spots, duration, credits)
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

// ========================================
// SEARCH AND FILTER FUNCTIONALITY
// ========================================

function filterCurrentTab() {
    const activeTab = document.querySelector('.nav-link.active');
    if (activeTab && activeTab.id === 'teachers-tab') {
        filterTeachers();
    } else if (activeTab && activeTab.id === 'users-tab') {
        filterUsers();
    } else if (activeTab && activeTab.id === 'sessions-tab') {
        filterSessions();
    }
}

function filterTeachers() {
    const firstName = document.getElementById('globalFirstNameSearch').value.toLowerCase().trim();
    const lastName = document.getElementById('globalLastNameSearch').value.toLowerCase().trim();

    const tableRows = document.querySelectorAll('.teacher-row');

    tableRows.forEach(row => {
        const cells = row.cells;
        if (cells.length < 6) return;

        const rowFirstName = cells[0].textContent.toLowerCase().trim();
        const rowLastName = cells[1].textContent.toLowerCase().trim();

        const matchesFirstName = !firstName || rowFirstName.includes(firstName);
        const matchesLastName = !lastName || rowLastName.includes(lastName);

        const isVisible = matchesFirstName && matchesLastName;

        if (isVisible) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

function filterUsers() {
    const firstName = document.getElementById('globalFirstNameSearch').value.toLowerCase().trim();
    const lastName = document.getElementById('globalLastNameSearch').value.toLowerCase().trim();

    const tableRows = document.querySelectorAll('.user-row');

    tableRows.forEach(row => {
        const cells = row.cells;
        if (cells.length < 6) return;

        const rowFirstName = cells[0].textContent.toLowerCase().trim();
        const rowLastName = cells[1].textContent.toLowerCase().trim();

        const matchesFirstName = !firstName || rowFirstName.includes(firstName);
        const matchesLastName = !lastName || rowLastName.includes(lastName);

        const isVisible = matchesFirstName && matchesLastName;

        if (isVisible) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

function filterSessions() {
    const firstName = document.getElementById('globalFirstNameSearch').value.toLowerCase().trim();
    const lastName = document.getElementById('globalLastNameSearch').value.toLowerCase().trim();

    const tableRows = document.querySelectorAll('.session-row');

    tableRows.forEach(row => {
        const teacherNameCell = row.querySelector('.teacher-name');
        if (!teacherNameCell) return;

        const teacherFullName = teacherNameCell.textContent.toLowerCase().trim();
        const nameParts = teacherFullName.split(' ');

        // Extract first and last name from teacher full name
        const teacherFirstName = nameParts[0] || '';
        const teacherLastName = nameParts.slice(1).join(' ') || '';

        const matchesFirstName = !firstName || teacherFirstName.includes(firstName);
        const matchesLastName = !lastName || teacherLastName.includes(lastName);

        const isVisible = matchesFirstName && matchesLastName;

        if (isVisible) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

function clearGlobalSearch() {
    document.getElementById('globalFirstNameSearch').value = '';
    document.getElementById('globalLastNameSearch').value = '';
    filterCurrentTab();
}


// ========================================
// CONFIRMATION ACTIONS FROM MODALS
// ========================================

function confirmDeleteTeacherFromModal(teacherId) {
    showConfirmation(
        'Supprimer le Teacher',
        'Êtes-vous sûr de vouloir supprimer définitivement ce teacher ?',
        function() {
            const modal = mdb.Modal.getInstance(document.getElementById('teacherUpdateModal'));
            modal.hide();
            submitAction('/admin/teachers/' + teacherId + '/delete');
        }
    );
}

function confirmDisableTeacherFromModal(teacherId) {
    showConfirmation(
        'Désactiver le Teacher',
        'Êtes-vous sûr de vouloir désactiver ce teacher ?',
        function() {
            const modal = mdb.Modal.getInstance(document.getElementById('teacherUpdateModal'));
            modal.hide();
            submitAction('/admin/teachers/' + teacherId + '/disable');
        }
    );
}

function confirmEnableTeacherFromModal(teacherId) {
    showConfirmation(
        'Activer le Teacher',
        'Êtes-vous sûr de vouloir activer ce teacher ?',
        function() {
            const modal = mdb.Modal.getInstance(document.getElementById('teacherUpdateModal'));
            modal.hide();
            submitAction('/admin/teachers/' + teacherId + '/enable');
        }
    );
}

function confirmDeleteUserFromModal(userId) {
    showConfirmation(
        'Supprimer le Client',
        'Êtes-vous sûr de vouloir supprimer définitivement ce client ?',
        function() {
            const modal = mdb.Modal.getInstance(document.getElementById('userUpdateModal'));
            modal.hide();
            submitAction('/admin/users/' + userId + '/delete');
        }
    );
}

function confirmDisableUserFromModal(userId) {
    showConfirmation(
        'Désactiver le Client',
        'Êtes-vous sûr de vouloir désactiver ce client ?',
        function() {
            const modal = mdb.Modal.getInstance(document.getElementById('userUpdateModal'));
            modal.hide();
            submitAction('/admin/users/' + userId + '/disable');
        }
    );
}

function confirmEnableUserFromModal(userId) {
    showConfirmation(
        'Activer le Client',
        'Êtes-vous sûr de vouloir activer ce client ?',
        function() {
            const modal = mdb.Modal.getInstance(document.getElementById('userUpdateModal'));
            modal.hide();
            submitAction('/admin/users/' + userId + '/enable');
        }
    );
}
