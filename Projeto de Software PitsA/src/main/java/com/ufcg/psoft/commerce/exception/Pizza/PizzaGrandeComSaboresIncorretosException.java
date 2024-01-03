package com.ufcg.psoft.commerce.exception.Pizza;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class PizzaGrandeComSaboresIncorretosException extends CommerceException {
    public PizzaGrandeComSaboresIncorretosException() {
        super("So pode ter dois sabores");
    }

}
