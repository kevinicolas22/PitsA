package com.ufcg.psoft.commerce.dto.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CodigoAcessoValidator implements ConstraintValidator<CodigoAcessoConstraint, String> {

    @Override
    public void initialize(CodigoAcessoConstraint contactNumber) {

    }

    @Override
    public boolean isValid(String codigoAcesso,
                           ConstraintValidatorContext cxt) {
        return codigoAcesso == null || (codigoAcesso.matches("[0-9]+")
                && (codigoAcesso.length() == 6));
    }
}