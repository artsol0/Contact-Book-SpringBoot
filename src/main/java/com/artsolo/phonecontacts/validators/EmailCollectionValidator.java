package com.artsolo.phonecontacts.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

import java.util.Collection;

public class EmailCollectionValidator implements ConstraintValidator<EmailCollection, Collection<String>> {
    @Override
    public void initialize(EmailCollection constraintAnnotation) {}

    @Override
    public boolean isValid(Collection<String> emails, ConstraintValidatorContext context) {
        if (emails == null) {
            return true;
        }

        EmailValidator validator = new EmailValidator();
        for (String email : emails) {
            if (!validator.isValid(email, context)) {
                return false;
            }
        }

        return true;
    }
}
