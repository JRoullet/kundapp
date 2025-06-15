// Admin Teachers JavaScript
// Manages teacher creation, editing, deletion and search functionality

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Admin Teachers page loaded');
    initializeValidation();
});

// ========================================
// TEACHER MODAL MANAGEMENT
// ========================================

/**
 * Open modal for creating new teacher
 */
function openCreateTeacherModal() {
    document.getElementById('teacherModalTitle').innerHTML =
        '<i class="fas fa-chalkboard-teacher me-2"></i>Créer un Teacher';
    document.getElementById('teacherForm').reset();
    document.getElementById('teacherId').value = '';
    document.getElementById('teacherForm').action = '/admin/teachers';
    new mdb.Modal(document.getElementById('teacherModal')).show();
}

/**
 * Open modal for editing existing teacher
 * @param {number} teacherId - ID of teacher to edit
 */
function openEditTeacherModal(teacherId) {
    document.getElementById('teacherModalTitle').innerHTML =
        '<i class="fas fa-edit me-2"></i>Modifier le Teacher';
    document.getElementById('teacherId').value = teacherId;
    document.getElementById('teacherForm').action = '/admin/teachers/' + teacherId;

    // TODO: Load teacher data from server and populate form fields
    // This would typically be done via a fetch request to get teacher details
    // populateTeacherForm(teacherId);

    new mdb.Modal(document.getElementById('teacherModal')).show();
}

/**
 * Populate form fields with teacher data (for editing)
 * @param {number} teacherId - ID of teacher to load data for
 */
function populateTeacherForm(teacherId) {
    // TODO: Implement when edit endpoint is available
    // fetch('/admin/teachers/' + teacherId + '/data')
    //     .then(response => response.json())
    //     .then(teacher => {
    //         document.getElementById('teacherFirstName').value = teacher.firstName || '';
    //         document.getElementById('teacherLastName').value = teacher.lastName || '';
    //         document.getElementById('teacherEmail').value = teacher.email || '';
    //         document.getElementById('teacherPhone').value = teacher.phoneNumber || '';
    //         document.getElementById('teacherBiography').value = teacher.biography || '';
    //         document.getElementById('teacherBirthDate').value = teacher.dateOfBirth || '';
    //     })
    //     .catch(error => console.error('Error loading teacher data:', error));
}

// ========================================
// SEARCH AND FILTER FUNCTIONALITY
// ========================================

/**
 * Filter teachers table based on search inputs
 */
function filterTeachers() {
    const firstName = document.getElementById('teacherFirstNameSearch').value.toLowerCase().trim();
    const lastName = document.getElementById('teacherLastNameSearch').value.toLowerCase().trim();

    const tableRows = document.querySelectorAll('.admin-table-row');
    let visibleCount = 0;

    tableRows.forEach(row => {
        // Skip if this is not a teacher row
        const cells = row.cells;
        if (cells.length < 6) return;

        const rowFirstName = cells[0].textContent.toLowerCase().trim();
        const rowLastName = cells[1].textContent.toLowerCase().trim();

        const matchesFirstName = !firstName || rowFirstName.includes(firstName);
        const matchesLastName = !lastName || rowLastName.includes(lastName);

        const isVisible = matchesFirstName && matchesLastName;

        if (isVisible) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });

    // Show/hide empty state based on visible results
    updateEmptyState(visibleCount === 0);
}

/**
 * Clear all search filters
 */
function clearTeacherSearch() {
    document.getElementById('teacherFirstNameSearch').value = '';
    document.getElementById('teacherLastNameSearch').value = '';
    filterTeachers(); // Reset table display
}

/**
 * Update empty state visibility
 * @param {boolean} show - Whether to show empty state
 */
function updateEmptyState(show) {
    const emptyStateRow = document.querySelector('.admin-empty-state');
    if (emptyStateRow) {
        emptyStateRow.style.display = show ? '' : 'none';
    }
}

// ========================================
// CONFIRMATION ACTIONS
// ========================================

/**
 * Confirm teacher disable action
 * @param {number} teacherId - ID of teacher to disable
 */
function confirmDisableTeacher(teacherId) {
    showConfirmation(
        '<i class="fas fa-user-slash me-2"></i>Désactiver le Teacher',
        'Êtes-vous sûr de vouloir désactiver ce teacher ? Il ne pourra plus se connecter à l\'application.',
        function() {
            disableTeacher(teacherId);
        },
        'warning'
    );
}

