package com.ufcg.psoft.commerce.exception.Estabelecimento;

import com.ufcg.psoft.commerce.exception.CommerceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CodigoAcessoInvalidoException extends CommerceException{


    public CodigoAcessoInvalidoException(){


        super("Erros de validacao encontrados", "Codigo de acesso deve ter exatamente 6 digitos numericos");
    }

}
