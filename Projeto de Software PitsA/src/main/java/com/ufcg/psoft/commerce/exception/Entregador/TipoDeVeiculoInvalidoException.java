package com.ufcg.psoft.commerce.exception.Entregador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class TipoDeVeiculoInvalidoException extends CommerceException{
    public TipoDeVeiculoInvalidoException(){
        super("Tipo de veiculo deve ser carro ou moto");
    }
    
}
