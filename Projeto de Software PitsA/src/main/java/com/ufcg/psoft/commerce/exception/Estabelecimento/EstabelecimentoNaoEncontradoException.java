package com.ufcg.psoft.commerce.exception.Estabelecimento;

import com.ufcg.psoft.commerce.exception.CommerceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EstabelecimentoNaoEncontradoException  extends CommerceException {

    public EstabelecimentoNaoEncontradoException(){

        super("O estabelecimento consultado nao existe!");

    }

}
