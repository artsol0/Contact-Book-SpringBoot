package com.artsolo.phonecontacts.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class PhoneCollectionValidator implements ConstraintValidator<PhoneCollection, Collection<String>> {
    @Override
    public void initialize(PhoneCollection constraintAnnotation) {}

    @Override
    public boolean isValid(Collection<String> phones, ConstraintValidatorContext context) {
        if (phones == null) {
            return true;
        }
        for (String phone : phones) {
            if (phone == null || !phone.matches("^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$")) {
                return false;
            }
        }
        return true;
    }
}