/**
 * Confirm teacher deletion
 * @param {number} teacherId - ID of teacher to delete
 */
function confirmDeleteTeacher(teacherId) {
    showConfirmation(
        '<i class="fas fa-trash-alt me-2"></i>Supprimer le Teacher',
        'Êtes-vous sûr de vouloir supprimer définitivement ce teacher ? Cette action est irréversible et supprimera également toutes ses sessions associées.',
        function() {
            deleteTeacher(teacherId);
        },
        'danger'
    );
}

/**
 * Show confirmation modal with custom content
 * @param {string} title - Modal title
 * @param {string} message - Confirmation message
 * @param {function} callback - Function to execute on confirmation
 * @param {string} type - Type of confirmation (warning, danger)
 */
function showConfirmation(title, message, callback, type = 'warning') {
    document.getElementById('confirmationTitle').innerHTML = title;
    document.getElementById('confirmationMessage').textContent = message;

    const confirmBtn = document.getElementById('confirmButton');
    confirmBtn.className = type === 'danger' ? 'btn btn-admin-danger' : 'btn btn-admin-secondary';

    // Update confirm button icon based on type
    const iconClass = type === 'danger' ? 'fas fa-trash-alt' : 'fas fa-check';
    confirmBtn.innerHTML = `<i class="${iconClass} me-1"></i>Confirmer`;

    confirmBtn.onclick = function() {
        callback();
        mdb.Modal.getInstance(document.getElementById('confirmationModal')).hide();
    };

    new mdb.Modal(document.getElementById('confirmationModal')).show();
}

// ========================================
// TEACHER ACTIONS (CRUD OPERATIONS)
// ========================================

/**
 * Disable teacher account
 * @param {number} teacherId - ID of teacher to disable
 */
function disableTeacher(teacherId) {
    // Create form for disable action using POST method
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/admin/teachers/' + teacherId + '/disable';

    // Add CSRF token if needed
    addCSRFToken(form);

    document.body.appendChild(form);
    form.submit();
}

/**
 * Delete teacher permanently
 * @param {number} teacherId - ID of teacher to delete
 */
function deleteTeacher(teacherId) {
    // Create form for delete action using POST method
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/admin/teachers/' + teacherId + '/delete';

    // Add CSRF token if needed
    addCSRFToken(form);

    document.body.appendChild(form);
    form.submit();
}

/**
 * Add CSRF token to form if available
 * @param {HTMLFormElement} form - Form to add token to
 */
function addCSRFToken(form) {
    const csrfToken = document.querySelector('meta[name="_csrf"]');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]');

    if (csrfToken && csrfHeader) {
        const tokenInput = document.createElement('input');
        tokenInput.type = 'hidden';
        tokenInput.name = '_token';
        tokenInput.value = csrfToken.getAttribute('content');
        form.appendChild(tokenInput);
    }
}

// ========================================
// FORM VALIDATION
// ========================================

/**
 * Initialize form validation
 */
function initializeValidation() {
    const teacherForm = document.getElementById('teacherForm');
    if (!teacherForm) return;

    // Add validation event listeners
    const emailInput = document.getElementById('teacherEmail');
    const passwordInput = document.getElementById('teacherPassword');
    const firstNameInput = document.getElementById('teacherFirstName');
    const lastNameInput = document.getElementById('teacherLastName');

    // Email validation
    if (emailInput) {
        emailInput.addEventListener('blur', validateEmail);
        emailInput.addEventListener('input', clearValidationState);
    }

    // Password validation
    if (passwordInput) {
        passwordInput.addEventListener('blur', validatePassword);
        passwordInput.addEventListener('input', clearValidationState);
    }

    // Required field validation
    [firstNameInput, lastNameInput].forEach(input => {
        if (input) {
            input.addEventListener('blur', validateRequired);
            input.addEventListener('input', clearValidationState);
        }
    });

    // Form submission validation
    teacherForm.addEventListener('submit', handleFormSubmission);
}

/**
 * Validate email format and show feedback
 * @param {Event} event - Input event
 */
