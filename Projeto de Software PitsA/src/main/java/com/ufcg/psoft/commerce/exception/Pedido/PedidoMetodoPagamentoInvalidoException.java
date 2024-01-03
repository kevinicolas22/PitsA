package com.ufcg.psoft.commerce.exception.Pedido;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class PedidoMetodoPagamentoInvalidoException extends CommerceException {

    public PedidoMetodoPagamentoInvalidoException(){
        super("Metodo De Pagamento Invalido");
    }
}
