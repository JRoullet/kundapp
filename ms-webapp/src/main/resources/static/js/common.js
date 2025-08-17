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

// ========================================
// SESSION MANAGEMENT - SHARED FUNCTIONS
// ========================================

// Session form contexts configuration
const SESSION_CONTEXTS = {
    CREATE: {
        prefix: '',
        radioName: 'sessionType',
        hiddenFieldId: 'sessionIsOnline',
        formId: 'sessionForm'
    },
    UPDATE_TEACHER: {
        prefix: 'Update',
        radioName: 'sessionUpdateType',
        hiddenFieldId: 'sessionUpdateIsOnline',
        formId: 'sessionUpdateForm'
    },
    UPDATE_ADMIN: {
        prefix: 'Update',
        radioName: 'sessionUpdateType',
        hiddenFieldId: 'sessionUpdateIsOnline',
        formId: 'sessionUpdateForm'
    }
};

// Initialize session type handlers for any context
function initializeSessionTypeHandlers(context) {
    const sessionTypeRadios = document.querySelectorAll(`input[name="${context.radioName}"]`);
    const isOnlineHidden = document.getElementById(context.hiddenFieldId);

    sessionTypeRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            const isOnline = this.value === 'true';
            isOnlineHidden.value = isOnline;
            toggleSessionFields(isOnline, context);

            // Clear conflicting fields when switching types (only for update forms)
            if (context !== SESSION_CONTEXTS.CREATE) {
                clearConflictingFields(isOnline, context);
            }
        });
    });
}

// Toggle session fields visibility based on online/IRL
function toggleSessionFields(isOnline, context) {
    const elements = getSessionFieldElements(context);

    if (!elements.isValid) {
        console.error(`Missing elements for session fields toggle with context:`, context);
        return;
    }

    if (isOnline) {
        // Show online fields, hide IRL fields
        elements.irlFields.style.display = 'none';
        elements.onlineFields.style.display = 'block';
        if (elements.materialSection) elements.materialSection.style.display = 'none';
        if (elements.locationTitle) elements.locationTitle.textContent = 'Paramètres de la session en ligne';

        // Update field requirements
        elements.roomNameInput.removeAttribute('required');
        elements.postalCodeInput.removeAttribute('required');
        elements.zoomLinkInput.setAttribute('required', 'required');

        // Clear IRL fields for CREATE form only
        if (context === SESSION_CONTEXTS.CREATE) {
            clearIrlFields(context);
        }

    } else {
        // Show IRL fields, hide online fields
        elements.irlFields.style.display = 'block';
        elements.onlineFields.style.display = 'none';
        if (elements.materialSection) elements.materialSection.style.display = 'block';
        if (elements.locationTitle) elements.locationTitle.textContent = 'Lieu de la séance';

        // Update field requirements
        elements.roomNameInput.setAttribute('required', 'required');
        elements.postalCodeInput.setAttribute('required', 'required');
        elements.zoomLinkInput.removeAttribute('required');

        // Clear online fields for CREATE form only
        if (context === SESSION_CONTEXTS.CREATE) {
            clearOnlineFields(context);
        }
    }
}

// Get session field elements based on context
function getSessionFieldElements(context) {
    const prefix = context.prefix;

    // Build element IDs based on context
    const irlFieldsId = prefix ? `session${prefix}IrlFields` : 'sessionIrlFields';
    const onlineFieldsId = prefix ? `session${prefix}OnlineFields` : 'sessionOnlineFields';
    const materialSectionId = prefix ? `session${prefix}MaterialSection` : 'sessionMaterialSection';
    const locationTitleId = prefix ? `session${prefix}LocationTitle` : 'sessionLocationTitle';

    const roomNameId = prefix ? `session${prefix}RoomName` : 'sessionRoomName';
    const postalCodeId = prefix ? `session${prefix}PostalCode` : 'sessionPostalCode';
    const zoomLinkId = prefix ? `session${prefix}ZoomLink` : 'sessionZoomLink';

    const elements = {
        irlFields: document.getElementById(irlFieldsId),
        onlineFields: document.getElementById(onlineFieldsId),
        materialSection: document.getElementById(materialSectionId),
        locationTitle: document.getElementById(locationTitleId),
        roomNameInput: document.getElementById(roomNameId),
        postalCodeInput: document.getElementById(postalCodeId),
        zoomLinkInput: document.getElementById(zoomLinkId)
    };

    // Validate required elements exist
    elements.isValid = !!(elements.irlFields && elements.onlineFields &&
        elements.roomNameInput && elements.postalCodeInput &&
        elements.zoomLinkInput);

    return elements;
}

