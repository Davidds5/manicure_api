package br.com.davidds5.manicure_api.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

public class ValidationUtil {

    public static <T> void validate(Validator validator, T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Validação falhou");
            throw new IllegalArgumentException(errors);
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\$[0-9]{2}\\$ [0-9]{4,5}-[0-9]{4}");
    }
}