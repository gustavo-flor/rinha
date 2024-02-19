package com.github.gustavoflor.rinha.core.exception;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class LockException extends ResponseStatusException {

    public LockException(final Throwable cause) {
        super(SERVICE_UNAVAILABLE, null, cause);
    }

}
