// Admin JavaScript - Teachers/Users tabs management

// Toast System for errors management
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
            error: '✕'
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
}

// Initialize global toast system
const toastSystem = new ToastSystem();

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Admin page loaded');

    // Manage tabs on URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const activeTab = urlParams.get('tab');

    if (activeTab === 'users') {
        // Enable users tab
        document.getElementById('teachers-tab').classList.remove('active');
        document.getElementById('users-tab').classList.add('active');
        document.getElementById('teachers-content').classList.remove('show', 'active');
        document.getElementById('users-content').classList.add('show', 'active');
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
    // Configurer le formulaire
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
            // Champs de base
            document.getElementById('teacherUpdateFirstName').value = teacher.firstName || '';
            document.getElementById('teacherUpdateLastName').value = teacher.lastName || '';
            document.getElementById('teacherUpdateEmail').value = teacher.email || '';
            document.getElementById('teacherUpdatePhone').value = teacher.phoneNumber || '';
            document.getElementById('teacherUpdateBirthDate').value = teacher.dateOfBirth || '';
            document.getElementById('teacherUpdateBiography').value = teacher.biography || '';

            // Champs d'adresse depuis l'objet address
            const address = teacher.address || {};
            document.getElementById('teacherUpdateStreet').value = address.street || '';
            document.getElementById('teacherUpdateCity').value = address.city || '';
            document.getElementById('teacherUpdateZipCode').value = address.zipCode || '';
            document.getElementById('teacherUpdateCountry').value = address.country || '';

            // Afficher le modal
            const modal = new mdb.Modal(document.getElementById('teacherUpdateModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Erreur lors du chargement des données du teacher:', error);
            toastSystem.error('Erreur lors du chargement des données du teacher');
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
    // Configurer le formulaire
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
            // Préremplir les champs
            document.getElementById('userUpdateFirstName').value = user.firstName || '';
            document.getElementById('userUpdateLastName').value = user.lastName || '';
            document.getElementById('userUpdateEmail').value = user.email || '';
            document.getElementById('userUpdatePhone').value = user.phoneNumber || '';
            document.getElementById('userUpdateBirthDate').value = user.dateOfBirth || '';
            document.getElementById('userUpdateCredits').value = user.credits || 0;
            // Champs d'adresse depuis l'objet address
            const address = user.address || {};
            document.getElementById('userUpdateStreet').value = address.street || '';
            document.getElementById('userUpdateCity').value = address.city || '';
            document.getElementById('userUpdateZipCode').value = address.zipCode || '';
            document.getElementById('userUpdateCountry').value = address.country || '';

            // Afficher le modal
            const modal = new mdb.Modal(document.getElementById('userUpdateModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Erreur lors du chargement des données du user:', error);
            toastSystem.error('Erreur lors du chargement des données du user');
        });
}

function openCreditsModal(userId) {
    document.getElementById('creditsForm').action = '/admin/users/' + userId + '/credits/add';
    document.getElementById('creditsUserId').value = userId;

    const modal = new mdb.Modal(document.getElementById('creditsModal'));
    modal.show();
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

function clearGlobalSearch() {
    document.getElementById('globalFirstNameSearch').value = '';
    document.getElementById('globalLastNameSearch').value = '';
    filterCurrentTab();
}

// Fonctions maintenues pour compatibilité (peuvent être supprimées si non utilisées ailleurs)
function clearTeacherSearch() {
    clearGlobalSearch();
}

function clearUserSearch() {
    clearGlobalSearch();
}

// ========================================
// CONFIRMATION ACTIONS
// ========================================

function confirmDisableTeacher(teacherId) {
    showConfirmation(
        'Désactiver le Teacher',
        'Êtes-vous sûr de vouloir désactiver ce teacher ?',
        function() {
            submitAction('/admin/teachers/' + teacherId + '/disable');
        }
    );
}

function confirmEnableTeacher(teacherId) {
    showConfirmation(
        'Activer le Teacher',
        'Êtes-vous sûr de vouloir activer ce teacher ?',
        function() {
            submitAction('/admin/teachers/' + teacherId + '/enable');
        }
    );
}

function confirmDeleteTeacher(teacherId) {
    showConfirmation(
        'Supprimer le Teacher',
        'Êtes-vous sûr de vouloir supprimer définitivement ce teacher ?',
        function() {
            submitAction('/admin/teachers/' + teacherId + '/delete');
        }
    );
}

function confirmDisableUser(userId) {
    showConfirmation(
        'Désactiver le Client',
        'Êtes-vous sûr de vouloir désactiver ce client ?',
        function() {
            submitAction('/admin/users/' + userId + '/disable');
        }
    );
}

function confirmEnableUser(userId) {
    showConfirmation(
        'Activer le Client',
        'Êtes-vous sûr de vouloir activer ce client ?',
        function() {
            submitAction('/admin/users/' + userId + '/enable');
        }
    );
}

function confirmDeleteUser(userId) {
    showConfirmation(
        'Supprimer le Client',
        'Êtes-vous sûr de vouloir supprimer définitivement ce client ?',
        function() {
            submitAction('/admin/users/' + userId + '/delete');
        }
    );
}

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