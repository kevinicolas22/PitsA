package com.ufcg.psoft.commerce.exception.Pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class MetodoPagamentoInvalidoException extends CommerceException {

    public MetodoPagamentoInvalidoException() {

        super("Metodo de pagamento incorreto");
    }
}