package com.ufcg.psoft.commerce.exception.Pizza;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class TipoDeSaborNaoExisteException extends CommerceException {

    public TipoDeSaborNaoExisteException() {
        super("Tipo Invalido");
    }
}
