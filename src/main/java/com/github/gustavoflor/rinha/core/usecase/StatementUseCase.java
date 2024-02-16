package com.github.gustavoflor.rinha.core.usecase;

import com.github.gustavoflor.rinha.core.Customer;
import com.github.gustavoflor.rinha.core.Transfer;

import java.util.List;

public interface StatementUseCase {

    Output execute(Input input);

    record Input(Integer customerId) {
    }

    record Output(Customer customer, List<Transfer> latestTransfers) {
    }

}
