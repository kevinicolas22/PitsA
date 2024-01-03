package com.ufcg.psoft.commerce.exception.Entregador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class CodigoAcessoEntregadorException extends CommerceException {
    public CodigoAcessoEntregadorException() {
        super("Codigo de acesso invalido!");
    }
}
