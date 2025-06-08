// Admin Dashboard JavaScript
// Uses existing MDB functionality and minimal custom JS

// Store all users data for client-side filtering
let allUsers = [];

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Store users data passed from Thymeleaf for client-side filtering
    // This will be populated by Thymeleaf in the template
    showTab('users');
    loadUsersData();
});

// Load users data from the page (populated by Thymeleaf)
function loadUsersData() {
    // Extract users data from DOM or use a global variable set by Thymeleaf
    // This function will be populated with actual data
}

// Tab management - simple show/hide
function showTab(tabName) {
    // Hide all tab contents
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });

    // Remove active class from all nav links
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });

    // Show selected tab and mark nav link as active
    document.getElementById(tabName + '-content').classList.add('active');
    document.getElementById(tabName + '-tab').classList.add('active');
}

// Client-side filtering functions
function filterUsers() {
    const firstName = document.getElementById('userFirstNameSearch').value.toLowerCase();
    const lastName = document.getElementById('userLastNameSearch').value.toLowerCase();

    const rows = document.querySelectorAll('#users-content tbody tr');

    rows.forEach(row => {
        // Skip empty state row
        if (row.cells.length < 7) return;

        const rowFirstName = row.cells[0].textContent.toLowerCase();
        const rowLastName = row.cells[1].textContent.toLowerCase();

        const matches = (!firstName || rowFirstName.includes(firstName)) &&
            (!lastName || rowLastName.includes(lastName));

        row.style.display = matches ? '' : 'none';
    });
}

function filterTeachers() {
    const firstName = document.getElementById('teacherFirstNameSearch').value.toLowerCase();
    const lastName = document.getElementById('teacherLastNameSearch').value.toLowerCase();

    const rows = document.querySelectorAll('#teachers-content tbody tr');

    rows.forEach(row => {
        // Skip empty state row
        if (row.cells.length < 7) return;

        const rowFirstName = row.cells[0].textContent.toLowerCase();
        const rowLastName = row.cells[1].textContent.toLowerCase();

        const matches = (!firstName || rowFirstName.includes(firstName)) &&
            (!lastName || rowLastName.includes(lastName));

        row.style.display = matches ? '' : 'none';
    });
}

// Clear search functions
function clearUserSearch() {
    document.getElementById('userFirstNameSearch').value = '';
    document.getElementById('userLastNameSearch').value = '';
    filterUsers(); // Show all users again
}

function clearTeacherSearch() {
    document.getElementById('teacherFirstNameSearch').value = '';
    document.getElementById('teacherLastNameSearch').value = '';
    filterTeachers(); // Show all teachers again
}

// Modal management functions
function openCreateUserModal() {
    document.getElementById('userModalTitle').textContent = 'Créer un client';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('userForm').action = '/admin/users';
    document.getElementById('userRole').value = 'CLIENT'; // Default to CLIENT
    toggleRoleSpecificFields();
    new mdb.Modal(document.getElementById('userModal')).show();
}

function openCreateTeacherModal() {
    document.getElementById('userModalTitle').textContent = 'Créer un teacher';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('userForm').action = '/admin/users';
    document.getElementById('userRole').value = 'TEACHER';
    toggleRoleSpecificFields();
    new mdb.Modal(document.getElementById('userModal')).show();
}

function openEditUserModal(userId) {
    document.getElementById('userModalTitle').textContent = 'Modifier l\'utilisateur';
    document.getElementById('userId').value = userId;
    document.getElementById('userForm').action = '/admin/users/' + userId;
    document.getElementById('httpMethod').value = 'PATCH';  // Force PATCH method
    // TODO: Populate form with user data from page data or hidden fields
    new mdb.Modal(document.getElementById('userModal')).show();
}

function openEditTeacherModal(teacherId) {
    document.getElementById('userModalTitle').textContent = 'Modifier le teacher';
    document.getElementById('userId').value = teacherId;
    document.getElementById('userForm').action = '/admin/users/' + teacherId;
    document.getElementById('httpMethod').value = 'PATCH';  // Force PATCH method
    // TODO: Populate form with teacher data from page data or hidden fields
    new mdb.Modal(document.getElementById('userModal')).show();
}

