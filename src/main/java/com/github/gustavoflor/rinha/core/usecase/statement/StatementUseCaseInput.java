package com.github.gustavoflor.rinha.core.usecase.statement;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StatementUseCaseInput {
    private final Integer customerId;

    public Integer customerId() {
        return customerId;
    }
}
