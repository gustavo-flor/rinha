package com.github.gustavoflor.rinha.core.exception;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class InsufficientBalanceException extends ResponseStatusException {

    public InsufficientBalanceException() {
        super(UNPROCESSABLE_ENTITY);
    }

}
