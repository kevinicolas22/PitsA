package com.ufcg.psoft.commerce.exception.Pizza;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class DisponibilidadeSaborException extends CommerceException {
    public DisponibilidadeSaborException() {
        super("O sabor consultado ja esta disponivel/indisponivel!");
    }


}
