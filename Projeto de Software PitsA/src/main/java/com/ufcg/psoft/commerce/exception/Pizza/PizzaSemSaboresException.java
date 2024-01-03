package com.ufcg.psoft.commerce.exception.Pizza;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class PizzaSemSaboresException extends CommerceException {
    public PizzaSemSaboresException() {
        super("Sabor Nao Existe");
    }

}
