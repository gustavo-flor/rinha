package com.github.gustavoflor.rinha.entrypoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.gustavoflor.rinha.core.Customer;

public record TransferResponse(@JsonProperty("limite") Integer limit,
                               @JsonProperty("saldo") Integer balance) {

    public static TransferResponse of(final Customer customer) {
        return new TransferResponse(customer.getLimit(), customer.getBalance());
    }

}
