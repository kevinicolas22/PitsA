package com.ufcg.psoft.commerce.exception.Pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class PedidoNaoCancelavelException extends CommerceException {

    public PedidoNaoCancelavelException(){
        super("Pedido não pode ser cancelado após estar pronto.");
    }

}
