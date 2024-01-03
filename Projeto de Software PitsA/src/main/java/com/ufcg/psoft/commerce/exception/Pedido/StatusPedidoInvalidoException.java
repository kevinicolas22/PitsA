package com.ufcg.psoft.commerce.exception.Pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class StatusPedidoInvalidoException extends CommerceException {

    public StatusPedidoInvalidoException(){
        super("Status do pedido invalido!");
    }
}
