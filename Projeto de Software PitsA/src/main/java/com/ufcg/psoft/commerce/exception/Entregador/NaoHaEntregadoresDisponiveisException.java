package com.ufcg.psoft.commerce.exception.Entregador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class NaoHaEntregadoresDisponiveisException extends CommerceException {
    public NaoHaEntregadoresDisponiveisException(){
        super("Nao ha entregadores disponiveis!");
    }
}
