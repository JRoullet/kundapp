// Admin JavaScript - Gestion des onglets Teachers/Users
// Simple vanilla JS - Form submissions only

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Admin page loaded');

    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.3s ease';
            alert.style.opacity = '0';

            setTimeout(() => {
                alert.remove();
            }, 500);
        }, 3000);
    });
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
            alert('Erreur lors du chargement des données du teacher');
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
    document.getElementById('userModalTitle').textContent = 'Modifier le Client';
    document.getElementById('userId').value = userId;
    document.getElementById('userForm').action = '/admin/users/' + userId + '/update';

    const modal = new mdb.Modal(document.getElementById('userModal'));
    modal.show();
}

function openCreditsModal(userId) {
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
    document.body.appendChild(form);
    form.submit();

}