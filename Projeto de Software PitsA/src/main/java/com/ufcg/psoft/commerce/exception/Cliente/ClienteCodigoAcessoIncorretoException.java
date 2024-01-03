package com.ufcg.psoft.commerce.exception.Cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ClienteCodigoAcessoIncorretoException extends CommerceException {

    public ClienteCodigoAcessoIncorretoException() {

        super("Codigo Incorreto");
    }
}