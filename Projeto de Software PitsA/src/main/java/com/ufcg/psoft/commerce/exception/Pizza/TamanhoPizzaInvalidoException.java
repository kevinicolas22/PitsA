package com.ufcg.psoft.commerce.exception.Pizza;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class TamanhoPizzaInvalidoException extends CommerceException {
    public TamanhoPizzaInvalidoException() {
        super("Tamanho Invalido");
    }

}
