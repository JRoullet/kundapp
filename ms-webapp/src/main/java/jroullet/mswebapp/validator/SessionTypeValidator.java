package jroullet.mswebapp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jroullet.mswebapp.dto.session.SessionUpdateDTO;
import jroullet.mswebapp.dto.session.create.SessionCreationDTO;

public class SessionTypeValidator implements ConstraintValidator<ValidSessionType, Object> {

    @Override
    public void initialize(ValidSessionType constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        // Update and create session dtos have the same fields to validate
        Boolean isOnline;
        String zoomLink;
        String roomName;
        String postalCode;

        //associates values based on the actual transmitted object (object may be SessionUpdateDTO or SessionCreationDTO type)
        if (obj instanceof SessionUpdateDTO dto) {
            isOnline = dto.getIsOnline();
            zoomLink = dto.getZoomLink();
            roomName = dto.getRoomName();
            postalCode = dto.getPostalCode();
        } else if (obj instanceof SessionCreationDTO dto) {
            isOnline = dto.getIsOnline();
            zoomLink = dto.getZoomLink();
            roomName = dto.getRoomName();
            postalCode = dto.getPostalCode();
        } else {
            return false;
        }

        if (isOnline == null) return false;

        if (isOnline) {
            return validateOnlineSession(zoomLink, context);
        } else {
            return validateIrlSession(roomName, postalCode, context);
        }
    }

    private boolean validateOnlineSession(String zoomLink, ConstraintValidatorContext context) {
        if (zoomLink == null || zoomLink.trim().isEmpty()) {
            addError(context, "Le lien Zoom est obligatoire pour une session en ligne");
            return false;
        }
        if (!zoomLink.matches("^https://(.*\\.)?zoom\\.(us|com)/j/\\d+.*$")) {
            addError(context, "Le lien doit être un lien Zoom valide");
            return false;
        }
        return true;
    }

    private boolean validateIrlSession(String roomName, String postalCode, ConstraintValidatorContext context) {
        if (roomName == null || roomName.trim().isEmpty()) {
            addError(context, "Le nom de la salle est obligatoire pour une session en présentiel");
            return false;
        }
        if (postalCode == null || !postalCode.matches("^(0[1-9]|[1-8][0-9]|9[0-8])\\d{3}$")) {
            addError(context, "Le code postal doit être un code postal valide");
            return false;
        }
        return true;
    }
    private void addError(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