// Clear conflicting fields when switching session types
function clearConflictingFields(isOnline, context) {
    if (isOnline) {
        clearIrlFields(context);
    } else {
        clearOnlineFields(context);
    }
}

// Clear IRL specific fields
function clearIrlFields(context) {
    const prefix = context.prefix;
    const roomNameId = prefix ? `session${prefix}RoomName` : 'sessionRoomName';
    const postalCodeId = prefix ? `session${prefix}PostalCode` : 'sessionPostalCode';
    const googleMapsId = prefix ? `session${prefix}GoogleMapsLink` : 'sessionGoogleMapsLink';
    const mattressId = prefix ? `session${prefix}BringYourMattress` : 'sessionBringYourMattress';

    const elements = {
        roomName: document.getElementById(roomNameId),
        postalCode: document.getElementById(postalCodeId),
        googleMaps: document.getElementById(googleMapsId),
        mattress: document.getElementById(mattressId)
    };

    if (elements.roomName) elements.roomName.value = '';
    if (elements.postalCode) elements.postalCode.value = '';
    if (elements.googleMaps) elements.googleMaps.value = '';
    if (elements.mattress) elements.mattress.checked = false;
}

// Clear online specific fields
function clearOnlineFields(context) {
    const prefix = context.prefix;
    const zoomLinkId = prefix ? `session${prefix}ZoomLink` : 'sessionZoomLink';

    const zoomElement = document.getElementById(zoomLinkId);
    if (zoomElement) zoomElement.value = '';
}

// Populate session type fields with existing data
function populateSessionTypeFields(isOnline, session, context) {
    const prefix = context.prefix;
    const sessionTypeOnlineId = prefix ? `session${prefix}TypeOnline` : 'sessionTypeOnline';
    const sessionTypeIrlId = prefix ? `session${prefix}TypeIrl` : 'sessionTypeIrl';
    const hiddenFieldId = context.hiddenFieldId;

    const sessionTypeOnline = document.getElementById(sessionTypeOnlineId);
    const sessionTypeIrl = document.getElementById(sessionTypeIrlId);
    const sessionIsOnlineHidden = document.getElementById(hiddenFieldId);

    if (isOnline) {
        if (sessionTypeOnline) sessionTypeOnline.checked = true;
        if (sessionIsOnlineHidden) sessionIsOnlineHidden.value = 'true';

        // Populate online fields
        const zoomLinkId = prefix ? `session${prefix}ZoomLink` : 'sessionZoomLink';
        const zoomElement = document.getElementById(zoomLinkId);
        if (zoomElement) zoomElement.value = session.zoomLink || '';

    } else {
        if (sessionTypeIrl) sessionTypeIrl.checked = true;
        if (sessionIsOnlineHidden) sessionIsOnlineHidden.value = 'false';

        // Populate IRL fields
        const irlFields = {
            roomName: prefix ? `session${prefix}RoomName` : 'sessionRoomName',
            postalCode: prefix ? `session${prefix}PostalCode` : 'sessionPostalCode',
            googleMapsLink: prefix ? `session${prefix}GoogleMapsLink` : 'sessionGoogleMapsLink',
            bringYourMattress: prefix ? `session${prefix}BringYourMattress` : 'sessionBringYourMattress'
        };

        Object.entries(irlFields).forEach(([field, id]) => {
            const element = document.getElementById(id);
            if (element) {
                if (field === 'bringYourMattress') {
                    element.checked = session.bringYourMattress || false;
                } else {
                    element.value = session[field] || '';
                }
            }
        });
    }

    // Apply field visibility
    toggleSessionFields(isOnline, context);
}

