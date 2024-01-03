package com.ufcg.psoft.commerce.exception.Estabelecimento;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class CodigoAcessoEstabelecimentoException extends CommerceException{

    public CodigoAcessoEstabelecimentoException() {
        super("Codigo de acesso invalido!");
    }

}
