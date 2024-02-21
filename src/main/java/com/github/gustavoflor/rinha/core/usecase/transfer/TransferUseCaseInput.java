package com.github.gustavoflor.rinha.core.usecase.transfer;

import com.github.gustavoflor.rinha.core.Transfer;
import com.github.gustavoflor.rinha.core.TransferType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransferUseCaseInput {

    private final Integer customerId;
    private final TransferType transferType;
    private final Integer value;
    private final String description;

    public Integer customerId() {
        return customerId;
    }

    public Transfer transfer() {
        return Transfer.builder()
            .customerId(customerId)
            .type(transferType)
            .value(value)
            .description(description)
            .build();
    }
}
