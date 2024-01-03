package com.ufcg.psoft.commerce.exception.Pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class PedidoNaoPertenceAoClienteException extends CommerceException {

    public PedidoNaoPertenceAoClienteException(){

        super("Pedido Nao Pertence ao Cliente");
    }
}
