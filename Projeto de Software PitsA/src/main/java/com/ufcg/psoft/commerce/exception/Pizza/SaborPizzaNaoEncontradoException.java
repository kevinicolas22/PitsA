package com.ufcg.psoft.commerce.exception.Pizza;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class SaborPizzaNaoEncontradoException extends CommerceException {

    public SaborPizzaNaoEncontradoException() {
        super("O sabor consultado nao existe!");
    }
}