function validateEmail(event) {
    const input = event.target;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (input.value && !emailRegex.test(input.value)) {
        setInputState(input, 'invalid', 'Veuillez saisir une adresse email valide');
    } else if (input.value) {
        setInputState(input, 'valid', 'Email valide');
    }
}

/**
 * Validate password requirements
 * @param {Event} event - Input event
 */
function validatePassword(event) {
    const input = event.target;
    const minLength = 8;

    if (input.value && input.value.length < minLength) {
        setInputState(input, 'invalid', `Le mot de passe doit contenir au moins ${minLength} caractères`);
    } else if (input.value) {
        setInputState(input, 'valid', 'Mot de passe valide');
    }
}

/**
 * Validate required fields
 * @param {Event} event - Input event
 */
function validateRequired(event) {
    const input = event.target;

    if (!input.value.trim()) {
        setInputState(input, 'invalid', 'Ce champ est obligatoire');
    } else {
        setInputState(input, 'valid', '');
    }
}

/**
 * Set input validation state
 * @param {HTMLInputElement} input - Input element
 * @param {string} state - 'valid' or 'invalid'
 * @param {string} message - Validation message
 */
function setInputState(input, state, message) {
    // Remove existing validation classes
    input.classList.remove('is-valid', 'is-invalid');

    // Add appropriate class
    input.classList.add(state === 'valid' ? 'is-valid' : 'is-invalid');

    // Update or create feedback element
    let feedback = input.parentNode.querySelector('.invalid-feedback, .valid-feedback');
    if (!feedback) {
        feedback = document.createElement('div');
        feedback.className = state === 'valid' ? 'valid-feedback' : 'invalid-feedback';
        input.parentNode.appendChild(feedback);
    } else {
        feedback.className = state === 'valid' ? 'valid-feedback' : 'invalid-feedback';
    }

    feedback.textContent = message;
    feedback.style.display = message ? 'block' : 'none';
}

/**
 * Clear validation state from input
 * @param {Event} event - Input event
 */
function clearValidationState(event) {
    const input = event.target;
    input.classList.remove('is-valid', 'is-invalid');

    const feedback = input.parentNode.querySelector('.invalid-feedback, .valid-feedback');
    if (feedback) {
        feedback.style.display = 'none';
    }
}

/**
 * Handle form submission with validation
 * @param {Event} event - Submit event
 */
function handleFormSubmission(event) {
    const form = event.target;
    const inputs = form.querySelectorAll('input[required], input[type="email"]');
    let isValid = true;

    // Validate all required and special inputs
    inputs.forEach(input => {
        if (input.hasAttribute('required') && !input.value.trim()) {
            setInputState(input, 'invalid', 'Ce champ est obligatoire');
            isValid = false;
        } else if (input.type === 'email' && input.value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(input.value)) {
                setInputState(input, 'invalid', 'Veuillez saisir une adresse email valide');
                isValid = false;
            }
        } else if (input.type === 'password' && input.value && input.value.length < 8) {
            setInputState(input, 'invalid', 'Le mot de passe doit contenir au moins 8 caractères');
            isValid = false;
        }
    });

    // Prevent submission if validation fails
    if (!isValid) {
        event.preventDefault();
        event.stopPropagation();

        // Show first invalid field
        const firstInvalid = form.querySelector('.is-invalid');
        if (firstInvalid) {
            firstInvalid.focus();
        }

        return false;
    }

    // Show loading state on submit button
    const submitBtn = form.querySelector('button[type="submit"]');
    if (submitBtn) {
        const originalContent = submitBtn.innerHTML;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Enregistrement...';
        submitBtn.disabled = true;

        // Re-enable button after a delay (in case of validation errors)
        setTimeout(() => {
            submitBtn.innerHTML = originalContent;
            submitBtn.disabled = false;
        }, 5000);
    }

    return true;
}

// ========================================
// UTILITY FUNCTIONS
// ========================================

/**
 * Show success message (can be extended for toast notifications)
 * @param {string} message - Success message to display
 */
function showSuccessMessage(message) {
    console.log('Success:', message);
    // TODO: Implement toast notification system if needed
}

/**
 * Show error message (can be extended for toast notifications)
 * @param {string} message - Error message to display
 */
function showErrorMessage(message) {
    console.error('Error:', message);
    // TODO: Implement toast notification system if needed
}