// Common JavaScript - Shared functionality across all pages
// Modern Vanilla JS with Fetch API

// ========================================
// TOAST SYSTEM - Centralized for all pages
// ========================================

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

        // Use requestAnimationFrame for smooth animations
        requestAnimationFrame(() => {
            toast.classList.add('show');
        });

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
            error: '✕',
            info: 'ℹ'
        };

        toast.innerHTML = `
            <button class="toast-close" type="button">×</button>
            <div class="toast-content">
                <div class="toast-icon">${icons[type] || 'ℹ'}</div>
                <div class="toast-message">
                    <strong>${title}</strong>
                    ${message ? `<div style="margin-top: 4px; opacity: 0.9;">${message}</div>` : ''}
                </div>
            </div>
        `;

        // Modern event listeners
        const closeBtn = toast.querySelector('.toast-close');
        const hideToast = () => this.hide(toast);

        closeBtn.addEventListener('click', hideToast);
        toast.addEventListener('click', hideToast);

        return toast;
    }

    hide(toast) {
        if (!toast?.parentElement) return;

        toast.classList.remove('show');
        toast.classList.add('hide');

        // Modern transition handling
        const handleTransitionEnd = () => {
            if (toast.parentElement) {
                toast.remove();
            }
            this.toasts = this.toasts.filter(t => t !== toast);
            toast.removeEventListener('transitionend', handleTransitionEnd);
        };

        toast.addEventListener('transitionend', handleTransitionEnd);

        // Fallback timeout
        setTimeout(handleTransitionEnd, 500);
    }

    success(title, message, duration) {
        return this.show('success', title, message, duration);
    }

    error(title, message, duration) {
        return this.show('error', title, message, duration);
    }

    info(title, message, duration) {
        return this.show('info', title, message, duration);
    }
}

// Initialize global toast system
const toastSystem = new ToastSystem();

// ========================================
// THYMELEAF MESSAGE HANDLER
// ========================================

class ThymeleafMessageHandler {
    constructor() {
        this.init();
    }

    init() {
        document.addEventListener('DOMContentLoaded', () => {
            this.handleMessages();
        });
    }

    handleMessages() {
        // Get messages from meta tags (cleanest approach for Thymeleaf)
        const successMeta = document.querySelector('meta[name="app-success"]');
        const errorMeta = document.querySelector('meta[name="app-error"]');

        if (successMeta?.content && successMeta.content !== 'null') {
            toastSystem.success('Succès', successMeta.content);
        }

        if (errorMeta?.content && errorMeta.content !== 'null') {
            toastSystem.error('Erreur', errorMeta.content);
        }
    }
}

// ========================================
// API CLIENT - Modern Fetch API wrapper
// ========================================

class ApiClient {
    constructor() {
        this.baseHeaders = {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };
    }

    // Get CSRF token from meta tag
    getCsrfToken() {
        return document.querySelector('meta[name="_csrf"]')?.content || '';
    }

    // Headers with CSRF
    getHeaders(additionalHeaders = {}) {
        return {
            ...this.baseHeaders,
            'X-CSRF-TOKEN': this.getCsrfToken(),
            ...additionalHeaders
        };
    }

    // Modern GET with Fetch API
    async get(url) {
        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: this.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            return await response.json();
        } catch (error) {
            console.error('GET Error:', error);
            throw error;
        }
    }

    // Modern POST with Fetch API
    async post(url, data = {}) {
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            return await response.json();
        } catch (error) {
            console.error('POST Error:', error);
            throw error;
        }
    }

    // Modern DELETE
    async delete(url) {
        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: this.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            return response.status === 204 ? null : await response.json();
        } catch (error) {
            console.error('DELETE Error:', error);
            throw error;
        }
    }
}

// ========================================
// COMMON UTILITIES
// ========================================

class CommonUtils {
    // Modern debounce
    static debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // Form serialization utility
    static serializeForm(form) {
        return Object.fromEntries(new FormData(form));
    }

    // CSRF token helper
    static getCsrfToken() {
        return document.querySelector('meta[name="_csrf"]')?.content || '';
    }
}

// ========================================
// COMMON FORM ACTIONS
// ========================================

// Confirmation modal logic (used by admin and teacher)
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

// Submit form action with CSRF (used by admin)
function submitAction(actionUrl) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = actionUrl;
    form.style.display = 'none';

    // Add CSRF token
    const csrfToken = CommonUtils.getCsrfToken();
    const csrfInput = document.createElement('input');
    csrfInput.type = 'hidden';
    csrfInput.name = '_csrf';
    csrfInput.value = csrfToken;
    form.appendChild(csrfInput);

    document.body.appendChild(form);
    form.submit();
    document.body.removeChild(form);
}

// TODO DOUBLE METHOD TO Refactor when possible (change endpoints in admin/teacher for each pathvariable to requestparam)
// Submit form action with CSRF and parameters (NEW function for teacher cancel)
function submitActionWithParams(actionUrl, params = {}) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = actionUrl;
    form.style.display = 'none';

    // Add CSRF token
    const csrfToken = CommonUtils.getCsrfToken();
    const csrfInput = document.createElement('input');
    csrfInput.type = 'hidden';
    csrfInput.name = '_csrf';
    csrfInput.value = csrfToken;
    form.appendChild(csrfInput);

    // Add parameters
    Object.entries(params).forEach(([key, value]) => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = value;
        form.appendChild(input);
    });

    document.body.appendChild(form);
    form.submit();
    document.body.removeChild(form);
}

// Initialize global instances
const apiClient = new ApiClient();
const messageHandler = new ThymeleafMessageHandler();