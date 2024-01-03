package com.ufcg.psoft.commerce.exception.Cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteCodigoAcessoInvalidoException extends CommerceException {
    public ClienteCodigoAcessoInvalidoException() {
        super("Código de acesso inválido");
    }
}
