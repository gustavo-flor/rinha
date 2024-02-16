package com.github.gustavoflor.rinha.core.exception;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class ServiceUnavailableException extends ResponseStatusException {

    public ServiceUnavailableException(final Throwable cause) {
        super(SERVICE_UNAVAILABLE, null, cause);
    }

}
