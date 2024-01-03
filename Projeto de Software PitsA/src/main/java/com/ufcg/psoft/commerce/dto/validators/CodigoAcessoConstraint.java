package com.ufcg.psoft.commerce.dto.validators;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CodigoAcessoValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CodigoAcessoConstraint {
    String message() default "Código de acesso inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}