package com.ufcg.psoft.commerce.exception.Associacao;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AssociacaoNaoEncontradaException extends CommerceException {
    public AssociacaoNaoEncontradaException(){
        super("Associacao inexistente!");
    }
}