// Validate session fields based on type
function validateSessionTypeFields(formData) {
    const isOnline = formData.get('isOnline') === 'true';

    if (isOnline) {
        // Online session validation
        const zoomLink = formData.get('zoomLink');
        if (!zoomLink || zoomLink.trim() === '') {
            toastSystem.error('Erreur de validation', 'Le lien Zoom est obligatoire pour une session en ligne');
            return false;
        }

        const zoomRegex = /^https:\/\/(.*\.)?zoom\.(us|com)\/j\/\d+.*$/;
        if (!zoomRegex.test(zoomLink)) {
            toastSystem.error('Erreur de validation', 'Le lien doit être un lien Zoom valide');
            return false;
        }
    } else {
        // IRL session validation
        const roomName = formData.get('roomName');
        if (!roomName || roomName.trim().length === 0) {
            toastSystem.error('Erreur de validation', 'Le nom de la salle/lieu est obligatoire');
            return false;
        }

        const postalCode = formData.get('postalCode');
        const postalCodeRegex = /^(0[1-9]|[1-8][0-9]|9[0-8])\d{3}$/;
        if (!postalCode || !postalCodeRegex.test(postalCode)) {
            toastSystem.error('Erreur de validation', 'Le code postal doit être un code postal français valide (ex: 75001)');
            return false;
        }

        // Google Maps link validation (optional but if present must be valid)
        const googleMapsLink = formData.get('googleMapsLink');
        if (googleMapsLink && googleMapsLink.trim() !== '') {
            const googleMapsRegex = /^https:\/\/(maps\.google\.(com|fr)|maps\.app\.goo\.gl)\/.*/;
            if (!googleMapsRegex.test(googleMapsLink)) {
                toastSystem.error('Erreur de validation', 'Le lien doit être un lien Google Maps valide');
                return false;
            }
        }
    }

    return true;
}

// ========================================
// SESSION CREDITS CONSTRAINT MANAGEMENT
// ========================================

// ========================================
// SESSION CREDITS CONSTRAINT MANAGEMENT
// ========================================

/**
 * Apply credits field constraint based on participants count
 * @param {number} participantsCount - Number of registered participants
 */
function applyCreditsConstraint(participantsCount) {
    const creditsField = document.getElementById('sessionUpdateCreditsRequired');
    const creditsHelpText = creditsField?.parentElement?.querySelector('.form-text');

    if (!creditsField || !creditsHelpText) {
        console.error('Credits field or help text not found');
        return;
    }

    if (participantsCount > 0) {
        // Apply constraint - field becomes read-only
        creditsField.readOnly = true;
        creditsField.style.backgroundColor = '#f8f9fa';
        creditsField.style.cursor = 'not-allowed';

        creditsHelpText.innerHTML = '<span class="text-warning">Impossible de modifier : des participants se sont déjà inscrits à la séance. Annulez cette session et créez en une autre pour modifier le nombre de crédits.</span>';
        creditsHelpText.style.color = '#dc3545';
    } else {
        // Remove constraint - field becomes editable
        creditsField.readOnly = false;
        creditsField.style.backgroundColor = '';
        creditsField.style.cursor = '';
        creditsHelpText.innerHTML = 'Nombre de crédits nécessaires pour participer';
        creditsHelpText.style.color = '';
    }
}

// ========================================
// MODAL ACCESSIBILITY
// ========================================

// Fix for MDBootstrap modal accessibility issue
document.addEventListener('DOMContentLoaded', function() {
    // Handle modal focus issues when closing modals
    document.querySelectorAll('.modal').forEach(modal => {
        // MDBootstrap uses 'hidden.mdb.modal' not 'hidden.bs.modal'
        modal.addEventListener('hidden.mdb.modal', function() {
            // Remove focus from any element inside the modal
            const focusedElement = this.querySelector(':focus');
            if (focusedElement) {
                focusedElement.blur();
            }
        });
    });
});