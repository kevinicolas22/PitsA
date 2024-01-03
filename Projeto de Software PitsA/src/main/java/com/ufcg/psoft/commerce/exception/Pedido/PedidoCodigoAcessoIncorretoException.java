package com.ufcg.psoft.commerce.exception.Pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class PedidoCodigoAcessoIncorretoException extends CommerceException {

    public PedidoCodigoAcessoIncorretoException() {

        super("Codigo Incorreto");
    }
}