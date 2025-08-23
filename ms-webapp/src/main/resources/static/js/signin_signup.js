// Form validation for signin and signup pages
(() => {
    'use strict';

    // Wait for DOM to be ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initializePage);
    } else {
        initializePage();
    }

    function initializePage() {
        loadSavedEmail();
        initializeFormValidation();
        handleAlerts();
    }

    function loadSavedEmail() {
        const emailInput = document.getElementById('email');
        const savedEmail = localStorage.getItem('lastEmail');

        if (emailInput && savedEmail && window.location.pathname.includes('signin')) {
            emailInput.value = savedEmail;
        }
    }

    function initializeFormValidation() {
        const form = document.querySelector('.needs-validation');
        if (!form) return;

        let formSubmitted = false;

        // Handle form submission - VERSION SIMPLIFIEE
        form.addEventListener('submit', event => {
            formSubmitted = true;

            // Save email for signin
            const emailInput = document.getElementById('email');
            if (emailInput && window.location.pathname.includes('signin')) {
                localStorage.setItem('lastEmail', emailInput.value);
            }

            // Handle password confirmation for signup ONLY
            if (window.location.pathname.includes('signup')) {
                const password = document.getElementById('password');
                const confirmPassword = document.getElementById('confirmPassword');

                if (password && confirmPassword && password.value !== confirmPassword.value) {
                    confirmPassword.setCustomValidity('Les mots de passe ne correspondent pas');
                    event.preventDefault();
                    event.stopPropagation();
                    form.classList.add('was-validated');
                    return;
                } else if (confirmPassword) {
                    confirmPassword.setCustomValidity('');
                }
            }

            // Let HTML5 validation handle the rest
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
                form.classList.add('was-validated');
            }
        });

        // Visual feedback only - no validation logic
        setupVisualFeedback(form);
    }

    function setupVisualFeedback(form) {
        const inputs = form.querySelectorAll('.form-control');

        inputs.forEach(input => {
            input.addEventListener('input', function() {
                // Remove any previous custom validity for regular fields
                if (this.id !== 'confirmPassword') {
                    this.setCustomValidity('');
                }

                // Visual feedback only
                if (this.value) {
                    if (this.checkValidity()) {
                        this.classList.add('is-valid');
                        this.classList.remove('is-invalid');
                    } else {
                        this.classList.add('is-invalid');
                        this.classList.remove('is-valid');
                    }
                }
            });

            input.addEventListener('blur', function() {
                if (this.value) {
                    if (this.checkValidity()) {
                        this.classList.add('is-valid');
                        this.classList.remove('is-invalid');
                    } else {
                        this.classList.add('is-invalid');
                        this.classList.remove('is-valid');
                    }
                }
            });
        });

        // Special handling for password validation in signup
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');

        if (password) {
            const originalPasswordPlaceholder = password.placeholder;
            const passwordLabel = document.querySelector('label[for="password"]');

            password.addEventListener('input', function() {
                const pattern = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

                if (this.value) {
                    if (pattern.test(this.value)) {
                        this.classList.add('is-valid');
                        this.classList.remove('is-invalid');
                        this.placeholder = originalPasswordPlaceholder;
                        if (passwordLabel) passwordLabel.textContent = 'Mot de passe';
                    } else {
                        this.classList.add('is-invalid');
                        this.classList.remove('is-valid');
                        this.placeholder = '8 caractères, majuscule, chiffre, symbole (@$!%*?&)';
                        if (passwordLabel) passwordLabel.textContent = '8 caractères, majuscule, chiffre, symbole';
                    }
                } else {
                    this.classList.remove('is-valid', 'is-invalid');
                    this.placeholder = originalPasswordPlaceholder;
                    if (passwordLabel) passwordLabel.textContent = 'Mot de passe';
                }
            });
        }

        if (confirmPassword && password) {
            const originalConfirmPlaceholder = confirmPassword.placeholder;
            const confirmLabel = document.querySelector('label[for="confirmPassword"]');

            confirmPassword.addEventListener('input', function() {
                if (this.value) {
                    if (password.value === this.value) {
                        this.setCustomValidity('');
                        this.classList.add('is-valid');
                        this.classList.remove('is-invalid');
                        this.placeholder = originalConfirmPlaceholder;
                        if (confirmLabel) confirmLabel.textContent = 'Confirmer le mot de passe';
                    } else {
                        this.setCustomValidity('Les mots de passe ne correspondent pas');
                        this.classList.add('is-invalid');
                        this.classList.remove('is-valid');
                        this.placeholder = 'Les mots de passe ne correspondent pas';
                        if (confirmLabel) confirmLabel.textContent = 'Les mots de passe ne correspondent pas';
                    }
                } else {
                    this.classList.remove('is-valid', 'is-invalid');
                    this.placeholder = originalConfirmPlaceholder;
                    if (confirmLabel) confirmLabel.textContent = 'Confirmer le mot de passe';
                }
            });
        }
    }

    function handleAlerts() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            setTimeout(() => {
                alert.classList.remove('show');
                setTimeout(() => alert.remove(), 500);
            }, 5000);
        });
    }
})();