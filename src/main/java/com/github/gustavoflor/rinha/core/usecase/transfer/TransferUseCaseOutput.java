package com.github.gustavoflor.rinha.core.usecase.transfer;

import com.github.gustavoflor.rinha.core.Customer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransferUseCaseOutput {
    private final Customer customer;

    public Customer customer() {
        return customer;
    }
}