// Confirmation functions avec méthodes HTTP appropriées
function confirmDisableUser(userId) {
    showConfirmation(
        'Désactiver l\'utilisateur',
        'Êtes-vous sûr de vouloir désactiver cet utilisateur ?',
        function() {
            // Create form with PATCH method
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/admin/users/' + userId + '/disable';

            // Add hidden field for PATCH method
            const methodInput = document.createElement('input');
            methodInput.type = 'hidden';
            methodInput.name = '_method';
            methodInput.value = 'PATCH';
            form.appendChild(methodInput);

            document.body.appendChild(form);
            form.submit();
        }
    );
}

function confirmDeleteUser(userId) {
    showConfirmation(
        'Supprimer l\'utilisateur',
        'Êtes-vous sûr de vouloir supprimer définitivement cet utilisateur ? Cette action est irréversible.',
        function() {
            // Create form with DELETE method
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/admin/users/' + userId;

            // Add hidden field for DELETE method
            const methodInput = document.createElement('input');
            methodInput.type = 'hidden';
            methodInput.name = '_method';
            methodInput.value = 'DELETE';
            form.appendChild(methodInput);

            document.body.appendChild(form);
            form.submit();
        }
    );
}

// Credits modal management
function openCreditsModal(userId) {
    document.getElementById('creditsUserId').value = userId;
    // TODO: Load user data to populate name and current credits
    document.getElementById('creditsUserName').value = 'User Name'; // Replace with actual data
    new mdb.Modal(document.getElementById('creditsModal')).show();
}

// Role-specific field toggle
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('userRole').addEventListener('change', toggleRoleSpecificFields);
});

function toggleRoleSpecificFields() {
    const role = document.getElementById('userRole').value;
    const creditsSection = document.getElementById('creditsSection');
    const biographySection = document.getElementById('biographySection');

    if (role === 'CLIENT') {
        creditsSection.style.display = 'block';
        biographySection.style.display = 'none';
    } else if (role === 'TEACHER') {
        creditsSection.style.display = 'none';
        biographySection.style.display = 'block';
    } else {
        creditsSection.style.display = 'none';
        biographySection.style.display = 'none';
    }
}

// Search functions - replaced by client-side filtering above
// Keep these for backward compatibility or future server-side search
function searchUsers() {
    filterUsers();
}

function searchTeachers() {
    filterTeachers();
}

// Confirmation functions
function confirmDisableUser(userId) {
    showConfirmation(
        'Désactiver l\'utilisateur',
        'Êtes-vous sûr de vouloir désactiver cet utilisateur ?',
        function() {
            // Create and submit form for disable action
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/admin/users/' + userId + '/disable';
            document.body.appendChild(form);
            form.submit();
        }
    );
}

function confirmDeleteUser(userId) {
    showConfirmation(
        'Supprimer l\'utilisateur',
        'Êtes-vous sûr de vouloir supprimer définitivement cet utilisateur ? Cette action est irréversible.',
        function() {
            // Create and submit form for delete action
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/admin/users/' + userId + '/delete';
            document.body.appendChild(form);
            form.submit();
        }
    );
}

function confirmDisableTeacher(teacherId) {
    showConfirmation(
        'Désactiver le teacher',
        'Êtes-vous sûr de vouloir désactiver ce teacher ?',
        function() {
            // Create and submit form for disable action
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/admin/users/' + teacherId + '/disable';
            document.body.appendChild(form);
            form.submit();
        }
    );
}

function confirmDeleteTeacher(teacherId) {
    showConfirmation(
        'Supprimer le teacher',
        'Êtes-vous sûr de vouloir supprimer définitivement ce teacher ? Cette action est irréversible.',
        function() {
            // Create and submit form for delete action
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/admin/users/' + teacherId + '/delete';
            document.body.appendChild(form);
            form.submit();
        }
    );
}

// Generic confirmation modal
function showConfirmation(title, message, callback) {
    document.getElementById('confirmationTitle').textContent = title;
    document.getElementById('confirmationMessage').textContent = message;
    document.getElementById('confirmButton').onclick = function() {
        callback();
        mdb.Modal.getInstance(document.getElementById('confirmationModal')).hide();
    };
    new mdb.Modal(document.getElementById('confirmationModal')).show();
}