package com.ufcg.psoft.commerce.exception;

public class CommerceException extends RuntimeException {

    public CommerceException() {
        super("Erro inesperado no AppCommerce!");
    }

    public CommerceException(String message) {
        super(message);
    }

    public CommerceException(String mensage, String erro){
        super(mensage, new Throwable(erro));

    }

}
