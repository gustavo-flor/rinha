package com.github.gustavoflor.rinha.core.usecase.statement;

import com.github.gustavoflor.rinha.core.Customer;
import com.github.gustavoflor.rinha.core.Transfer;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class StatementUseCaseOutput {
    private final Customer customer;
    private final List<Transfer> latestTransfers;

    public Customer customer() {
        return customer;
    }

    public List<Transfer> latestTransfers() {
        return latestTransfers;
    }
}
