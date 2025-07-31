package jroullet.mswebapp.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SessionTypeValidator.class)
public @interface ValidSessionType {
    String message() default "Invalid session type configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
