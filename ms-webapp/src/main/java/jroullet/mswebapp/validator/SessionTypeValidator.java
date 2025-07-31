package jroullet.mswebapp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jroullet.mswebapp.dto.session.SessionUpdateDTO;

public class SessionTypeValidator implements ConstraintValidator<ValidSessionType, SessionUpdateDTO> {

    @Override
    public void initialize(ValidSessionType constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(SessionUpdateDTO dto, ConstraintValidatorContext context) {
        if (dto.getIsOnline() == null) return false;

        if (dto.getIsOnline()) {
            return validateOnlineSession(dto, context);
        } else {
            return validateIrlSession(dto, context);
        }
    }

    private boolean validateOnlineSession(SessionUpdateDTO dto, ConstraintValidatorContext context) {
        if (dto.getZoomLink() == null || dto.getZoomLink().trim().isEmpty()) {
            addError(context, "Le lien Zoom est obligatoire pour une session en ligne");
            return false;
        }
        if (!dto.getZoomLink().matches("^https://(.*\\.)?zoom\\.(us|com)/j/\\d+.*$")) {
            addError(context, "Le lien doit être un lien Zoom valide");
            return false;
        }
        return true;
    }

    private boolean validateIrlSession(SessionUpdateDTO dto, ConstraintValidatorContext context) {
        if (dto.getRoomName() == null || dto.getRoomName().trim().isEmpty()) {
            addError(context, "Le nom de la salle est obligatoire pour une session en présentiel");
            return false;
        }
        if (dto.getPostalCode() == null || !dto.getPostalCode().matches("^(0[1-9]|[1-8][0-9]|9[0-8])\\d{3}$")) {
            addError(context, "Le code postal doit être un code postal français valide");
            return false;
        }
        return true;
    }

    private void addError(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
