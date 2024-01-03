package com.ufcg.psoft.commerce.exception.Associacao;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class EstabelecimentoIdNaoEncontradoException extends CommerceException {

    public EstabelecimentoIdNaoEncontradoException(){

        super("O estabelecimento consultado nao existe!");

    }

}
